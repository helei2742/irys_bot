package cn.com.vortexa.irys_onchain_bot.service.impl;


import cn.com.vortexa.irys_onchain_bot.constants.IrysConstants;
import cn.com.vortexa.irys_onchain_bot.exception.OnChainException;
import cn.com.vortexa.irys_onchain_bot.onchain.DynamicGasProvider;
import cn.com.vortexa.irys_onchain_bot.onchain.execute.ExecutorGuardedIntent;
import cn.com.vortexa.irys_onchain_bot.onchain.node.NodeRegistry;
import cn.com.vortexa.irys_onchain_bot.service.IrysOnChainService;
import cn.com.vortexa.web3.EthWalletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

/**
 * @author helei
 * @since 2025-09-19
 */
@Slf4j
@Service
public class IrysOnChainServiceImpl implements IrysOnChainService {

    @Lazy
    @Autowired
    private ExecutorGuardedIntent executorGuardedIntent;

    @Override
    public String deployExecutorGuardedIntentContract(String privateKey) throws OnChainException {
        try {
            Credentials credentials = Credentials.create(privateKey);

            Web3j web3j = EthWalletUtil.getRpcWeb3j(IrysConstants.IRYS_CHAIN_INFO.getRpcUrl());
            DynamicGasProvider gasProvider = new DynamicGasProvider(web3j, credentials.getAddress());
            ExecutorGuardedIntent contract = ExecutorGuardedIntent.deploy(
                    web3j,
                    credentials,
                    gasProvider
            ).send();

            return contract.getContractAddress();
        } catch (Exception e) {
            throw new OnChainException("deploy ExecutorGuardedIntent contract error", e);
        }
    }

    @Override
    public String deployNodeRegistryContract(String privateKey) throws OnChainException {
        try {
            Credentials credentials = Credentials.create(privateKey);

            Web3j web3j = EthWalletUtil.getRpcWeb3j(IrysConstants.IRYS_CHAIN_INFO.getRpcUrl());
            DynamicGasProvider gasProvider = new DynamicGasProvider(web3j, credentials.getAddress());
            NodeRegistry contract = NodeRegistry.deploy(
                    web3j,
                    credentials,
                    gasProvider
            ).send();

            return contract.getContractAddress();
        } catch (Exception e) {
            throw new OnChainException("deploy NodeRegistry contract error", e);
        }
    }

    @Override
    public ExecutorGuardedIntent.IntentData tryGetOnChainIntent(byte[] intentId, String intentIdStr, String address) throws OnChainException {
        // 1 claim intent
        String claimTX = claimIntent(intentId, intentIdStr, address);
        log.info("claim intent[{}] success...txHash:[{}]", intentIdStr, claimTX);
        // 2 get intent
        ExecutorGuardedIntent.IntentData intentData = getOnChainIntent(intentId, intentIdStr);
        log.info("intent[{}] data get success...", intentIdStr);
        return intentData;
    }

    @Override
    public String startExecuteIntent(String intentIdStr, String address) throws OnChainException {
        TransactionReceipt receipt;
        try {
            log.info("on chain start execute intent[{}]...", intentIdStr);
            receipt = executorGuardedIntent.startExecuteIntent(toBytes32(intentIdStr), address).send();
            if (!EthWalletUtil.waitForTransactionReceipt(
                    IrysConstants.IRYS_CHAIN_INFO.getRpcUrl(),
                    receipt.getTransactionHash()
            ).isStatusOK()) {
                throw new OnChainException("fail to execute intent...");
            }
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new OnChainException(e);
        }
    }

    @Override
    public String completeExecuteIntent(String intentIdStr, String address) throws OnChainException {
        try {
            log.info("on chain complete execute intent[{}]...", intentIdStr);
            TransactionReceipt receipt = executorGuardedIntent.completeExecuteIntent(
                    toBytes32(intentIdStr), address
            ).send();
            if (!EthWalletUtil.waitForTransactionReceipt(
                    IrysConstants.IRYS_CHAIN_INFO.getRpcUrl(),
                    receipt.getTransactionHash()
            ).isStatusOK()) {
                throw new OnChainException("fail to execute intent...");
            }
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new OnChainException(e);
        }
    }

    @Override
    public String commitError(String intentIdStr, String address) throws OnChainException {
        try {
            log.info("on chain commit error[{}]...", intentIdStr);
            TransactionReceipt receipt = executorGuardedIntent.commitError(toBytes32(intentIdStr), address).send();
            if (!EthWalletUtil.waitForTransactionReceipt(
                    IrysConstants.IRYS_CHAIN_INFO.getRpcUrl(),
                    receipt.getTransactionHash()
            ).isStatusOK()) {
                throw new OnChainException("fail to execute intent...");
            }
            return receipt.getTransactionHash();
        } catch (Exception e) {
            throw new OnChainException(e);
        }
    }

    private ExecutorGuardedIntent.IntentData getOnChainIntent(byte[] intentId, String intentIdStr) throws OnChainException {
        try {
            log.info("try get intent[{}] data...", intentIdStr);
            return executorGuardedIntent.getIntent(intentId).send();
        } catch (Exception e) {
            throw new OnChainException("get intent fail", e);
        }
    }

    private String claimIntent(byte[] intentId, String intentIdStr, String address) throws OnChainException {
        try {
            log.info("try to claim intent[{}]...", intentIdStr);
            TransactionReceipt claimReceipt = executorGuardedIntent.claimIntent(intentId, address).send();
            if (!EthWalletUtil.waitForTransactionReceipt(
                    IrysConstants.IRYS_CHAIN_INFO.getRpcUrl(),
                    claimReceipt.getTransactionHash()
            ).isStatusOK()) {
                throw new OnChainException("fail to claim intent...");
            }
            return claimReceipt.getTransactionHash();
        } catch (Exception e) {
            throw new OnChainException("claim intent fail", e);
        }
    }

    public static byte[] toBytes32(String hexStr) {
        // 去掉 0x 前缀
        if (hexStr.startsWith("0x")) {
            hexStr = hexStr.substring(2);
        }

        byte[] bytes = Numeric.hexStringToByteArray("0x" + hexStr);
        if (bytes.length > 32) {
            throw new IllegalArgumentException("Input too long for bytes32");
        }

        byte[] bytes32 = new byte[32];
        // 左补零
        System.arraycopy(bytes, 0, bytes32, 32 - bytes.length, bytes.length);
        return bytes32;
    }
}

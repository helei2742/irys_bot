package cn.com.vortexa.irys_onchain_bot.onchain;

import cn.com.vortexa.irys_onchain_bot.onchain.execute.ExecutorGuardedIntent;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.request.Transaction;

import java.math.BigInteger;

public class DynamicGasProvider implements ContractGasProvider {

    private final Web3j web3j;
    private final String fromAddress;

    // 构造函数传入 Web3j 和部署者地址
    public DynamicGasProvider(Web3j web3j, String fromAddress) {
        this.web3j = web3j;
        this.fromAddress = fromAddress;
    }

    @Override
    public BigInteger getGasPrice(String contractFunc) {
        try {
            EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
            BigInteger gasPrice = ethGasPrice.getGasPrice();
            // 可乘 1.2 提升优先级
            return gasPrice.multiply(BigInteger.valueOf(12)).divide(BigInteger.valueOf(10));
        } catch (Exception e) {
            // 出错时返回默认值
            return BigInteger.valueOf(10_000_000_000L); // 10 Gwei
        }
    }

    @Override
    public BigInteger getGasLimit(String contractFunc) {
        try {
            // 可以调用 ethEstimateGas 进行估算
            EthEstimateGas estimateGas = web3j.ethEstimateGas(
                    Transaction.createContractTransaction(
                            fromAddress,
                            null,
                            null,
                            null,
                            BigInteger.ZERO,
                            ExecutorGuardedIntent.BINARY
                    )
            ).send();
            BigInteger gasLimit = estimateGas.getAmountUsed();
            // 留点余量
            return gasLimit.multiply(BigInteger.valueOf(2));
        } catch (Exception e) {
            return BigInteger.valueOf(5_000_000); // fallback
        }
    }

    @Override
    public BigInteger getGasPrice() {
        return getGasPrice("");
    }

    @Override
    public BigInteger getGasLimit() {
        return getGasLimit("");
    }
}

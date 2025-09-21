package cn.com.vortexa.irys_onchain_bot.onchain.node;


import cn.com.vortexa.bot_template.bot.VortexaBotContext;
import cn.com.vortexa.common.util.CastUtil;
import cn.com.vortexa.irys_onchain_bot.constants.IrysConstants;
import cn.com.vortexa.irys_onchain_bot.onchain.DynamicGasProvider;
import cn.com.vortexa.irys_onchain_bot.service.IrysOnChainService;
import cn.com.vortexa.web3.EthWalletUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cn.com.vortexa.irys_onchain_bot.constants.IrysConstants.*;
import static cn.com.vortexa.irys_onchain_bot.constants.IrysCustomConfigKey.DEPLOY_PRIVATE_KEY;

/**
 * @author helei
 * @since 2025-09-19
 */
@Slf4j
@Component
public class NodeRegistryFactoryBean extends AbstractFactoryBean<NodeRegistry> {

    @Autowired
    private VortexaBotContext botContext;

    @Autowired
    private IrysOnChainService irysOnChainService;

    @Override
    public Class<?> getObjectType() {
        return NodeRegistry.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @NotNull
    @Override
    protected NodeRegistry createInstance() throws Exception {
        String privateKey = CastUtil.autoCast(botContext.getCustomConfig().getKeyValues()
                .get(DEPLOY_PRIVATE_KEY));

        if (StrUtil.isBlank(privateKey)) {
            throw new IllegalArgumentException("contract deployer PK is empty");
        }

        String contractAddress = readFileContractAddress();
        if (StrUtil.isBlank(contractAddress)) {
            log.warn("node-registry-address not found, try to deploy new one...");
            contractAddress = irysOnChainService.deployNodeRegistryContract(privateKey);
            if (StrUtil.isBlank(contractAddress)) {
                throw new RuntimeException("deploy node-registry-address failed");
            } else {
                saveContractAddress(contractAddress);
                log.info("deploy node-registry-address successful, address: {}", contractAddress);
            }
        }
        botContext.getCustomConfig().getKeyValues().put(IRYS_NODE_CONTRACT_ADDRESS, contractAddress);
        botContext.getCustomConfig().getKeyValues().put(IRYS_NODE_CONTRACT_ABI, loadAbiForFile());

        Web3j web3j = EthWalletUtil.getRpcWeb3j(IrysConstants.IRYS_CHAIN_INFO.getRpcUrl());
        Credentials credentials = Credentials.create(privateKey);
        DynamicGasProvider gasProvider = new DynamicGasProvider(web3j, credentials.getAddress());
        NodeRegistry load = NodeRegistry.load(
                contractAddress,
                web3j,
                credentials,
                gasProvider
        );
        log.info("load node-registry-address successful, address: {}", contractAddress);
        return load;
    }

    private String loadAbiForFile() throws IOException {
        Path path = Paths.get(System.getProperty("user.dir"), "node-registry.abi");
        if (Files.notExists(path)) {
            throw new RuntimeException("node-registry.abi not found");
        }

        return Files.readString(path);
    }

    private void saveContractAddress(String contractAddress) {
        Path path = Paths.get(System.getProperty("user.dir"), "node-registry-address.txt");

        if (Files.notExists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toFile()))) {
            bw.write(contractAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readFileContractAddress() {
        Path path = Paths.get(System.getProperty("user.dir"), "node-registry-address.txt");
        if (Files.exists(path)) {
            try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
                return br.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }
}

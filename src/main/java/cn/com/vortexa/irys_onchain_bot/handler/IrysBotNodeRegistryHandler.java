package cn.com.vortexa.irys_onchain_bot.handler;


import cn.com.vortexa.bot_template.bot.VortexaBotContext;
import cn.com.vortexa.common.util.CastUtil;
import cn.com.vortexa.irys_onchain_bot.constants.IrysConstants;
import cn.com.vortexa.irys_onchain_bot.onchain.node.NodeRegistry;
import cn.com.vortexa.web3.EthWalletUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;

import static cn.com.vortexa.irys_onchain_bot.constants.IrysCustomConfigKey.DEPLOY_ADDRESS;
import static cn.com.vortexa.irys_onchain_bot.constants.IrysCustomConfigKey.DEPLOY_PRIVATE_KEY;

/**
 * @author helei
 * @since 2025-09-21
 */
@Slf4j
@Order(0)
@Component
public class IrysBotNodeRegistryHandler  implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private VortexaBotContext vortexaBotContext;

    @Autowired
    private NodeRegistry nodeRegistry;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            String deployAddress = CastUtil.autoCast(vortexaBotContext.getCustomConfig().getKeyValues().get(DEPLOY_ADDRESS));
            log.info("bot node deployed at [{}]", deployAddress);
            String pk = CastUtil.autoCast(vortexaBotContext.getCustomConfig().getKeyValues().get(DEPLOY_PRIVATE_KEY));
            String ethAddress = EthWalletUtil.getETHAddress(pk);

            String url = "http://" + deployAddress + "/app/ui/index.html";
            // 1 查询是否注册
            if (nodeRegistry.isRegistered(ethAddress).send()) {
                Tuple2<String, Boolean> nodeUrl = nodeRegistry.getNode(ethAddress).send();
                log.info("bot node registered, {}", nodeUrl);
                if (!nodeUrl.component2() || !url.equals(nodeUrl.component1())) {
                    log.info("bot node url need update, {}", url);
                    TransactionReceipt receipt = nodeRegistry.updateNodeUrl(url).send();
                    if (EthWalletUtil.waitForTransactionReceipt(
                            IrysConstants.IRYS_CHAIN_INFO.getRpcUrl(),
                            receipt.getTransactionHash()
                    ).isStatusOK()) {
                        log.info("bot node update url success at [{}]", url);
                    } else {
                        log.warn("bot node failed to update url [{}]", url);
                        System.exit(-1);
                    }
                }
            } else {
                log.warn("bot node url not registry, try to register node...");
                TransactionReceipt receipt = nodeRegistry.registerNode(url).send();
                if (EthWalletUtil.waitForTransactionReceipt(
                        IrysConstants.IRYS_CHAIN_INFO.getRpcUrl(),
                        receipt.getTransactionHash()
                ).isStatusOK()) {
                    log.info("bot node registered success at [{}]", url);
                } else {
                    log.warn("bot node failed to register at [{}]", url);
                    System.exit(-1);
                }
            }
        } catch (Exception e) {
            log.error("get bot node server address error", e);
        }
    }
}

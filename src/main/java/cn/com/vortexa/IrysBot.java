package cn.com.vortexa;


import cn.com.vortexa.account.entity.Web3Wallet;
import cn.com.vortexa.base.util.log.AppendLogger;
import cn.com.vortexa.bot_template.bot.AbstractVortexaBot;
import cn.com.vortexa.bot_template.bot.VortexaBotContext;
import cn.com.vortexa.bot_template.bot.anno.VortexaBot;
import cn.com.vortexa.bot_template.bot.anno.VortexaBotAPI;
import cn.com.vortexa.bot_template.bot.anno.VortexaBotCatalogueGroup;
import cn.com.vortexa.bot_template.constants.VortexaBotApiSchedulerType;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * @author helei
 * @since 2025-09-17
 */
@Slf4j
@VortexaBot(
        namespace = "irys",
        websiteUrl = "https://irys.xyz",
        catalogueGroup = {
                @VortexaBotCatalogueGroup(name = "Wallet Opt", order = 1),
                @VortexaBotCatalogueGroup(name = "Game", order = 2)
        }
)
public class IrysBot extends AbstractVortexaBot {

    private final IrysBotApi botApi;

    public IrysBot(VortexaBotContext vortexaBotContext) {
        super(vortexaBotContext);
        this.botApi = new IrysBotApi(this);
    }

    @VortexaBotAPI(
            name = "Faucet irys",
            catalogueName = "Wallet Opt",
            schedulerType = VortexaBotApiSchedulerType.INTERVAL,
            catalogueOrder = 1
    )
    public void faucet() {
        if (StrUtil.isBlank(botApi.getTwoCaptchaApiKey())) {
            throw new IllegalArgumentException("twoCaptchaApiKey is blank");
        }

        forEachAccountContext((pageResult, i, fullAccountContext) -> {
            botApi.faucet(fullAccountContext);
        });
    }

    @VortexaBotAPI(
            name = "Deposit game balance",
            catalogueName = "Wallet Opt",
            schedulerType = VortexaBotApiSchedulerType.NONE,
            catalogueOrder = 2
    )
    public void depositGameBalance(Double amount) {
        AppendLogger logger = getBotMethodInvokeContext().getLogger();

        if (amount == null || amount <= 0) {
            logger.error("invalid param amount, " + amount);
            return;
        }
        forEachAccountContext((pageResult, i, fullAccountContext) -> {
            Web3Wallet wallet = fullAccountContext.getWallet();
            try {
                logger.debug("account[%s] deposit game wallet balance[%s]..".formatted(
                        fullAccountContext.getId(), amount
                ));
                String txHash = botApi.depositGameBalance(wallet.getEthPrivateKey(), wallet.getEthAddress(), amount);
                logger.info("account[%s] deposit game wallet balance[%s] success, txHash:[%s]".formatted(
                        fullAccountContext.getId(), amount, txHash
                ));
            } catch (Exception e) {
                logger.error("account[%s] deposit game wallet balance[%s] error, ".formatted(
                        fullAccountContext.getId(), amount
                ), e.getCause() == null ? e : e.getCause());
            }
        });
    }

    @VortexaBotAPI(
            name = "Withdraw game balance",
            catalogueName = "Wallet Opt",
            schedulerType = VortexaBotApiSchedulerType.NONE,
            catalogueOrder = 3
    )
    public void withdrawGameBalance(Double amount) {
        AppendLogger logger = getBotMethodInvokeContext().getLogger();

        if (amount == null || amount <= 0) {
            logger.error("invalid param amount , " + amount);
            return;
        }

        forEachAccountContext((pageResult, i, fullAccountContext) -> {
            try {
                logger.debug("account[%s] withdraw game wallet balance[%s]..".formatted(
                        fullAccountContext.getId(), amount
                ));

                Web3Wallet wallet = fullAccountContext.getWallet();
                String txHash = botApi.withdrawGameBalance(wallet.getEthPrivateKey(), wallet.getEthAddress(), amount);
                logger.info("account[%s] withdraw game wallet balance[%s] success, txHash:[%s]".formatted(
                        fullAccountContext.getId(), amount, txHash
                ));
            } catch (Exception e) {
                logger.error("account[%s] withdraw game wallet balance[%s] error, ".formatted(
                        fullAccountContext.getId(), amount
                ), e.getCause() == null ? e : e.getCause());
            }
        });
    }


    @VortexaBotAPI(
            name = "Query irys balance",
            catalogueName = "Wallet Opt",
            schedulerType = VortexaBotApiSchedulerType.NONE,
            catalogueOrder = 4
    )
    public void walletIrysBalanceQuery() {
        AppendLogger logger = getBotMethodInvokeContext().getLogger();
        forEachAccountContext((pageResult, i, fullAccountContext) -> {
            try {
                logger.debug("account[%s] query wallet irys balance..".formatted(
                        fullAccountContext.getId()
                ));

                BigDecimal balance = botApi.queryWalletBalance(fullAccountContext);
                logger.info("account[%s] wallet irys balance[%s]".formatted(
                        fullAccountContext.getId(), balance.doubleValue()
                ));
            } catch (Exception e) {
                logger.error("account[%s] query wallet irys balance error, ".formatted(
                        fullAccountContext.getId()
                ), e.getCause() == null ? e : e.getCause());
            }
        });
    }

    @VortexaBotAPI(
            name = "Query game wallet balance",
            catalogueName = "Wallet Opt",
            schedulerType = VortexaBotApiSchedulerType.NONE,
            catalogueOrder = 5
    )
    public void gameWalletBalanceQuery() {
        AppendLogger logger = getBotMethodInvokeContext().getLogger();

        forEachAccountContext((pageResult, i, fullAccountContext) -> {
            try {
                logger.debug("account[%s] query game wallet balance..".formatted(
                        fullAccountContext.getId()
                ));
                BigDecimal balance = botApi.queryGameInnerWalletBalance(fullAccountContext);
                logger.info("account[%s] game wallet balance[%s]".formatted(
                        fullAccountContext.getId(), balance.doubleValue()
                ));
            } catch (Exception e) {
                logger.error("account[%s] query game wallet balance error, ".formatted(
                        fullAccountContext.getId()
                ), e.getCause() == null ? e : e.getCause());
            }
        });
    }

    @VortexaBotAPI(
            name = "Snake Game",
            catalogueName = "Game",
            schedulerType = VortexaBotApiSchedulerType.ALL,
            description = "请编辑参数。参数1: 最小积分次数; 参数2:最大积分次数; 参数3: 积分乘数; 参数4: 基础分。比如：[30,50,10,100]，就代表了分数范围在400-600",
            catalogueOrder = 1
    )
    public void snakeGame(int minBase, int maxBase, int multiply, int base) {
        AppendLogger logger = getBotMethodInvokeContext().getLogger();
        forEachAccountContext((pageResult, i, fullAccountContext) -> {
            try {
                botApi.playGame(fullAccountContext, IrysGameType.SNAKE, base, minBase, maxBase, multiply);
            } catch (Exception e) {
                logger.error("account[%s] play snake game error, ".formatted(
                        fullAccountContext.getId()
                ), e.getCause() == null ? e : e.getCause());
            }
        });
    }

    @VortexaBotAPI(
            name = "Asteroids Game",
            catalogueName = "Game",
            schedulerType = VortexaBotApiSchedulerType.ALL,
            description = "请编辑参数。参数1: 最小积分次数; 参数2:最大积分次数; 参数3: 积分乘数; 参数4: 基础分。比如：[30,50,10,100]，就代表了分数范围在400-600",
            catalogueOrder = 2
    )
    public void asteroidsGame(int minBase, int maxBase, int multiply, int base) {
        AppendLogger logger = getBotMethodInvokeContext().getLogger();
        forEachAccountContext((pageResult, i, fullAccountContext) -> {
            try {
                botApi.playGame(fullAccountContext, IrysGameType.ASTEROIDS, base, minBase, maxBase, multiply);
            } catch (Exception e) {
                logger.error("account[%s] play asteroids game error, ".formatted(
                        fullAccountContext.getId()
                ), e.getCause() == null ? e : e.getCause());
            }
        });
    }

    @VortexaBotAPI(
            name = "Hexshot Game",
            catalogueName = "Game",
            schedulerType = VortexaBotApiSchedulerType.ALL,
            description = "请编辑参数。参数1: 最小积分次数; 参数2:最大积分次数; 参数3: 积分乘数; 参数4: 基础分。比如：[30,50,10,100]，就代表了分数范围在400-600",
            catalogueOrder = 3
    )
    public void hexshotGame(int minBase, int maxBase, int multiply, int base) {
        AppendLogger logger = getBotMethodInvokeContext().getLogger();
        forEachAccountContext((pageResult, i, fullAccountContext) -> {
            try {
                botApi.playGame(fullAccountContext, IrysGameType.HEX_SHOOTER, base, minBase, maxBase, multiply);
            } catch (Exception e) {
                logger.error("account[%s] play hexshot game error, ".formatted(
                        fullAccountContext.getId()
                ), e.getCause() == null ? e : e.getCause());
            }
        });
    }

    @VortexaBotAPI(
            name = "Missile Game",
            catalogueName = "Game",
            schedulerType = VortexaBotApiSchedulerType.ALL,
            description = "请编辑参数。参数1: 最小积分次数; 参数2:最大积分次数; 参数3: 积分乘数; 参数4: 基础分。比如：[30,50,10,100]，就代表了分数范围在400-600",
            catalogueOrder = 4
    )
    public void missileGame(int minBase, int maxBase, int multiply, int base) {
        AppendLogger logger = getBotMethodInvokeContext().getLogger();
        forEachAccountContext((pageResult, i, fullAccountContext) -> {
            try {
                botApi.playGame(fullAccountContext, IrysGameType.MISSILE_COMMAND, base, minBase, maxBase, multiply);
            } catch (Exception e) {
                logger.error("account[%s] play missile game error, ".formatted(
                        fullAccountContext.getId()
                ), e.getCause() == null ? e : e.getCause());
            }
        });
    }
}

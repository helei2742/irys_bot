package cn.com.vortexa;

import cn.com.vortexa.base.constants.HeaderKey;
import cn.com.vortexa.base.util.log.AppendLogger;
import cn.com.vortexa.bot_template.bot.dto.FullAccountContext;
import cn.com.vortexa.bot_template.exception.BotInvokeException;
import cn.com.vortexa.captcha.CaptchaResolver;
import cn.com.vortexa.common.constants.ChainType;
import cn.com.vortexa.common.constants.HttpMethod;
import cn.com.vortexa.common.constants.NetType;
import cn.com.vortexa.common.util.CastUtil;
import cn.com.vortexa.common.util.http.RestApiClientFactory;
import cn.com.vortexa.web3.EthWalletUtil;
import cn.com.vortexa.web3.constants.Web3jFunctionType;
import cn.com.vortexa.web3.dto.SCInvokeParams;
import cn.com.vortexa.web3.dto.WalletInfo;
import cn.com.vortexa.web3.dto.Web3ChainInfo;
import cn.com.vortexa.web3.exception.ABIInvokeException;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author helei
 * @since 2025-09-17
 */
public class IrysBotApi {
    private static final String GAME_WALLET_BALANCE_KEY = "game_wallet_balance";
    private static final String WALLET_BALANCE_KEY = "wallet_balance";

    private static final String FAUCET_URL = "https://irys.xyz/api/faucet";
    private static final String FAUCET_SITE_KEY = "0x4AAAAAAA6vnrvBCtS4FAl-";
    private static final String BASE_URL = "https://play.irys.xyz/api";
    private static final String IRYS_GAME_START_SIGN_MESSAGE = """
            I authorize payment of %s IRYS to play a game on Irys Arcade.
               \s
            Player: %s
            Amount: %s IRYS
            Timestamp: %s
            
            This signature confirms I own this wallet and authorize the payment.
            """;
    private static final String IRYS_GAME_COMPLETE_SIGN_MESSAGE = """
            I completed a %s game on Irys Arcade.
               \s
            Player: %s
            Game: %s
            Score: %s
            Session: %s
            Timestamp: %s
            
            This signature confirms I own this wallet and completed this game.
            """;

    private static final String RANDOM_STR_TEM = "1234567890abcdefghijklmnopqrstuvwxyz";
    private static final String BALANCE_DEPOSIT_ADDRESS = "0xBC41F2B6BdFCB3D87c3d5E8b37fD02C56B69ccaC";
    private static final Web3ChainInfo IRYS_CHAIN_INFO = Web3ChainInfo
            .builder()
            .name("Irys Testnet")
            .rpcUrls(List.of("https://testnet-rpc.irys.xyz/v1/execution-rpc"))
            .chainId(1270)
            .chainType(ChainType.EVM)
            .netType(NetType.Test_net)
            .originTokenSymbol("IRYS")
            .blockExploreUrl("https://explorer.irys.xyz")
            .build();

    private final IrysBot bot;
    @Getter
    private final String twoCaptchaApiKey;

    public IrysBotApi(IrysBot bot) {
        this.bot = bot;
        this.twoCaptchaApiKey = CastUtil.autoCast(
                bot.getVortexaBotContext().getCustomConfig().getKeyValues().get("two-captcha-api-key")
        );
    }

    /**
     * 领水
     *
     * @param accountContext    accountContext
     */
    public void faucet(FullAccountContext accountContext) {
        if (StrUtil.isBlank(twoCaptchaApiKey)) {
            throw new IllegalArgumentException("twoCaptchaApiKey is blank");
        }
        AppendLogger logger = bot.getBotMethodInvokeContext().getLogger();
        logger.debug("start faucet...");

        Map<String, String> headers = accountContext.buildHeader();
        headers.put(HeaderKey.REFERER, "https://irys.xyz/faucet");
        headers.put(HeaderKey.ORIGIN, "https://irys.xyz");
        headers.put(HeaderKey.CONTENT_TYPE, "application/json");

        try {
            logger.debug("getting faucet cf token...");
            String token = CaptchaResolver.cloudFlareResolve(
                    accountContext.getProxy(),
                    FAUCET_URL,
                    FAUCET_SITE_KEY,
                    twoCaptchaApiKey
            ).get();
            logger.debug("faucet cf token[%s]...".formatted(token.substring(0, 18)));

            JSONObject jsonObject = RestApiClientFactory.getClient(accountContext.getProxy()).jsonRequest(
                    FAUCET_URL,
                    HttpMethod.POST,
                    headers,
                    null,
                    new JSONObject(Map.of(
                            "captchaToken", token,
                            "walletAddress", accountContext.getWallet().getEthAddress()
                    ))
            ).get();
            logger.debug("faucet finish, %s".formatted(jsonObject));
        } catch (Exception e) {
            logger.error("faucet error", e.getCause() == null ? e : e.getCause());
        }
    }

    /**
     * 查询游戏内置钱包余额
     *
     * @param fullAccountContext    fullAccountContext
     * @return BigDecimal
     * @throws BotInvokeException   BotInvokeException
     */
    public BigDecimal queryGameInnerWalletBalance(FullAccountContext fullAccountContext) throws BotInvokeException {
        String selector = "47734892"; // balanceOf(address)
        String wallet = fullAccountContext.getWallet().getEthAddress().toLowerCase();
        if (wallet.startsWith("0x")) {
            wallet = wallet.substring(2);
        }
        String paddedAddress = String.format("%024x%s", 0, wallet.toLowerCase());
        String data = "0x" + selector + paddedAddress;
        try {
            JSONObject result = RestApiClientFactory.getClient(fullAccountContext.getProxy()).jsonRequest(
                    "https://testnet-rpc.irys.xyz/v1/execution-rpc",
                    HttpMethod.POST,
                    fullAccountContext.buildHeader(),
                    null,
                    new JSONObject(Map.of(
                            "id", 0,
                            "jsonrpc", "2.0",
                            "method", "eth_call",
                            "params", List.of(
                                    Map.of("to", BALANCE_DEPOSIT_ADDRESS.toLowerCase(), "data", data),
                                    "latest"
                            )
                    ))
            ).get();
            Object error = result.get("error");
            if (error != null) {
                throw new BotInvokeException(error.toString());
            }
            String s = result.getString("result").substring(2);
            BigInteger raw = new BigInteger(s, 16);

            BigDecimal bigDecimal = EthWalletUtil.formatUnits(raw, 18);
            fullAccountContext.putParam(GAME_WALLET_BALANCE_KEY, bigDecimal.doubleValue());
            return bigDecimal;
        } catch (InterruptedException | ExecutionException e) {
            throw new BotInvokeException("query game inner wallet balance fail, " + e.getMessage());
        }
    }

    /**
     * 查询钱包余额
     *
     * @param fullAccountContext    fullAccountContext
     * @return BigDecimal
     * @throws ABIInvokeException   ABIInvokeException
     */
    public BigDecimal queryWalletBalance(FullAccountContext fullAccountContext) throws ABIInvokeException {
        BigDecimal balance = EthWalletUtil.getOriginTokenBalance(
                IRYS_CHAIN_INFO.getRpcUrl(),
                fullAccountContext.getWallet().getEthAddress()
        );
        fullAccountContext.putParam(WALLET_BALANCE_KEY, balance.doubleValue());
        return balance;
    }

    /**
     * 玩游戏
     *
     * @param fullAccountContext fullAccountContext
     * @param gameType           gameType
     * @param base              基础分
     * @param minBase            最小
     * @param maxBase            最大
     * @param multiply           乘数
     * @throws BotInvokeException BotInvokeException
     */
    public void playGame(
            FullAccountContext fullAccountContext,
            String gameType,
            int base,
            int minBase,
            int maxBase,
            int multiply
    ) throws BotInvokeException, InterruptedException {
        AppendLogger logger = bot.getBotMethodInvokeContext().getLogger();

        String sessionId = startIrysGame(fullAccountContext, getGameCost(gameType), gameType);
        int score = RandomUtil.randomInt(minBase, maxBase) * multiply + base;

        if (!checkScoreCanCompleteAble(gameType, score)) {
            logger.warn("score[%s] cannot complete game[%s]...".formatted(score, gameType));
            return;
        }

        long dynamicWait = dynamicCalWait(gameType, score);
        logger.debug("wait %s(s) to complete game[%s], score[%s]，execute on: %s".formatted(
                dynamicWait, sessionId, score, LocalDateTime.now().plusSeconds(dynamicWait)
        ));
        TimeUnit.SECONDS.sleep(dynamicWait);

        Throwable throwException = null;
        for (int i = 0; i < 3; i++) {
            try {
                completeIrysGame(fullAccountContext, gameType, sessionId, score);
                throwException = null;
                break;
            } catch (Exception e) {
                logger.warn("complete game fail，retry...");
                throwException = e.getCause() == null ? e : e.getCause();
            }
        }
        if (throwException != null) {
            throw new BotInvokeException(throwException);
        }
    }


    /**
     * 开始游戏
     *
     * @param fullAccountContext    fullAccountContext
     * @param cost  cost
     * @param gameType  gameType
     * @return CompletableFuture<JSONObject>
     */
    private String startIrysGame(
            FullAccountContext fullAccountContext, double cost, String gameType
    ) throws BotInvokeException {
        AppendLogger logger = bot.getBotMethodInvokeContext().getLogger();
        logger.debug("send play game[%s] cost[%s] request".formatted(
                gameType, cost
        ));

        long timestamp = System.currentTimeMillis();
        String ethAddress = fullAccountContext.getWallet().getEthAddress();
        String message = generateStartGameSignMessage(ethAddress, cost, timestamp);

        JSONObject body = new JSONObject(Map.of(
                "playerAddress", ethAddress,
                "gameCost", cost,
                "signature", EthWalletUtil.signatureMessage2String(
                        fullAccountContext.getWallet().getEthPrivateKey(), message
                ),
                "message", message,
                "timestamp", timestamp,
                "sessionId", generateIrysGameSessionId(timestamp),
                "gameType", gameType
        ));
        try {
            JSONObject result = RestApiClientFactory.getClient(fullAccountContext.getProxy()).request(
                    BASE_URL + "/game/start",
                    HttpMethod.POST,
                    generateHeader(fullAccountContext, convertHeaderGameType(gameType)),
                    null,
                    body,
                    3
            ).thenApply(JSONObject::parseObject).get();
            if (result != null && result.getBoolean("success")) {
                logger.info("start play game[%s], tsHash[%s] - sessionId[%s]".formatted(
                        gameType,
                        result.getJSONObject("data").getString("transactionHash"),
                        result.getJSONObject("data").getString("sessionId")
                ));
                return result.getJSONObject("data").getString("sessionId");
            } else {
                throw new BotInvokeException("start error, " + result);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new BotInvokeException(e);
        }
    }

    /**
     * 完成游戏
     *
     * @param fullAccountContext    fullAccountContext
     * @param gameType  gameType
     * @param sessionId sessionId
     * @param score score
     * @throws BotInvokeException   BotInvokeException
     */
    private void completeIrysGame(
            FullAccountContext fullAccountContext, String gameType, String sessionId, int score
    ) throws BotInvokeException {
        AppendLogger logger = bot.getBotMethodInvokeContext().getLogger();
        logger.debug("send complete game[%s] request, score[%s]".formatted(
               gameType, score
        ));

        long timestamp = System.currentTimeMillis();
        String ethAddress = fullAccountContext.getWallet().getEthAddress();
        String message = generateCompleteGameMessage(gameType, ethAddress, score, sessionId, timestamp);
        JSONObject body = new JSONObject(Map.of(
                "gameType", gameType,
                "message", message,
                "playerAddress", ethAddress,
                "score", score,
                "sessionId", sessionId,
                "signature", EthWalletUtil.signatureMessage2String(
                        fullAccountContext.getWallet().getEthPrivateKey(), message
                ),
                "timestamp", timestamp
        ));

        try {
            JSONObject result = RestApiClientFactory.getClient(fullAccountContext.getProxy()).request(
                    BASE_URL + "/game/complete",
                    HttpMethod.POST,
                    generateHeader(fullAccountContext, convertHeaderGameType(gameType)),
                    null,
                    body,
                    3
            ).thenApply(JSONObject::parseObject).get();
            if (result != null && result.getBoolean("success")) {
                logger.info("complete game[%s], tsHash[%s] - sessionId[%s], message: %s".formatted(
                        gameType,
                        result.getJSONObject("data").getString("transactionHash"),
                        result.getJSONObject("data").getString("sessionId"),
                        result.getString("message")
                ));
            } else {
                throw new BotInvokeException("start error, " + result);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new BotInvokeException(e);
        }
    }

    /**
     * 存入资金
     * @param privateKey    privateKey
     * @param walletAddress walletAddress
     * @param amount    amount
     * @return String
     * @throws ABIInvokeException   ABIInvokeException
     */
    public String depositGameBalance(String privateKey, String walletAddress, double amount) throws ABIInvokeException {
        BigDecimal balance = EthWalletUtil.getOriginTokenBalance(IRYS_CHAIN_INFO.getRpcUrl(), walletAddress);
        if (BigDecimal.valueOf(amount * 1.1).compareTo(balance) > 0) {
            throw new IllegalArgumentException("wallet balance not enough");
        }
        return EthWalletUtil.smartContractTransactionInvoke(
                IRYS_CHAIN_INFO,
                BALANCE_DEPOSIT_ADDRESS,
                privateKey,
                walletAddress,
                null,
                EthWalletUtil.parseUnits(BigDecimal.valueOf(amount), 18),
                "0xd0e30db0"
        );
    }

    /**
     * 取出资金
     * @param privateKey    privateKey
     * @param walletAddress walletAddress
     * @param amount    amount
     * @return String
     * @throws ABIInvokeException   ABIInvokeException
     */
    public String withdrawGameBalance(String privateKey, String walletAddress, double amount) throws ABIInvokeException {
        return EthWalletUtil.smartContractTransactionInvoke(
                SCInvokeParams.builder()
                        .contractAddress(BALANCE_DEPOSIT_ADDRESS)
                        .chainInfo(IRYS_CHAIN_INFO)
                        .walletInfo(WalletInfo.builder()
                                .privateKey(privateKey)
                                .address(walletAddress)
                                .build()
                        )
                        .functionName("withdraw")
                        .paramsTypes(List.of(Pair.of(Web3jFunctionType.Uint256, EthWalletUtil.parseUnits(BigDecimal.valueOf(amount), 18))))
                        .resultTypes(List.of())
                        .build()
        );
    }

    private static Object generateIrysGameSessionId(long timestamp) {
        return "game_%s_%s".formatted(timestamp, RandomUtil.randomString(RANDOM_STR_TEM, 9));
    }

    private static String generateStartGameSignMessage(String evmAddress, Double count, long timestamp) {
        return IRYS_GAME_START_SIGN_MESSAGE.formatted(count, evmAddress, count, timestamp);
    }

    private static String convertHeaderGameType(String gameType) {
        return switch (gameType) {
            case IrysGameType.SNAKE -> "snake";
            case IrysGameType.MISSILE_COMMAND -> "missile";
            case IrysGameType.ASTEROIDS -> "asteroids";
            case IrysGameType.HEX_SHOOTER -> "hexshot";
            default -> throw new IllegalArgumentException("unknown gameType: " + gameType);
        };
    }

    private boolean checkScoreCanCompleteAble(String gameType, int score) {
        return switch (gameType) {
            case IrysGameType.SNAKE -> score > 200;
            case IrysGameType.MISSILE_COMMAND -> score > 50000;
            case IrysGameType.ASTEROIDS -> score > 100000;
            case IrysGameType.HEX_SHOOTER -> score > 15000;
            default -> throw new IllegalArgumentException("unknown gameType: " + gameType);
        };
    }

    private static double getGameCost(String gameType) {
        return switch (gameType) {
            case IrysGameType.SNAKE -> 0.001;
            case IrysGameType.MISSILE_COMMAND -> 0.001;
            case IrysGameType.ASTEROIDS -> 0.001;
            case IrysGameType.HEX_SHOOTER -> 0.001;
            default -> throw new IllegalArgumentException("unknown gameType: " + gameType);
        };
    }

    private static long dynamicCalWait(String gameType, int score) {
        long waitSecond = switch (gameType) {
            case IrysGameType.SNAKE -> calculatePlayTime(score, 10,1.1);
            case IrysGameType.MISSILE_COMMAND -> calculatePlayTime(score, 100,0.5);
            case IrysGameType.ASTEROIDS -> calculatePlayTime(score, 100,0.6);
            case IrysGameType.HEX_SHOOTER -> calculatePlayTime(score, 100,0.7);
            default -> throw new IllegalArgumentException("unknown gameType: " + gameType);
        };
        if (waitSecond > 60 * 10 && waitSecond < 60 * 20) {
            waitSecond = (long) (waitSecond * 0.7);
        } else if (waitSecond >= 60 * 20 && waitSecond < 60 * 30) {
            waitSecond = (long) (waitSecond * 0.75);
        } else if (waitSecond >= 60 * 30) {
            waitSecond = (long) (waitSecond * 0.8);
        }

        waitSecond += RandomUtil.randomInt(1, 10);
        return waitSecond;
    }

    /**
     * 计算游玩时间
     * @param score 玩家得分（每个球10分）
     * @param secondsPerBall 每个球对应的秒数
     * @return 格式化后的时间（mm:ss）
     */
    public static long calculatePlayTime(int score, int base, double secondsPerBall) {
        if (score < 0 || score % base != 0) {
            throw new IllegalArgumentException("score must * %s >= 0".formatted(base));
        }
        int balls = score / base;
        return Math.round(balls * secondsPerBall);
    }


    private static String generateCompleteGameMessage(
            String gameType,
            String evmAddress,
            int score,
            String sessionId,
            long timestamp
    ) {
        return IRYS_GAME_COMPLETE_SIGN_MESSAGE.formatted(
                gameType,
                evmAddress,
                gameType,
                score,
                sessionId,
                timestamp
        );
    }

    private static Map<String, String> generateHeader(
            FullAccountContext fullAccountContext, String refererAppend
    ) {
        Map<String, String> headers = fullAccountContext.buildHeader();
        headers.put(HeaderKey.ORIGIN, "https://play.irys.xyz");
        headers.put(HeaderKey.REFERER, "https://play.irys.xyz/" + refererAppend);
        headers.put(HeaderKey.CONTENT_TYPE, "application/json");
        return headers;
    }

}

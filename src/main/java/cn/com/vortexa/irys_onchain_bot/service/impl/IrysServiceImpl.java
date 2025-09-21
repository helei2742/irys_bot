package cn.com.vortexa.irys_onchain_bot.service.impl;


import cn.com.vortexa.account.entity.BrowserEnv;
import cn.com.vortexa.account.entity.ProxyInfo;
import cn.com.vortexa.account.entity.Web3Wallet;
import cn.com.vortexa.base.constants.HeaderKey;
import cn.com.vortexa.bot_template.service.impl.BotQuartzJobServiceImpl;
import cn.com.vortexa.common.util.NamedThreadFactory;
import cn.com.vortexa.irys_onchain_bot.bot.IrysBot;
import cn.com.vortexa.bot_template.bot.VortexaBotContext;
import cn.com.vortexa.bot_template.bot.dto.FullAccountContext;
import cn.com.vortexa.bot_template.exception.BotInvokeException;
import cn.com.vortexa.common.constants.ProxyProtocol;
import cn.com.vortexa.common.util.CastUtil;
import cn.com.vortexa.irys_onchain_bot.constants.IrysGameType;
import cn.com.vortexa.irys_onchain_bot.dto.PlayGameJobParams;
import cn.com.vortexa.irys_onchain_bot.dto.PlayGameMessage;
import cn.com.vortexa.irys_onchain_bot.dto.StartSignParams;
import cn.com.vortexa.irys_onchain_bot.job.IrysCompleteGameJob;
import cn.com.vortexa.irys_onchain_bot.job.IrysStartGameJob;
import cn.com.vortexa.irys_onchain_bot.service.IIrysExecuteRecordService;
import cn.com.vortexa.irys_onchain_bot.service.IrysService;
import cn.com.vortexa.irys_onchain_bot.service.ServerLoadService;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author helei
 * @since 2025-09-19
 */
@Service
public class IrysServiceImpl implements IrysService {

    @Autowired
    private IrysBot irysBot;

    @Autowired
    private ServerLoadService serverLoadService;

    @Autowired
    private IIrysExecuteRecordService irysExecuteRecordService;

    @Autowired
    private BotQuartzJobServiceImpl botQuartzJobService;

    @Getter
    private final ExecutorService executorService = Executors.newThreadPerTaskExecutor(
            new NamedThreadFactory("irys-executor-")
    );

    private ProxyInfo proxyInfo;

    private final BrowserEnv browserEnv;

    public IrysServiceImpl(VortexaBotContext vortexaBotContext) {
        String proxyStr = CastUtil.autoCast(vortexaBotContext.getCustomConfig().getKeyValues().get("proxy"));
        if (proxyStr != null) {
            String[] split = proxyStr.split("@");
            String[] hp = split[0].split(":");
            String[] up = split[1].split(":");
            this.proxyInfo = ProxyInfo.builder()
                    .proxyProtocol(ProxyProtocol.HTTP)
                    .host(hp[0])
                    .port(CastUtil.toInteger(hp[1]))
                    .username(up[0])
                    .password(up[1])
                    .build();
        }
        this.browserEnv = new BrowserEnv();
        this.browserEnv.setUserAgent(CastUtil.autoCast(vortexaBotContext.getCustomConfig().getKeyValues().get("user-agent")));
        this.browserEnv.setOtherHeader(new HashMap<>());
    }

    @Override
    public PlayGameMessage getStartSignMessage(StartSignParams startSignParams) throws BotInvokeException {
        // 1 检查类型
        String gameType = startSignParams.getGameType();
        if (!IrysGameType.ASTEROIDS.equals(gameType)
                && !IrysGameType.SNAKE.equals(gameType)
                && !IrysGameType.HEX_SHOOTER.equals(gameType)
                && !IrysGameType.MISSILE_COMMAND.equals(gameType)
        ) {
            throw new IllegalArgumentException("game type invalid");
        }
        Long startTime = startSignParams.getStartTime();
        Integer score = startSignParams.getScore();
        String address = startSignParams.getAddress();
        if (score == null || startTime == null || score < 0 || StrUtil.isBlank(address)) {
            throw new IllegalArgumentException("invalid params");
        }

        // 2.检查余额
        double gameCost = IrysBotApi.getGameCost(gameType);
        BigDecimal balance = irysBot.getBotApi().queryGameInnerWalletBalance(
                FullAccountContext.builder()
                        .proxy(startSignParams.getProxy() == null ? proxyInfo : startSignParams.getProxy())
                        .browserEnv(browserEnv)
                        .wallet(Web3Wallet.builder().ethAddress(address).build())
                        .build()
        );

        if (Double.compare(balance.doubleValue(), gameCost * 1.2) < 0) {
            throw new IllegalArgumentException("insufficient game wallet balance, need: " + gameCost * 1.2);
        }

        // 3.构建参数
        String startMessage = IrysBotApi.generateStartGameSignMessage(
                address,
                gameCost,
                startTime
        );
        String sessionId = IrysBotApi.generateIrysGameSessionId(startTime);
        long wait = IrysBotApi.dynamicCalWait(gameType, score);
        long endTime = startTime + wait * 1000;
        String completeMessage = IrysBotApi.generateCompleteGameMessage(
                gameType,
                address,
                score,
                sessionId,
                endTime
        );

        String id = generateExecuteRecordId();
        return PlayGameMessage.builder()
                .id(id)
                .address(address)
                .gameType(gameType)
                .gameSessionId(sessionId)
                .score(score)
                .cost(gameCost)
                .startTime(startTime)
                .startMessage(startMessage)
                .completeTime(endTime)
                .completeMessage(completeMessage)
                .loadScore(serverLoadService.getServerLoad())
                .build();
    }

    @Override
    public Date registryStartGameJob(PlayGameJobParams playGameJobParams) throws SchedulerException {
        String id = playGameJobParams.getId();
        String address = playGameJobParams.getAddress();
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(IrysStartGameJob.EXECUTE_PARAMS, playGameJobParams);
        String name = "START-" + id;
        JobKey jobKey = new JobKey(name, address);
        JobDetail jobDetail = JobBuilder.newJob(IrysStartGameJob.class)
                .setJobData(jobDataMap)
                .withIdentity(jobKey)
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .startAt(new Date(playGameJobParams.getStartTime()))
                .forJob(jobDetail)
                .withIdentity(name, address)
                .build();
        botQuartzJobService.deleteJob(jobKey);
        return botQuartzJobService.getScheduler().scheduleJob(jobDetail, trigger);
    }


    @Override
    public CompletableFuture<JSONObject> startPlayOnChainGame(PlayGameJobParams params) {
        return irysBot.getBotApi().startIrysGame(
                proxyInfo,
                generateOnChainExecuteHeader(params.getGameType()),
                params.getAddress(),
                params.getStartMessage(),
                params.getStartSign(),
                params.getGameSessionId(),
                params.getStartTime(),
                params.getGameType(),
                IrysBotApi.getGameCost(params.getGameType())
        );
    }

    @Override
    public Date registryCompleteGameJob(PlayGameJobParams playGameJobParams) throws SchedulerException {
        String id = playGameJobParams.getId();
        String address = playGameJobParams.getAddress();
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(IrysStartGameJob.EXECUTE_PARAMS, playGameJobParams);
        String name = "Complete-" + id;
        JobKey jobKey = new JobKey(name, address);
        JobDetail jobDetail = JobBuilder.newJob(IrysCompleteGameJob.class)
                .setJobData(jobDataMap)
                .withIdentity(jobKey)
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .startAt(new Date(playGameJobParams.getCompleteTime()))
                .forJob(jobDetail)
                .withIdentity(name, address)
                .build();
        botQuartzJobService.deleteJob(jobKey);
        return botQuartzJobService.getScheduler().scheduleJob(jobDetail, trigger);
    }

    @Override
    public CompletableFuture<JSONObject> completePlayOnChainGame(PlayGameJobParams params) {
        return irysBot.getBotApi().completeIrysGame(
                proxyInfo,
                generateOnChainExecuteHeader(params.getGameType()),
                params.getAddress(),
                params.getCompleteMessage(),
                params.getCompleteSign(),
                params.getGameSessionId(),
                params.getCompleteTime(),
                params.getGameType(),
                params.getScore()
        );
    }

    private Map<String, String> generateOnChainExecuteHeader(String gameType) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderKey.USER_AGENT, browserEnv.getUserAgent());
        headers.put(HeaderKey.ORIGIN, "https://play.irys.xyz");
        headers.put(HeaderKey.REFERER, "https://play.irys.xyz/" + IrysBotApi.convertHeaderGameType(gameType));
        headers.put(HeaderKey.CONTENT_TYPE, "application/json");
        return headers;
    }

    private static String generateExecuteRecordId() {
        return "0x" + UUID.randomUUID().toString().replace("-", "");
    }

}

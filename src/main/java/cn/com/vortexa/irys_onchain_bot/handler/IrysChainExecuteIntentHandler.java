package cn.com.vortexa.irys_onchain_bot.handler;

import cn.com.vortexa.bot_template.bot.VortexaBotContext;
import cn.com.vortexa.common.util.CastUtil;
import cn.com.vortexa.irys_onchain_bot.constants.IrysExecuteIntentStatus;
import cn.com.vortexa.irys_onchain_bot.dto.PlayGameJobParams;
import cn.com.vortexa.irys_onchain_bot.entity.IrysExecuteRecord;
import cn.com.vortexa.irys_onchain_bot.onchain.ExecutorGuardedIntent;
import cn.com.vortexa.irys_onchain_bot.service.IIrysExecuteRecordService;
import cn.com.vortexa.irys_onchain_bot.service.IrysOnChainService;
import cn.com.vortexa.irys_onchain_bot.service.IrysService;
import com.alibaba.fastjson.JSONObject;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Numeric;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author helei
 * @since 2025-09-19
 */
@Slf4j
@Component
public class IrysChainExecuteIntentHandler {
    private static final String THREAD_POOL_SIZE_KEY = "listen-thread-pool-size";

    @Autowired
    private ExecutorGuardedIntent executorGuardedIntent;

    @Autowired
    private IIrysExecuteRecordService irysExecuteRecordService;

    @Autowired
    private IrysOnChainService irysOnChainService;

    @Autowired
    private IrysService irysService;

    private final ExecutorService executorService;

    public IrysChainExecuteIntentHandler(VortexaBotContext vortexaBotContext) {
        Integer size = CastUtil.autoCast(vortexaBotContext.getCustomConfig().getKeyValues().get(THREAD_POOL_SIZE_KEY));
        this.executorService = Executors.newFixedThreadPool(
                size == null ? Runtime.getRuntime().availableProcessors() : size
        );
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startListening() {
        try {
            log.info("start listening execute intent...");
            // 监听 IntentRegistered 事件
            Disposable ignored = executorGuardedIntent.intentRegisteredEventFlowable(
                    DefaultBlockParameterName.LATEST,
                    DefaultBlockParameterName.LATEST
            ).subscribe(this::handlerExecuteEvent);
        } catch (Exception e) {
            log.error("start listening execute intent error¬", e);
        }
    }

    private void handlerExecuteEvent(ExecutorGuardedIntent.IntentRegisteredEventResponse event) {
        executorService.execute(() -> {
            byte[] intentId = event.intentId;
            String intentIdStr = resolveIdStr(intentId);

            // 1. 链上获取intent data
            log.info("receive address[{}] play intent[{}]", event.user, intentIdStr);
            ExecutorGuardedIntent.IntentData intentData;
            try {
                intentData = irysOnChainService.tryGetOnChainIntent(intentId, intentIdStr);
            } catch (Exception e) {
                log.warn("try get intent[{}] data fail, {}", intentIdStr, e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
                return;
            }

            // 2.注册start job
            String address = event.user;
            PlayGameJobParams playGameJobParams = convertEvent2JobParams(intentIdStr, address, intentData);

            IrysExecuteRecord.IrysExecuteRecordBuilder recordBuilder = IrysExecuteRecord.builder()
                    .id(intentIdStr)
                    .address(address)
                    .status(IrysExecuteIntentStatus.ON_CHAIN)
                    .executeParams(JSONObject.toJSONString(playGameJobParams));

            try {
                if (!checkPlayGameJobParams(playGameJobParams)) {
                    throw new IllegalArgumentException("params invalid");
                } else if (playGameJobParams.getStartTime() < System.currentTimeMillis() - 60000) {
                    recordBuilder.status(IrysExecuteIntentStatus.CANCELLED)
                            .result("start time late 60s");
                    log.warn("address[{}] execute record[{}] has been cancelled", address, intentIdStr);
                } else {
                    // 注册job
                    recordBuilder.status(IrysExecuteIntentStatus.WAITING);
                    Date nextFireTime = irysService.registryStartGameJob(playGameJobParams);
                    recordBuilder.executeTime(
                            nextFireTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                    );
                    log.info("address[{}] execute intent[{}] fire at[{}]", address, intentIdStr, nextFireTime);
                }
            } catch (Exception e) {
                log.error("handle execute event error", e.getCause() == null ? e : e.getCause());
                recordBuilder.status(IrysExecuteIntentStatus.ERROR)
                        .errorMsg(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            } finally {
                irysExecuteRecordService.save(recordBuilder.build());
            }
        });
    }

    private boolean checkPlayGameJobParams(PlayGameJobParams playGameJobParams) {
        return playGameJobParams.getScore() != null
                && playGameJobParams.getScore() > 0
                && playGameJobParams.getScore() % 10 == 0;
    }

    private static PlayGameJobParams convertEvent2JobParams(
            String id, String address, ExecutorGuardedIntent.IntentData data
    ) {
        return PlayGameJobParams.builder()
                .id(id)
                .address(address)
                .gameType(bytes32ToString(data.gameType))
                .gameSessionId(bytes32ToString(data.gameSessionId))
                .score(data.score.intValue())
                .startTime(data.startTime.longValue())
                .startMessage(new String(data.startMessage))
                .startSign(Numeric.toHexString((data.startSign)))
                .completeTime(data.completeTime.longValue())
                .completeMessage(new String(data.completeMessage))
                .completeSign(Numeric.toHexString((data.completeSign)))
                .build();
    }

    private static String resolveIdStr(byte[] padded) {
        int i = 0;
        while (i < padded.length && padded[i] == 0) {
            i++;
        }
        byte[] original = Arrays.copyOfRange(padded, i, padded.length);
        return Numeric.toHexString(original);
    }

    private static String bytes32ToString(byte[] input) {
        int i = 0;
        while (i < input.length && input[i] != 0) {
            i++;
        }
        return new String(input, 0, i, StandardCharsets.UTF_8);
    }
}



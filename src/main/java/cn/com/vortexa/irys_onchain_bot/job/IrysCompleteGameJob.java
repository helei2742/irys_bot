package cn.com.vortexa.irys_onchain_bot.job;


import cn.com.vortexa.common.util.CastUtil;
import cn.com.vortexa.irys_onchain_bot.constants.IrysExecuteIntentStatus;
import cn.com.vortexa.irys_onchain_bot.dto.PlayGameJobParams;
import cn.com.vortexa.irys_onchain_bot.entity.IrysExecuteRecord;
import cn.com.vortexa.irys_onchain_bot.exception.OnChainException;
import cn.com.vortexa.irys_onchain_bot.service.IIrysExecuteRecordService;
import cn.com.vortexa.irys_onchain_bot.service.IrysOnChainService;
import cn.com.vortexa.irys_onchain_bot.service.IrysService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author helei
 * @since 2025-09-20
 */
@Slf4j
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class IrysCompleteGameJob implements Job {

    public static final String EXECUTE_PARAMS = "executeParams";

    @Autowired
    private IrysService irysService;

    @Autowired
    private IIrysExecuteRecordService executeRecordService;

    @Autowired
    private IrysOnChainService irysOnChainService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDetail jobDetail = context.getJobDetail();
        PlayGameJobParams params = CastUtil.autoCast(jobDetail.getJobDataMap().get(EXECUTE_PARAMS));
        if (params != null) {
            irysService.getExecutorService().execute(() -> {
                String id = params.getId();
                IrysExecuteRecord.IrysExecuteRecordBuilder update = IrysExecuteRecord.builder().id(id);
                IrysExecuteRecord record = executeRecordService.getById(id);
                if (record == null) {
                    log.warn("Unregister intent[{}] from address[{}]", id, params.getAddress());
                    return;
                }

                try {

                    // 提交完成游戏
                    JSONObject data = irysService.completePlayOnChainGame(params).get();
                    log.info("address[{}]-intent[{}] complete game, result[{}]", params.getAddress(), id, data);
                    update.status(IrysExecuteIntentStatus.COMPLETED);
                    update.result(JSONObject.toJSONString(data));

                    // 提交上链的奖励
                    String txHash = irysOnChainService.completeExecuteIntent(id, params.getAddress());
                    log.info("address[{}]-intent[{}] commit on chain success, txHash[{}]", params.getAddress(), id, txHash);
                } catch (OnChainException e) {
                    log.error("commit execute intent failed", e);
                } catch (Exception e) {
                    String em = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                    log.error("address[{}]-intent[{}] complete gam error, {}", params.getAddress(), id, em);
                    update.status(IrysExecuteIntentStatus.ERROR);
                    update.errorMsg("%s -> %s error, %s".formatted(
                            record.getStatus(),
                            IrysExecuteIntentStatus.COMPLETED,
                            em
                    ));
                } finally {
                    executeRecordService.updateById(update.build());
                }
            });
        }
    }
}

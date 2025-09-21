package cn.com.vortexa.irys_onchain_bot.job;


import cn.com.vortexa.common.util.CastUtil;
import cn.com.vortexa.irys_onchain_bot.constants.IrysExecuteIntentStatus;
import cn.com.vortexa.irys_onchain_bot.dto.PlayGameJobParams;
import cn.com.vortexa.irys_onchain_bot.entity.IrysExecuteRecord;
import cn.com.vortexa.irys_onchain_bot.exception.OnChainException;
import cn.com.vortexa.irys_onchain_bot.service.IIrysExecuteRecordService;
import cn.com.vortexa.irys_onchain_bot.service.IrysOnChainService;
import cn.com.vortexa.irys_onchain_bot.service.IrysService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author helei
 * @since 2025-09-20
 */
@Slf4j
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class IrysStartGameJob implements Job {
    public static final String EXECUTE_PARAMS = "executeParams";

    @Autowired
    private IrysService irysService;

    @Autowired
    private IrysOnChainService irysOnChainService;

    @Autowired
    private IIrysExecuteRecordService executeRecordService;

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
                    log.warn("Unregister game params[{}] from address[{}]", id, params.getAddress());
                    return;
                }
                try {
                    // 链上提交开始
                    String txHash = irysOnChainService.startExecuteIntent(id, params.getAddress());
                    log.info("address[{}]-id[{}] start execute intent on chain finish, txHash:{}", params.getAddress(), id, txHash);

                    // 提交开始游戏
                    irysService.startPlayOnChainGame(params).get();

                    // 注册完成游戏任务
                    Date date = irysService.registryCompleteGameJob(params);
                    log.info("address[{}]-id[{}] start game finish, complete at[{}}", params.getAddress(), id, date);

                    update.status(IrysExecuteIntentStatus.RUNNING);
                } catch (Exception e) {
                    update.status(IrysExecuteIntentStatus.ERROR);
                    String em = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                    update.errorMsg("%s -> %s error, %s".formatted(
                            record.getStatus(),
                            IrysExecuteIntentStatus.RUNNING,
                            em
                    ));

                    // 发生错误，提交链上
                    log.error("address[{}]-id[{}] start game error, error msg[{}]", params.getAddress(), id, em);
                    try {
                        String s = irysOnChainService.commitError(id, params.getAddress());
                        log.info("address[{}]-id[{}] on chain commit error finish, txHash: {}", params.getAddress(), id, s);
                    } catch (OnChainException ex) {
                        log.info("address[{}]-id[{}] on chain commit error fail，", params.getAddress(), id, ex);
                    }
                } finally {
                    executeRecordService.updateById(update.build());
                }
            });
        }
    }
}

package cn.com.vortexa.irys_onchain_bot.service;

import cn.com.vortexa.bot_template.exception.BotInvokeException;
import cn.com.vortexa.irys_onchain_bot.dto.PlayGameJobParams;
import cn.com.vortexa.irys_onchain_bot.dto.PlayGameMessage;
import cn.com.vortexa.irys_onchain_bot.dto.StartSignParams;
import com.alibaba.fastjson.JSONObject;
import org.quartz.SchedulerException;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author helei
 * @since 2025-09-19
 */
public interface IrysService {

    ExecutorService getExecutorService();

    PlayGameMessage getStartSignMessage(StartSignParams startSignParams) throws BotInvokeException;

    Date registryStartGameJob(PlayGameJobParams playGameJobParams) throws SchedulerException;

    CompletableFuture<JSONObject> startPlayOnChainGame(PlayGameJobParams playGameJobParams);

    Date registryCompleteGameJob(PlayGameJobParams playGameJobParams) throws SchedulerException;

    CompletableFuture<JSONObject> completePlayOnChainGame(PlayGameJobParams params);
}

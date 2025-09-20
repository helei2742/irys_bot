package cn.com.vortexa.irys_onchain_bot.service;

import cn.com.vortexa.irys_onchain_bot.exception.OnChainException;
import cn.com.vortexa.irys_onchain_bot.onchain.ExecutorGuardedIntent;

public interface IrysOnChainService {
    String deployExecutorGuardedIntentContract(String privateKey) throws OnChainException;

    ExecutorGuardedIntent.IntentData tryGetOnChainIntent(byte[] intentId, String intentIdStr) throws OnChainException;

    String commitExecuteIntent(String intentIdStr) throws OnChainException;
}

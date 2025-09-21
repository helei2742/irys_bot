package cn.com.vortexa.irys_onchain_bot.service;

import cn.com.vortexa.irys_onchain_bot.exception.OnChainException;
import cn.com.vortexa.irys_onchain_bot.onchain.execute.ExecutorGuardedIntent;

public interface IrysOnChainService {
    String deployExecutorGuardedIntentContract(String privateKey) throws OnChainException;

    String deployNodeRegistryContract(String privateKey) throws OnChainException;

    ExecutorGuardedIntent.IntentData tryGetOnChainIntent(byte[] intentId, String intentIdStr, String address) throws OnChainException;

    String startExecuteIntent(String id, String address) throws OnChainException;

    String completeExecuteIntent(String id, String address) throws OnChainException;

    String commitError(String id, String address) throws OnChainException;

}

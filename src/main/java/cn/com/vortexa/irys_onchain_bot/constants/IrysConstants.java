package cn.com.vortexa.irys_onchain_bot.constants;


import cn.com.vortexa.common.constants.ChainType;
import cn.com.vortexa.common.constants.NetType;
import cn.com.vortexa.web3.dto.Web3ChainInfo;

import java.util.List;

/**
 * @author helei
 * @since 2025-09-19
 */
public class IrysConstants {
    public static final String IRYS_EXECUTE_CONTRACT_ADDRESS = "irys-execute-contract-address";
    public static final String IRYS_EXECUTE_CONTRACT_ABI = "irys-execute-contract-abi";

    public static final Web3ChainInfo IRYS_CHAIN_INFO = Web3ChainInfo
            .builder()
            .name("Irys Testnet")
            .rpcUrls(List.of("https://testnet-rpc.irys.xyz/v1/execution-rpc"))
            .chainId(1270)
            .chainType(ChainType.EVM)
            .netType(NetType.Test_net)
            .originTokenSymbol("IRYS")
            .blockExploreUrl("https://explorer.irys.xyz")
            .build();

    public static final String IRYS_GAME_START_SIGN_MESSAGE = """
            I authorize payment of %s IRYS to play a game on Irys Arcade.
               \s
            Player: %s
            Amount: %s IRYS
            Timestamp: %s
            
            This signature confirms I own this wallet and authorize the payment.
            """;

    public static final String IRYS_GAME_COMPLETE_SIGN_MESSAGE = """
            I completed a %s game on Irys Arcade.
               \s
            Player: %s
            Game: %s
            Score: %s
            Session: %s
            Timestamp: %s
            
            This signature confirms I own this wallet and completed this game.
            """;
}

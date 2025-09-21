package cn.com.vortexa.irys_onchain_bot.controller;


import cn.com.vortexa.bot_template.bot.VortexaBotContext;
import cn.com.vortexa.bot_template.exception.BotInvokeException;
import cn.com.vortexa.common.dto.Result;
import cn.com.vortexa.irys_onchain_bot.dto.StartSignParams;
import cn.com.vortexa.irys_onchain_bot.service.IrysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.com.vortexa.irys_onchain_bot.constants.IrysConstants.*;

/**
 * @author helei
 * @since 2025-09-19
 */
@RestController
@RequestMapping("${dynamic.context-path}/irys")
public class IrysController {

    @Autowired
    private VortexaBotContext vortexaBotContext;
    @Autowired
    private IrysService irysService;

    @PostMapping("/getStartSignMessage")
    public Result getSignMessage(@RequestBody @Validated StartSignParams startSignParams) throws BotInvokeException {
        return Result.ok(irysService.getStartSignMessage(startSignParams));
    }

    @GetMapping("/contractInfo")
    public Result getContractInfo() {
        return Result.ok(Map.of(
                "intent", Map.of(
                        "abi", vortexaBotContext.getCustomConfig().getKeyValues().get(IRYS_EXECUTE_CONTRACT_ABI),
                        "address", vortexaBotContext.getCustomConfig().getKeyValues().get(IRYS_EXECUTE_CONTRACT_ADDRESS)
                ),
                "node", Map.of(
                        "abi", vortexaBotContext.getCustomConfig().getKeyValues().get(IRYS_NODE_CONTRACT_ABI),
                        "address", vortexaBotContext.getCustomConfig().getKeyValues().get(IRYS_NODE_CONTRACT_ADDRESS)
                )
        ));
    }
}

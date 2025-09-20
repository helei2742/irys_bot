package cn.com.vortexa.irys_onchain_bot.service.impl;


import cn.com.vortexa.irys_onchain_bot.service.ServerLoadService;
import org.springframework.stereotype.Service;

/**
 * @author helei
 * @since 2025-09-19
 */
@Service
public class ServerLoadServiceImpl implements ServerLoadService {

    @Override
    public int getServerLoad() {
        return 50;
    }
}

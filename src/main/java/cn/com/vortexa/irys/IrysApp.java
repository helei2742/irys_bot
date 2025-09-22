package cn.com.vortexa.irys;

import cn.com.vortexa.bot_template.BotTemplateAutoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ImportAutoConfiguration(BotTemplateAutoConfig.class)
public class IrysApp {
    public static void main(String[] args) {
        SpringApplication.run(IrysApp.class, args);
    }
}

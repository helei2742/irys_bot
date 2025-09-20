package cn.com.vortexa.irys_onchain_bot.dto;


import cn.com.vortexa.account.entity.ProxyInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * @author helei
 * @since 2025-09-19
 */
@Data
public class StartSignParams {

    @NotBlank
    private String gameType;

    @NotBlank
    private String address;

    @NotNull
    @Positive
    private Integer score;

    @NotNull
    @Positive
    private Long startTime;

    private ProxyInfo proxy;
}

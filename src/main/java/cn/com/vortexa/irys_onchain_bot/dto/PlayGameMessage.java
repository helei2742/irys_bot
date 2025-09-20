package cn.com.vortexa.irys_onchain_bot.dto;


import lombok.Builder;
import lombok.Data;

/**
 * @author helei
 * @since 2025-09-19
 */
@Data
@Builder
public class PlayGameMessage {
    private String id;
    private String address;
    private String gameType;
    private String gameSessionId;
    private Integer score;
    private Double cost;
    private Long startTime;
    private String startMessage;
    private String startSign;
    private Long completeTime;
    private String completeMessage;
    private String completeSign;
    private Integer loadScore;
}

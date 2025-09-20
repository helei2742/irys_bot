package cn.com.vortexa.irys_onchain_bot.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author helei
 * @since 2025-09-20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayGameJobParams implements Serializable {
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

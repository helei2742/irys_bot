package cn.com.vortexa.irys_onchain_bot.entity;

import cn.com.vortexa.irys_onchain_bot.constants.IrysExecuteIntentStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author com.helei
 * @since 2025-09-20
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_irys_execute_record")
public class IrysExecuteRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 312712321281823L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("address")
    private String address;

    @TableField("status")
    private IrysExecuteIntentStatus status;

    @TableField("execute_time")
    private LocalDateTime executeTime;

    @TableField("execute_params")
    private String executeParams;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("result")
    private String result;

    @TableField("create_datetime")
    private LocalDateTime createDatetime;

    @TableField("update_datetime")
    private LocalDateTime updateDatetime;
}

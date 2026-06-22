package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("aqi_measurement")
public class AqiMeasurement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long feedbackId;
    private Long workerId;
    private String feedbackNo;
    private String so2Level;
    private String coLevel;
    private String pm25Level;
    private String finalAqiLevel;
    private LocalDateTime measureTime;
}

package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("aqi_feedback")
public class AqiFeedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String feedbackNo;
    private Long supervisorId;
    private String province;
    private String city;
    private String address;
    private String estimatedAqiLevel;
    private String description;
    private String status;
    private Long assignedWorkerId;
    private String assignType;
    private LocalDateTime assignTime;
    private LocalDateTime createTime;
}

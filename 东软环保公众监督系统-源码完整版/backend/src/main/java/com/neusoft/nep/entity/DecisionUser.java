package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("decision_user")
public class DecisionUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String decisionCode;
    private String password;
    private String realName;
}

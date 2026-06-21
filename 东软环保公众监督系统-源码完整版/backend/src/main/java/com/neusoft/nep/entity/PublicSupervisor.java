package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("public_supervisor")
public class PublicSupervisor {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String password;
    private String realName;
    private Integer age;
    private String gender;
    private LocalDateTime createTime;
}

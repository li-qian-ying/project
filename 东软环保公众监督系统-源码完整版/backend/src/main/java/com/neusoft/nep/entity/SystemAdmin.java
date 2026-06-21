package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("system_admin")
public class SystemAdmin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String adminCode;
    private String password;
    private String realName;
}

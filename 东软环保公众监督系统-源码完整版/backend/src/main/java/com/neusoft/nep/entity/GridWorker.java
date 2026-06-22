package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("grid_worker")
public class GridWorker {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String workerCode;
    private String password;
    private String realName;
    private String province;
    private String city;
    private String workStatus;
}

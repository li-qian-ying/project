package com.neusoft.nep.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("grid_area")
public class GridArea {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String province;
    private String city;
    private Boolean enabled;
}

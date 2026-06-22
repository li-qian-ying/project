package com.neusoft.nep.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AqiLevelService {
    private static final List<String> LEVELS = Arrays.asList(
            "优", "良", "轻度污染", "中度污染", "重度污染", "严重污染"
    );

    public String maximum(String so2, String co, String pm25) {
        int index = Math.max(levelIndex(so2), Math.max(levelIndex(co), levelIndex(pm25)));
        return LEVELS.get(index);
    }

    public int levelIndex(String level) {
        int index = LEVELS.indexOf(level);
        if (index < 0) {
            throw new IllegalArgumentException("无效的AQI等级：" + level);
        }
        return index;
    }
}

package com.neusoft.nep.dto;

import lombok.Data;

@Data
public class MeasurementRequest {
    private Long feedbackId;
    private Long workerId;
    private String so2;
    private String co;
    private String pm25;
}

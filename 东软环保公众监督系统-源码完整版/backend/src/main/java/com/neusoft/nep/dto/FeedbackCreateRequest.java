package com.neusoft.nep.dto;

import lombok.Data;

@Data
public class FeedbackCreateRequest {
    private Long supervisorId = 1L;
    private String province;
    private String city;
    private String address;
    private String estimate;
    private String description;
}

package com.tigerobo.example.entity;

import lombok.Data;

@Data
public class ConsulRiskModel {


    private String riskId;
    private String riskName;
    private String parentName;
    private Integer score;
}

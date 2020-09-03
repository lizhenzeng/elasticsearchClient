package com.tigerobo.example.entity;

import lombok.Data;

import java.util.List;

@Data
public class IgnoreOutputModel {
    private Integer maxLevel;
    private String blackStr;
    private List<IgnoreModel> blackList;
}

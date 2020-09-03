package com.tigerobo.example.entity;

import com.tiger.functool.base.BaseSuperDTO;
import lombok.Data;

import java.util.List;

@Data
public class Guosheng extends BaseSuperDTO {
    private String code;
    private String name;
    private List<GuoshengSuggests> prefixSuggests;
    private String exchange;
    private String id;
    private List<GuoshengSuggests> middleSuggests;
}

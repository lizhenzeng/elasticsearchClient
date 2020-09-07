package com.tigerobo.example.dao;

import com.tigerobo.example.entity.Sample;
import com.tigerobo.search.annotation.EsMapper;
import com.tigerobo.search.annotation.SelectProvider;
import org.springframework.beans.factory.annotation.Lookup;

import java.util.List;

public interface TestDao {

    @SelectProvider(sql = "select * from sample where name = #{name}")
    List<Sample> selectSample(String name);
}

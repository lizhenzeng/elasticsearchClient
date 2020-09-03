package com.tigerobo.example.controller;


import com.tiger.functool.util.PageUtils;
import com.tigerobo.example.dao.ConsulEsDao;
import com.tigerobo.example.dao.ExampleDao;
import com.tigerobo.example.entity.ConsulEsModel;
import com.tigerobo.example.entity.Sample;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/sample")
public class SampleController {

    @Autowired
    ExampleDao exampleDao;
    @Autowired
    ConsulEsDao consulEsDao;


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "测试put", tags = {"测试put"})
    public PageUtils<ConsulEsModel> queryHouseInfoList(@RequestBody ConsulEsModel consulEsModel) throws IOException {
        return consulEsDao.selectConsulEsModel(consulEsModel);
    }


}

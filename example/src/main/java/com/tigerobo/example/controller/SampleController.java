package com.tigerobo.example.controller;


import com.tiger.functool.base.BaseSuperDTO;
import com.tiger.functool.util.PageUtils;
import com.tigerobo.example.dao.ExampleDao;
import com.tigerobo.example.entity.Sample;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/sample")
public class SampleController {

    @Autowired
    ExampleDao exampleDao;
//            " and publishDate>'2020-06-02 10:15:00'and publishDate<'2020-06-05 10:15:00'")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "测试put", tags = {"测试put"})
    public PageUtils<Sample> queryHouseInfoList(@RequestBody Sample sample) throws IOException {
        sample.setPublishDateStart("2020-06-02 10:15:00");
        sample.setPublishDateEnd("2020-06-05 10:15:00");
        return exampleDao.selectSample(sample);
    }

}

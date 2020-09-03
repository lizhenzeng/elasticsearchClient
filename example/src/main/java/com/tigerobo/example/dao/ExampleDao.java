package com.tigerobo.example.dao;

import com.tiger.functool.util.PageUtils;
import com.tigerobo.example.entity.Sample;
import com.tigerobo.search.annotation.Param;
import com.tigerobo.search.annotation.SelectProvider;
import com.tigerobo.search.bind.BaseMapperInterface;

public interface ExampleDao extends BaseMapperInterface<Sample> {

    @SelectProvider(sql = "select newsId,source,subjectNames,subjectIds," +
            "titleSegs,tagIds,riskIds,publishDate,titleEmotion," +
            "state,title from mysteel_news_tag_v1.news where tagIds|tagId='#sample.tagIds' and newsId ='#sample.newsId' and subjectIds == '#sample.subjectIds' " +
            " and publishDate>'#sample.publishDateStart'and publishDate<'#sample.publishDateEnd' order by publishDate desc")
//@SelectProvider(sql = "select id, newsId,source,subjectNames,subjectIds, \n" +
//        "            titleSegs,tagIds,riskIds,publishDate,titleEmotion, \n" +
//        "            state,title from mysteel_news_tag_v1.news " +
//        "where 1=1 \n" +
//        "<if test=\"{#sample.tagIds!=null}\">\n" +
//        "  and tagIds|tagId='#sample.tagIds' \n" +
//        "</if>\n" +
//        "<if test=\"{#sample.newsId==\"1\"}\">\n" +
//        " and newsId ='#sample.newsId' \n" +
//        "</if>\n" +
//        "<if test=\"{#sample.subjectIds!=null}\">\n" +
//        "  and subjectIds == '#sample.subjectIds'  \n" +
//        "</if>" +
//        "<if test=\"{#sample.id!=null}\">\n" +
//        "  and id = '#sample.id'  \n" +
//        "</if>" +
//        "\t\t\torder by publishDate desc")
    PageUtils<Sample> selectSample(@Param("sample")Sample sample);

}

//package com.tigerobo.example.dao;
//
//import com.tiger.functool.util.PageUtils;
//import com.tigerobo.example.entity.Guosheng;
//import com.tigerobo.example.entity.GuoshengResult;
//import com.tigerobo.example.entity.Sample;
//import com.tigerobo.search.annotation.Param;
//import com.tigerobo.search.annotation.SelectProvider;
//import com.tigerobo.search.bind.BaseMapperInterface;
//
//public interface GuoshengDao extends BaseMapperInterface<GuoshengResult> {
//
////    @SelectProvider(sql = "select newsId,source,subjectNames,subjectIds," +
////            "titleSegs,tagIds,riskIds,publishDate,titleEmotion," +
////            "state,title from mysteel_news_tag_v1.news where tagIds|tagId='{#sample.tagIds}' and newsId ='{#sample.newsId}' and subjectIds == '{#sample.subjectIds}' " +
////            " and publishDate>'{#sample.publishDateStart}'and publishDate<'{#sample.publishDateEnd}' order by publishDate desc")
//@SelectProvider(sql = "select code ,exchange,middleSuggests from guosen_financial_index_v2.guosen where id='#sample.id' ")
//    PageUtils<GuoshengResult> selectSample(@Param("sample") Guosheng sample);
//
//}

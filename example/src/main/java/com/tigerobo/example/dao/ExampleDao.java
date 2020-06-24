package com.tigerobo.example.dao;

        import com.tiger.functool.base.BaseSuperDTO;
        import com.tiger.functool.util.PageUtils;
        import com.tigerobo.example.entity.Sample;
        import com.tigerobo.search.annotation.SelectProvider;
        import com.tigerobo.search.bind.BaseMapperInterface;

public interface ExampleDao extends BaseMapperInterface<Sample> {

//    @SelectProvider(sql = "select newsId,source,subjectNames,subjectIds," +
//            "titleSegs,tagIds,riskIds,publishDate,titleEmotion," +
//            "state,title from mysteel_news_tag_v1.news where tagIds|tagId='{#sample.tagIds}' and newsId ='{#sample.newsId}' and subjectIds == '{#sample.subjectIds}' " +
//            " and publishDate>'{#sample.publishDateStart}'and publishDate<'{#sample.publishDateEnd}' order by publishDate desc")
@SelectProvider(sql = "select newsId,source,subjectNames,subjectIds, \n" +
        "            titleSegs,tagIds,riskIds,publishDate,titleEmotion, \n" +
        "            state,title from mysteel_news_tag_v1.news " +
        "where 1=1 \n" +
        "<if test=\"#sample.tagIds!=null\">\n" +
        "  and tagIds|tagId='#sample.tagIds' \n" +
        "</if>\n" +
        "<if test=\"#sample.newsId!=null\">\n" +
        " and newsId ='#sample.newsId' \n" +
        "</if>\n" +
        "<if test=\"#sample.subjectIds!=null\">\n" +
        "  and subjectIds == '#sample.subjectIds'  \n" +
        "</if>\n" +
        "\t\t\torder by publishDate desc")
    PageUtils<Sample> selectSample(Sample sample);
}

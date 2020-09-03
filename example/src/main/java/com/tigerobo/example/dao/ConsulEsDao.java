package com.tigerobo.example.dao;

import com.tiger.functool.util.PageUtils;
import com.tigerobo.example.entity.ConsulEsModel;
import com.tigerobo.search.annotation.Param;
import com.tigerobo.search.annotation.SelectProvider;
import com.tigerobo.search.bind.BaseMapperInterface;

public interface ConsulEsDao extends BaseMapperInterface<ConsulEsModel> {


    @SelectProvider(sql = "select id,tagIdStr,titleSegs,tagIds,tagIds,publishDate,titleEmotion,source,title,state,newsId,riskIds,operator,updateTime,isBlack from mysteel_news_tag_v2.news " +
            "where 1=1  " +
            "<if test=\"{#consulEsModel.id!=null}\"> " +
            "  and id='#consulEsModel.id' " +
            "</if> " +

            "<if test=\"{#consulEsModel.newsId!=null}\"> " +
            "  and newsId='#consulEsModel.newsId' " +
            "</if> " +
            "<if test=\"{#consulEsModel.titleSegs!=null}\"> " +
            "  and titleSegs=='#consulEsModel.titleSegs' " +
            "</if> " +
            "<if test=\"{#consulEsModel.publishDateStart!=null}\"> " +
            "  and publishDate > '#consulEsModel.publishDateStart' " +
            "</if> " +
            "<if test=\"{#consulEsModel.publishDateEnd!=null}\"> " +
            "  and publishDate < '#consulEsModel.publishDateEnd' " +
            "</if> " +
            "<if test=\"{#consulEsModel.state!=null}\"> " +
            "  and state='#consulEsModel.state' " +
            "</if> " +
            "<if test=\"{#consulEsModel.source!=null}\"> " +
            "  and source='#consulEsModel.source' " +
            "</if> " +
            "<if test=\"{#consulEsModel.matchModel==1}\"> " +
            "  and tagIdStr='#consulEsModel.tagId' " +
            "</if> " +
            "<if test=\"{#consulEsModel.tagId==0}\"> " +
            "  and tagIds|tagId=='#consulEsModel.tagId' " +
            "</if> " +
            "<if test=\"{#consulEsModel.riskId!=null}\"> " +
            "  and riskIds|riskId=='#consulEsModel.riskId' " +
            "</if> "

//            " order by publishDate desc id desc newsId desc"
    )
    PageUtils<ConsulEsModel> selectConsulEsModel(@Param("consulEsModel") ConsulEsModel consulEsModel);
}

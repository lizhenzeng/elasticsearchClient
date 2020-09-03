package com.tigerobo.search.utils;

import com.alibaba.fastjson.JSONObject;
import com.tiger.functool.base.BaseSuperDTO;
import com.tiger.functool.util.PageUtils;
import com.tigerobo.search.config.EsConfiguration;
import com.tigerobo.search.factory.EsClient;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EsClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(EsClientUtils.class);

    private RestHighLevelClient esClient;
    private EsConfiguration esConfiguration;

    public EsClientUtils(EsClient esClient) {
        this.esClient = esClient.getRestHighLevelClient();
        this.esConfiguration = esClient.getEsConfiguration();
    }

    public <T, A extends BaseSuperDTO> PageUtils searchInfo(
            QueryBuilder queryBuilder, List<SortBuilder> fieldSort1,
            String[] includes, BaseSuperDTO solrDto, Type t, String index, String type) {
        int begin = solrDto.getPageIndex();
        if (begin - 1 >= 0) {
            begin = begin - 1;
        }
        solrDto.setPageIndex(begin);
        return searchInfo(new String[]{Validation.notEmptyAndBlankStr(index) ? index : esConfiguration.getIndex()}
                , new String[]{Validation.notEmptyAndBlankStr(type) ? type : esConfiguration.getType()}
                , queryBuilder, fieldSort1, includes, solrDto.getPageIndex() * solrDto.getPageSize(), solrDto.getPageSize(), (Class) t);
    }

    public <T> PageUtils searchInfo(String[] esIndex, String[] EsType,
                                    QueryBuilder queryBuilder, List<SortBuilder> fieldSorts,
                                    String[] includes, int begin, int pageSize, Class<T> t) {
        List<T> resList = new ArrayList();

        //请求对象
        SearchRequest request = new SearchRequest(esIndex);
        request.types(EsType);

        //查询条件对象
        SearchSourceBuilder builder = new SearchSourceBuilder();
        System.out.println(queryBuilder.toString());
        builder.query(queryBuilder)
                .from(begin)
                .size(pageSize)
                .trackScores(true)
                .timeout(new TimeValue(3, TimeUnit.SECONDS));
        if (fieldSorts != null && !fieldSorts.isEmpty()) {
            fieldSorts.stream().forEach(val -> {
                if (val != null && val instanceof SortBuilder) {
                    builder.sort(val);
                }
            });
        }
        if (includes != null) {
            builder.fetchSource(includes, null);
        }
//查询条件对象放入请求对象中
        request.source(builder);
        try {
            SearchResponse searchResponse = esClient.search(request);

            SearchHit[] hits = searchResponse.getHits().getHits();

            if (hits != null && hits.length > 0) {
                for (SearchHit hit : hits) {
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());
                        jsonObject.put("id", hit.getId());
                        T esDataDocument = jsonObject.toJavaObject(t);
                        resList.add(esDataDocument);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            logger.info("pageSize is {}, pageNo is {} ,pageInfo is{}",resList.size(),begin/pageSize+1,JSONObject.toJSON(resList));
            return PageUtils.initPage((int) searchResponse.getHits().getTotalHits(), pageSize, begin/pageSize, resList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(Object index, Object type, Object id, String jsonString) {
        int i = 0;
        while (i < 3) {
            IndexRequest indexRequest = new IndexRequest(String.valueOf(index), String.valueOf(type), String.valueOf(id));
            indexRequest.source(jsonString, XContentType.JSON);
            try {
                IndexResponse indexResponse = esClient.index(indexRequest, RequestOptions.DEFAULT);
                ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
                return shardInfo.getFailed() <= 0;
            } catch (Exception e) {
                logger.error("index failed!", e);
                i++;
            }
        }
        return false;
    }

    public boolean update(Object index, Object type, Object id, String jsonStr) {
        int i = 0;
        while (i < 3) {
            try {
                UpdateRequest request = new UpdateRequest(String.valueOf(index), String.valueOf(type),String.valueOf(id));
                request.doc(jsonStr, XContentType.JSON);
                //强制里面刷新
                request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
                UpdateResponse updateResponse = esClient.update(request, RequestOptions.DEFAULT);
                //也可以检查分片失败：
                ReplicationResponse.ShardInfo shardInfo = updateResponse.getShardInfo();
                return shardInfo.getFailed() <= 0;
            } catch (Exception e) {
                logger.error("index failed!", e);
                i++;
            }
        }
        return false;


    }
}

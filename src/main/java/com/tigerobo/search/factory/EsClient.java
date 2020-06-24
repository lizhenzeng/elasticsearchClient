package com.tigerobo.search.factory;

import com.tigerobo.search.config.EsConfiguration;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
@AutoConfigureAfter({EsConfiguration.class})
public class EsClient {

    private RestHighLevelClient restHighLevelClient;
    private EsConfiguration esConfiguration;

    public EsConfiguration getEsConfiguration() {
        return esConfiguration;
    }

    public void setEsConfiguration(EsConfiguration esConfiguration) {
        this.esConfiguration = esConfiguration;
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Bean
    public RestHighLevelClient getEsClient(EsConfiguration esConfiguration){
        this.esConfiguration = esConfiguration;
        this.restHighLevelClient =  new RestHighLevelClient(RestClient.builder(new HttpHost(esConfiguration.getHost(), esConfiguration.getPort())));
        return restHighLevelClient;
    }
}

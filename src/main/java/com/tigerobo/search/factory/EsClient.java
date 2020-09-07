package com.tigerobo.search.factory;

import com.tigerobo.search.config.EsConfiguration;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.context.annotation.Configuration
@AutoConfigureAfter({EsConfiguration.class})
public class EsClient {

    private static RestHighLevelClient restHighLevelClient;
    private static EsConfiguration configuration;

    public EsConfiguration getEsConfiguration() {
        return configuration;
    }

    public void setEsConfiguration(EsConfiguration esConfiguration) {
        this.configuration = esConfiguration;
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Bean
    public static RestHighLevelClient getEsClient(EsConfiguration esConfiguration) {
        configuration = esConfiguration;
        if (esConfiguration.getHosts() != null && !esConfiguration.getHosts().isEmpty()) {
            List<HttpHost> hostList = esConfiguration.getHosts().stream().map(val -> new HttpHost(val.split(":")[0], Integer.valueOf(val.split(":")[1]))).collect(Collectors.toList());
            restHighLevelClient = new RestHighLevelClient(RestClient.builder(hostList.toArray(new HttpHost[hostList.size()])));
        } else {
            restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost(esConfiguration.getHost(), esConfiguration.getPort())));
        }
        return restHighLevelClient;
    }
}

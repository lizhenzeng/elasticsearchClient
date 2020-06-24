elasticsearchClient 是一款基于注解进行操作elasticsearch@6.5工具 
现在有： 
1.@EnableElasticSearch 启动注解 
2.@SelectProvider sql注解 
使用案例：
启动项
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableElasticSearch
@EnableConfigurationProperties
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

}
Dao层 需要继承BaseMapperInterface Sample实现类为返回对象的泛型类
public interface ExampleDao extends BaseMapperInterface<Sample> {


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

实体类
import com.tiger.functool.base.BaseSuperDTO;
import lombok.Data;
Sample 需要继承BaseSuperDTO
@Data
public class Sample extends BaseSuperDTO {
    private String newsId;
    private String source;
    private String subjectNames;
    private String subjectIds;
    private String titleSegs;
    private String tagIds;
    private String riskIds;
    private String publishDateEnd;
    private String publishDateStart;
    private String titleEmotion;
    private String state;
    private String title;
}



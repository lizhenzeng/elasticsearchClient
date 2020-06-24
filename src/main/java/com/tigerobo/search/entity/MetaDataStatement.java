package com.tigerobo.search.entity;

import com.tiger.functool.base.BaseSuperDTO;
import com.tigerobo.search.condition.ConditionParser;
import com.tigerobo.search.condition.IfCondition;
import com.tigerobo.search.factory.EsClient;
import com.tigerobo.search.parser.ParserException;
import com.tigerobo.search.parser.ReadHelper;
import com.tigerobo.search.parser.SqlParser;
import com.tigerobo.search.utils.EsClientUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MetaDataStatement {

    private String sql;
    private String orignalSql;
    private Exception exception;
    private List<Parameter> ps = new ArrayList<>();
    private Map<Class, Parameter> psMap = new HashMap<>();
    private List<Element> elements;
    private String index;
    private Type[] genericType;

    public MetaDataStatement(String sql) {
        this.setSql(sql);
        this.setOrignalSql(sql);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public MetaDataStatement(List<Parameter> ps, String sql) {
        this.ps = ps;
        this.setSql(sql);
    }

    public MetaDataStatement(Exception exception) {
        this.exception = exception;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;


    }

    public String getOrignalSql() {
        return orignalSql;
    }

    public void setOrignalSql(String orignalSql) {
        this.orignalSql = orignalSql;
    }

    public String getIndex() {
        return index;
    }


    public void setIndex(String index) {
        this.index = index;
    }

    public List<Parameter> getPs() {
        return ps;
    }

    public void setPs(List<Parameter> ps) {
        this.ps = ps;
        ps.forEach(val -> psMap.put(val.getType(), val));
        if (psMap.get(BaseSuperDTO.class) == null) {
            Map<Class, Parameter> extendBaseSuper = new HashMap<>();
            for (Map.Entry<Class, Parameter> entry : psMap.entrySet()) {
                if (entry.getValue() != null && entry.getValue().getType() != null &&
                        BaseSuperDTO.class.isAssignableFrom(entry.getValue().getType())) {
                    extendBaseSuper.put(BaseSuperDTO.class, entry.getValue());
                }
            }
            if (extendBaseSuper.size() > 1) {
                throw new ParserException("the parmeters has more than one extend baseSuperDto");
            }
            if (extendBaseSuper.size() == 0) {
                throw new ParserException("one of parmeters must has one extend baseSuperDto");
            }
            psMap.putAll(extendBaseSuper);
        }
    }

    public Object getResult(EsClient esClient) {
        String originalSql = getOrignalSql();
        ConditionParser conditionParser = new ConditionParser();
        List<IfCondition> ifConditions = conditionParser.parser(originalSql, IfCondition.class);
        for(IfCondition ifCondition:ifConditions){
            originalSql = ifCondition.conditionIfTrue(originalSql,this);
        }
        this.setSql(originalSql);
        ReadHelper readHelper = new ReadHelper(this);
        this.elements = readHelper.getElement();
        EsClientUtils esClientUtils = new EsClientUtils(esClient);
        Parameter parameter = psMap.get(BaseSuperDTO.class);
        if (parameter != null) {
            Object baseSuperDto = parameter.getValue();
            //获取解析where条件
            Element whereElement = getElementByType(elements, ElementTypeEnum.WHERE);
            if (whereElement != null) {
                SqlParser sqlParser = new SqlParser(this);
                whereElement = sqlParser.parserWhere(whereElement);
            }

            //获取include
            Element orderElement = getElementByType(elements, ElementTypeEnum.ORDERBY);
            List<SortBuilder> sortBuilders = new ArrayList<>();
            if (orderElement != null) {
                SqlParser sqlParser = new SqlParser(this);
                sortBuilders = sqlParser.parserOrderBy(orderElement);
            }

            //获取include
            Element selectElement = getElementByType(elements, ElementTypeEnum.SELECT);

            //获取include
            Element fromElement = getElementByType(elements, ElementTypeEnum.FROM);


            if (whereElement != null) {
                return esClientUtils.searchInfo(whereElement.getQu(), sortBuilders, getIncludeCols(selectElement),
                        (BaseSuperDTO) baseSuperDto, genericType[0], fromElement.getParamterName(), fromElement.getPrefix());
            } else {
                return esClientUtils.searchInfo(new BoolQueryBuilder(), sortBuilders, getIncludeCols(selectElement),
                        (BaseSuperDTO) baseSuperDto, genericType[0], fromElement.getParamterName(), fromElement.getPrefix());

            }
        }
        return null;
    }

    public String[] getIncludeCols(Element element) {
        List<String> include = new ArrayList<>();
        if (element.size() > 0) {
            List<Element> eles = element.getElements();
            for (Element ele : eles) {
                if (ele.getClass().isAssignableFrom(ElementMeta.class)) {
                    include.add(ele.getParamterName());
                }
            }
        }
        return include.toArray(new String[include.size()]);
    }

    public Element getElementByType(List<Element> elements, ElementTypeEnum elementTypeEnum) {
        for (Element ele : elements) {
            if (ele.getType().equals(elementTypeEnum)) {
                return ele;
            }
        }
        return null;
    }

    public Type[] getGenericType() {
        return genericType;
    }

    public void setGenericType(Type[] genericType) {
        this.genericType = genericType;
    }
}

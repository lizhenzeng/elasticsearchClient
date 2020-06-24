package com.tigerobo.search.entity;

import com.tigerobo.search.constant.Constant;
import com.tigerobo.search.utils.Validation;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ElementTypeEnum {
    SELECT(Constant.select){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            FunctionMeta fm = new FunctionMeta(value,null,ElementTypeEnum.SELECT,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    },

    ORDERBY(Constant.order){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            FunctionMeta fm = new FunctionMeta(value,null,ElementTypeEnum.ORDERBY,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    },
    BY(Constant.BY){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            FunctionMeta fm = new FunctionMeta(value,null,ElementTypeEnum.BY,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    },
    DESC(Constant.DESC){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            FunctionMeta fm = new FunctionMeta(value,null,ElementTypeEnum.DESC,metaDataStatement);
            return  fm;
        }

        @Override
        public SortBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            SortBuilder sb = null;
            if(Validation.notEmptyAndBlankStr(element.getCompareValue())){
                if(element.getCompareValue().equalsIgnoreCase(Constant.score)){
                    sb = SortBuilders.scoreSort().order(SortOrder.DESC);
                }else{
                    sb = SortBuilders.fieldSort(element.getCompareValue()).order(SortOrder.DESC);
                }
            }
            return sb;
        }
    },
    ASC(Constant.ASC){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            FunctionMeta fm = new FunctionMeta(value,null,ElementTypeEnum.ASC,metaDataStatement);
            return  fm;
        }

        @Override
        public SortBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            SortBuilder sb = null;
            if(Validation.notEmptyAndBlankStr(element.getCompareValue())){
                if(element.getCompareValue().equalsIgnoreCase(Constant.score)){
                    sb = SortBuilders.scoreSort().order(SortOrder.ASC);
                }else{
                    sb = SortBuilders.fieldSort(element.getCompareValue()).order(SortOrder.ASC);
                }
            }
            return sb;
        }
    },
    FROM(Constant.from){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            FunctionMeta fm = new FunctionMeta(value,null,ElementTypeEnum.FROM,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    },
    WHERE(Constant.where){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            FunctionMeta fm = new FunctionMeta(value,null,ElementTypeEnum.WHERE,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            if(element.size() == 1){
                return  element.getElements().get(0).getQu();
            }
            if(element.size()>1){
               List<Element> els = element.getElements().stream().filter(val->val.getQu()==null).collect(Collectors.toList());
               if(els==null || els.isEmpty()){
                   BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                   element.getElements().forEach(val->boolQueryBuilder.must(val.getQu()));
                   return boolQueryBuilder;
               }
            }
            return null;
        }
    },
    AND(Constant.and){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            FunctionMeta fm = new FunctionMeta(value,null,ElementTypeEnum.AND,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if(f2 == null && f1 != null){
                boolQueryBuilder.must(f1);
            }
            if(f2 != null && f1 != null){
                boolQueryBuilder.must(f1).must(f2);
            }
            return boolQueryBuilder;
        }
    },
    OR(Constant.or){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            FunctionMeta fm = new FunctionMeta(value,null,ElementTypeEnum.OR,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if(f2 == null && f1 != null){
                boolQueryBuilder.should(f1);
            }
            if(f2 != null && f1 != null){
                boolQueryBuilder.should(f1).should(f2);
            }
            return boolQueryBuilder;
        }
    },

    EQUAL(Constant.equal){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            SymbolMeta fm = new SymbolMeta(value,null,ElementTypeEnum.EQUAL,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            if(element.getClass().isAssignableFrom(SymbolMeta.class) && !element.getParamterName().equalsIgnoreCase(Constant.one)){
                ((SymbolMeta)element).proccessor();
                if(Validation.notEmptyAndBlankStr(((SymbolMeta) element).getGroupName())){
                    NestedQueryBuilder nqb = QueryBuilders
                            .nestedQuery(((SymbolMeta) element).getGroupName(), QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(String.format("%s.%s",
                                    ((SymbolMeta) element).getGroupName(),((SymbolMeta) element).getDetailName())
                                    , element.getCompareValue())), ScoreMode.None);
                    boolQueryBuilder.must(nqb);
                }else{
                    TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery(element.getParamterName(),element.getCompareValue());
                    boolQueryBuilder.must(queryBuilder);
                }
            }
            return boolQueryBuilder;
        }
    },

    EQUALTOKEN(Constant.equalToken){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            SymbolMeta fm = new SymbolMeta(value,null,ElementTypeEnum.EQUALTOKEN,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            if(element.getClass().isAssignableFrom(SymbolMeta.class)){
                ((SymbolMeta)element).proccessor();
                if(Validation.notEmptyAndBlankStr(((SymbolMeta) element).getGroupName())){
                    NestedQueryBuilder nqb = QueryBuilders
                            .nestedQuery(((SymbolMeta) element).getGroupName(), QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(String.format("%s.%s",
                                    ((SymbolMeta) element).getGroupName(),((SymbolMeta) element).getDetailName())
                                    , element.getCompareValue())), ScoreMode.None);
                    boolQueryBuilder.must(nqb);
                }else{
                    MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery(element.getParamterName(),element.getCompareValue());
                    boolQueryBuilder.must(queryBuilder);
                }
            }
            return boolQueryBuilder;
        }
    },
    LESS(Constant.less){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            SymbolMeta fm = new SymbolMeta(value,null,ElementTypeEnum.LESS,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    },
    GREAT(Constant.great){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            SymbolMeta fm = new SymbolMeta(value,null,ElementTypeEnum.GREAT,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    },
    RANGE(Constant.range){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            SymbolMeta fm = new SymbolMeta(value,null,ElementTypeEnum.RANGE,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            if(element.getClass().isAssignableFrom(SymbolMeta.class)){
                ((SymbolMeta)element).proccessor();
                BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
                boolQueryBuilder.filter(QueryBuilders.rangeQuery(element.getParamterName())
                        .from(((SymbolMeta)element).getMinValue()).to(((SymbolMeta)element).getMaxValue()));
                return boolQueryBuilder;
            }
            return null;
        }
    },
    GREATANDEQUAL(Constant.greatAndEqual){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            SymbolMeta fm = new SymbolMeta(value,null,ElementTypeEnum.GREATANDEQUAL,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    },
    LESSANDEQUAL(Constant.lessAndEqual){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            SymbolMeta fm = new SymbolMeta(value,null,ElementTypeEnum.LESSANDEQUAL,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    },
    LIKE(Constant.like){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            SymbolMeta fm = new SymbolMeta(value,null,ElementTypeEnum.LIKE,metaDataStatement);
            return  fm;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery(element.getParamterName(),element.getCompareValue())
                    .fuzziness(Fuzziness.AUTO);
            return queryBuilder;
        }
    },

    LEFTBRACKETS(Constant.leftBrackets){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            return new NestElement(null,null,ElementTypeEnum.LEFTBRACKETS);
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            List<Element> eles =  element.getElements();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for(Element e:eles){
                if(!e.getType().equals(ElementTypeEnum.RIGHTRACKETS)){
                    if(e.getQu()==null){
                        return null;
                    }else{
                        boolQueryBuilder.must(e.getQu());
                    }
                }
            }
            return boolQueryBuilder;
        }
    },
    RIGHTRACKETS(Constant.rightBrackets){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            return new NestElement(null,null,ElementTypeEnum.RIGHTRACKETS);
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return f1;
        }
    },
    DEFUALT(Constant.COMMA){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            ElementMeta em = new ElementMeta(value,null,ElementTypeEnum.DEFUALT);
            return em;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    },
//    POINT(Constant.POINT){
//        @Override
//        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
//            ElementMeta em = new ElementMeta(value,null,ElementTypeEnum.POINT);
//            return em;
//        }
//
//        @Override
//        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
//            return null;
//        }
//    },
    BLANK(Constant.blank){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            ElementMeta em = new ElementMeta(value,null,ElementTypeEnum.BLANK);
            return em;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    },
    COLUMN(Constant.COLUMN){
        @Override
        public Element instanceElement(String value,MetaDataStatement metaDataStatement) {
            ElementMeta em = new ElementMeta(value,null,ElementTypeEnum.COLUMN);
            return em;
        }

        @Override
        public QueryBuilder buildQueryBuilder(Element element, MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2) {
            return null;
        }
    }
    ;
    private String fun;
    ElementTypeEnum(String fun){
        this.fun = fun;
    }
    public abstract Element instanceElement(String value,MetaDataStatement metaDataStatement);

    public abstract Object buildQueryBuilder(Element element,MetaDataStatement mds,QueryBuilder f1,QueryBuilder f2);
    public String getKey(){
        return this.fun;
    }

    public static ElementTypeEnum[] searchFun(String str){
        List<ElementTypeEnum> elementTypeEnumList = Arrays.stream(ElementTypeEnum.values()).filter(val->str.equalsIgnoreCase(val.getKey())).collect(Collectors.toList());
        if(elementTypeEnumList==null || elementTypeEnumList.isEmpty()){
            return new ElementTypeEnum[]{ElementTypeEnum.COLUMN};
        }
        return elementTypeEnumList.toArray(new ElementTypeEnum[elementTypeEnumList.size()]);
    }
}

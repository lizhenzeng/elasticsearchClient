package com.tigerobo.search.parser;

import com.tigerobo.search.entity.*;
import com.tigerobo.search.utils.ReflectUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class SqlParser {

    private MetaDataStatement mds;
    public SqlParser(MetaDataStatement mds){
        this.mds = mds;
    }

    public List<SortBuilder> parserOrderBy(Element element){
        return element.getElements().stream().map(val->(SortBuilder)val.getType().buildQueryBuilder(val,mds,null,null)).collect(Collectors.toList());
    }


    public Element parserWhere(Element element){
        Stack<Element> stack = new Stack<>();
        Set<Element> repeat = new HashSet<>();
        if(element.size()>0){
            parserWhere(element,stack,repeat,-1,0);
        }
        if(element.getQu()==null){
            try {
                stack.add(element);
                parserRetry(stack,repeat);
            }catch (Exception  e){
                e.printStackTrace();
            }
        }
        return element;
    }

    public synchronized QueryBuilder parserSymbolMeta(Element first){
        QueryBuilder fq = null;
        if(first instanceof SymbolMeta ){
            fq = (QueryBuilder) first.getType().buildQueryBuilder(first,mds, null,null);
            first.setQu(fq);
        }
        return fq;
    }

    public Boolean canParseSymbolMeta(Element first){
        return first.getParamterName()!=null ;
    }



    public synchronized QueryBuilder parserFunctionMeta(Element first){
        QueryBuilder fq = null;

        if(first instanceof FunctionMeta){
            if(first.size() == 2){
                fq = (QueryBuilder) first.getType().buildQueryBuilder(first,mds, first.getElements().get(0).getQu(),first.getElements().get(1).getQu());
                first.setQu(fq);
                if(fq == null){
                    return null;
                }
            }
            if(first.size() == 1){
                fq = (QueryBuilder) first.getType().buildQueryBuilder(first,mds, first.getElements().get(0).getQu(),null);
                first.setQu(fq);
                if(fq == null){
                    return null;
                }
            }
        }
        return fq;
    }

    public Boolean canParserFunctionMeta(Element first){
        if(first.getType().equals(ElementTypeEnum.WHERE)){
            QueryBuilder qu = (QueryBuilder) ElementTypeEnum.WHERE.buildQueryBuilder(first,mds,null,null);
            first.setQu(qu);
            return qu!=null;
        }
        if(first.size()==2){
            return first.getElements().get(0).getQu()!=null && first.getElements().get(1).getQu()!=null;
        }
        if(first.size() == 1){
            return first.getElements().get(0).getQu()!=null;
        }
        return false;
    }

    public synchronized QueryBuilder parserNestElement(Element first){
        QueryBuilder fq = null;
        if(first instanceof NestElement){
            fq = (QueryBuilder) first.getType().buildQueryBuilder(first,mds, null, null);
            first.setQu(fq);
        }
        return fq;
    }

    public Boolean canParserNestElemnt(Element first){
        if(first.size()>0){
            while (first.hasNext()){
                Element ele = first.next();
                if( ele.getQu() == null){
                    return false;
                }
            }
        }
        return true;
    }

    public void parserRetry(List<Element> stack, Set<Element> repeat) throws Exception {
        if(stack!=null && stack.size()>0){
            for(Element es:stack){
                parserElement(es,stack,repeat,NestElement.class,"canParserNestElemnt","parserNestElement",false);
                parserElement(es,stack,repeat,FunctionMeta.class,"canParserFunctionMeta","parserFunctionMeta",false);
                parserElement(es,stack,repeat,SymbolMeta.class,"canParseSymbolMeta","parserSymbolMeta",false);
            }
        }

    }

    public void parserElement(Element es, List<Element> stack,Set<Element> repeat
            ,Class clzz,String canParserMethodName,String processMethodName,Boolean isNeedRetry) throws Exception {
        if(es.getClass().isAssignableFrom(clzz)){
            Method canParserMethod = ReflectUtils.getCloseMethodByNameAndParamtized(SqlParser.class,canParserMethodName,es.getClass());
            Method processMethod = ReflectUtils.getCloseMethodByNameAndParamtized(SqlParser.class,processMethodName,es.getClass());
            if(canParserMethod!=null && processMethod!=null){
                canParserMethod.setAccessible(true);
                Boolean flag = (Boolean) canParserMethod.invoke(this,new Object[]{es});
                if(flag){
                    processMethod.setAccessible(true);
                    processMethod.invoke(this,new Object[]{es});
                    if(isNeedRetry){
                        parserRetry(stack,repeat);
                    }
                }else{
                    if(!repeat.contains(es)){
                        stack.add(es);
                        repeat.add(es);
                    }
                }
            }

        }
    }



    public void parserWhere(Element elements, List<Element> stack, Set<Element> repeat,int depth,int count){

        try {
            parserElement(elements,stack,repeat,NestElement.class,"canParserNestElemnt","parserNestElement",true);
            parserElement(elements,stack,repeat,FunctionMeta.class,"canParserFunctionMeta","parserFunctionMeta",true);
            parserElement(elements,stack,repeat,SymbolMeta.class,"canParseSymbolMeta","parserSymbolMeta",true);
            if(elements.size()>0 && (depth > count || depth ==-1)){
                count = count+1;
                for(Element element:elements.getElements()){
                    parserWhere(element,stack,repeat,depth,count);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}

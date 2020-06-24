package com.tigerobo.search.parser;

import com.tigerobo.search.constant.Constant;
import com.tigerobo.search.entity.Element;
import com.tigerobo.search.entity.ElementTypeEnum;
import com.tigerobo.search.entity.FunctionMeta;
import com.tigerobo.search.entity.MetaDataStatement;
import com.tigerobo.search.utils.Validation;
import com.tigerobo.search.validation.StringValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ReadHelper {


    public static List<String> paris = new ArrayList<>();

    public static MetaDataStatement mds;

    public ReadHelper(MetaDataStatement mds) {
        this.mds = mds;
    }

    public ReadHelper(){}
    static {
        paris.addAll(Arrays.asList(new String[]{
//                Constant.POINT,
                Constant.COMMA, Constant.leftBrackets
                , Constant.greatAndEqual, Constant.lessAndEqual
                , Constant.less, Constant.great, Constant.like, Constant.equalToken, Constant.equal, Constant.rightBrackets
        }));
    }

    public List<Element> getElement() {
        //first split sql
        List<Element> result = new ArrayList<>();
        String[] segments = splitStr(mds.getSql());
        for (String seg : segments) {
            ElementTypeEnum[] elements = ElementTypeEnum.searchFun(seg);
            if (elements == null || elements.length > 1) {
                throw new ParserException(String.format("parser is error, the error str is %s", seg));
            }
            result.add(elements[0].instanceElement(seg, mds));
        }

        Stack<Element> stack = new Stack<>();
        for (Element ele : result) {
            if (isNeedMerge(ele)) {
                Element element = stack.peek();
                element.addElement(ele);
            } else {
                stack.push(ele);
            }
        }
        stack.forEach(val -> {
            if (val instanceof FunctionMeta) {
                ((FunctionMeta) val).merge();
            }
        });

        return stack;
    }


    public boolean isNeedMerge(Element pre) {
        if (pre.getType().equals(ElementTypeEnum.SELECT) || pre.getType().equals(ElementTypeEnum.FROM)
                || pre.getType().equals(ElementTypeEnum.WHERE) || pre.getType().equals(ElementTypeEnum.ORDERBY)
        ) {
            return false;
        }
        return true;
    }

    public String getSymbolOfStr(List<String> symbol, String value, List<String> excludes) {
        List<String> var3 = new ArrayList<>();
        for (String var : symbol) {
            if (!excludes.contains(var)) {
                var3.add(var);
            }
        }
        for (String s : var3) {
            if (StringValidationUtils.isNotBlank(value) && value.indexOf(s) != -1) {
                return s;
            }
        }
        return null;
    }

    public String[] splitStrByBlankExceptPoint(String str) {

        List<Integer> indexs = new ArrayList<>();
        getPointIndexOfStr(str, "'", indexs);
        List<String> result = new ArrayList<>();
        if (indexs != null && !indexs.isEmpty()) {
            String[] includeStrs = getSplitStr(indexs, str, true);
            String[] excludeStrs = getSplitStr(indexs, str, false);
            for (int i = 0; i < includeStrs.length + excludeStrs.length; i++) {
                if (i % 2 == 1) {
                    result.add(includeStrs[i / 2]);
                } else {
                    result.addAll(Arrays.stream(excludeStrs[i / 2].split(Constant.blank)).collect(Collectors.toList()));
                }
            }
        }else{
            result.addAll(Arrays.stream(str.split(Constant.blank)).collect(Collectors.toList()));
        }
        result = result.stream().filter(val->Validation.notEmptyAndBlankStr(val)).collect(Collectors.toList());
        return result.toArray(new String[result.size()]);
    }

    public String[] getSplitStr(List<Integer> indexs, String str, boolean includeOrexclude) {
        if (!includeOrexclude) {
            Integer[] excludeIndexs = new Integer[indexs.size() + 2];
            System.arraycopy(indexs.toArray(new Integer[indexs.size()]), 0, excludeIndexs, 1, indexs.size());
            excludeIndexs[0] = 0;
            excludeIndexs[excludeIndexs.length - 1] = str.length();
            indexs = Arrays.stream(excludeIndexs).collect(Collectors.toList());
        }
        int modNum = indexs.size() % 2;
        if (modNum != 0) {
            throw new ParserException("the parser is error");
        }
        List<String> include = new ArrayList<>();
        for (int i = 0; i < indexs.size() / 2; i++) {
            if (!includeOrexclude) {
                include.add(str.substring(indexs.get(i * 2)==0?0:indexs.get(i * 2)+1, indexs.get(i * 2 + 1) ));
            } else {
                include.add(str.substring(indexs.get(i * 2) + 1, indexs.get(i * 2 + 1)));
            }
        }
        include = include.stream().filter(val -> Validation.notEmptyAndBlankStr(val)).collect(Collectors.toList());
        return include.toArray(new String[include.size()]);
    }

    public void getPointIndexOfStr(String str, String symbol, List<Integer> indexs) {
        Integer index = str.indexOf(symbol);
        if (index != -1) {
            if (indexs.isEmpty()) {
                indexs.add(index);
            } else {
                indexs.add(indexs.get(indexs.size() - symbol.length()) + index + symbol.length());
            }
            String temp = str.substring(index);
            if (temp.indexOf(symbol) == 0) {
                temp = temp.substring(1);
            }
            getPointIndexOfStr(temp, symbol, indexs);
        }
    }

    public String[] splitStr(String sql) {
        String[] splitArray = splitStrByBlankExceptPoint(sql);
        for (int i = 0; i < splitArray.length; i++) {
            String item = null;
            List<String> excludes = new ArrayList<>();
            do {
                String value = splitArray[i];
                item = getSymbolOfStr(paris, value, excludes);


                if (item != null) {
                    if(excludes.contains(Constant.equalToken) && item.equalsIgnoreCase(Constant.equal)){

                    }else{
                        String[] var2 = StringHelper.splitAddSplitSymbol(value, item);
                        splitArray = StringHelper.addValueByIndex(splitArray, var2, i, String.class);
                        i = i - 1 + var2.length;
                    }
                    excludes.add(item);
                }

            } while (item != null);

        }
        return splitArray;
    }


}

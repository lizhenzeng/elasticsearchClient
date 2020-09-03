package com.tigerobo.search.condition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConditionParser {

    //
    public <T extends AbstractCondition> List<T> parser(String str,Class<T> clzz) {
        try {
            List<T> conditions = new ArrayList<>();
            T t = clzz.newInstance();
            List<String> conditionFragmentStrs = getMatchStrByReg(str, t.getConditionFragmentReg());
            for(String conditionFragmentStr:conditionFragmentStrs){
                List<String> ifConditionStrs = new ArrayList<>();
                getConditionFragmentByIfSymbol(conditionFragmentStr,ifConditionStrs);
                for(String condition:ifConditionStrs){
                    t.setOriginalSql(str);
                    t.setConditionJudgeStr(getMatchStrByReg(condition, t.getConditionJudgeReg()).get(0));
                    t.setConditionSuccessFragmentStr(getMatchStrByReg(condition, t.getConditionSuccessFragmentReg()).get(0));
                    t.setConditionFragmentStr(condition);
                    conditions.add(t);
                    t = clzz.newInstance();
                }

            }
           return conditions;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void getConditionFragmentByIfSymbol(String conditionFragmentStrs,List<String> matchIfConditionValues){
         Integer endIndex =  conditionFragmentStrs.indexOf("</if>");
         if(endIndex!=-1){
             matchIfConditionValues.add(conditionFragmentStrs.substring(0,endIndex+5));
             getConditionFragmentByIfSymbol(conditionFragmentStrs.substring(endIndex+5),matchIfConditionValues);
         }
    }



    public List<String> getSplitIndex(List<String> startRegs, List<String> endRegs, String value) {
        List<Integer> startIndexs = new ArrayList<>();
        getPointIndexOfStr(value,startRegs,startIndexs,null);
        List<Integer> endIndexs = new ArrayList<>();
        List<String> endRegCopy = new ArrayList<>();
        endRegCopy.addAll(endRegs);
        getPointIndexOfStr(value,endRegs,endIndexs,null);
        for(int i =0;i<endIndexs.size(); i++){
            endIndexs.set(i, endIndexs.get(i)+endRegCopy.get(i).length());
        }
        List<String> relation = getNearIntegerInList(startIndexs, endIndexs);
        return relation.stream().map(val->value.substring(Integer.valueOf(val.split("-")[0]),Integer.valueOf(val.split("-")[1]))).collect(Collectors.toList());
    }
    public void getPointIndexOfStr(String str, List<String> symbol, List<Integer> indexs,String resolve) {
        for (int i = 0; i < symbol.size(); i++) {
            Integer index = str.indexOf(symbol.get(i));
            if (index != -1) {
                if(!indexs.isEmpty()){
                        indexs.add(indexs.get(indexs.size()-1) + (resolve==null?0:resolve.length())+index);
                }else{
                        indexs.add(index);
                }
                resolve = symbol.get(i);
                str = str.substring(index+resolve.length());
                symbol.remove(i);
                i--;
                getPointIndexOfStr(str,symbol,indexs,resolve);
            }
        }
    }
    // 120<if> 130<if> 140</if> 150</if>
    public List<String> getNearIntegerInList(List<Integer> start, List<Integer> end) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < end.size(); i++) {
            Integer var = end.get(i);
            List<Integer> vars2 = start.stream().filter(val -> val < var).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            if (vars2 != null && !vars2.isEmpty()) {
                res.add(vars2.get(0) + "-" + var);
                end.remove(vars2.get(0));
            }
        }
        return res;
    }

    public List<String> getMatchStrByReg(String str, String reg) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            //每一个符合正则的字符串
            result.add(matcher.group());
        }
        return result;
    }


}

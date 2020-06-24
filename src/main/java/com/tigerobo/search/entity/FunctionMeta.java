package com.tigerobo.search.entity;

import com.tigerobo.search.constant.Constant;
import com.tigerobo.search.parser.ParserException;
import com.tigerobo.search.utils.Validation;

import java.util.*;
import java.util.stream.Collectors;

public class FunctionMeta extends Element {

    private MetaDataStatement metaDataStatement;

    public FunctionMeta(String paramterName, String compareValue, ElementTypeEnum type, MetaDataStatement metaDataStatement) {
        super(paramterName, compareValue, type);
        this.metaDataStatement = metaDataStatement;
    }


    public void merge() {
        switch (this.getType()) {
            case SELECT:
                mergeSelect(this, metaDataStatement);
                break;
            case FROM:
                mergeFrom(this, metaDataStatement);
                break;
            case WHERE:
                mergeWhere(this, metaDataStatement);
                break;
            case ORDERBY:
                mergeOrder(this, metaDataStatement);
                break;
        }
    }

    private void mergeOrder(Element element, MetaDataStatement metaDataStatement) {
        while (element.hasNext()) {
            Element var = element.next();
            if (var.getType().equals(ElementTypeEnum.BY)) {
                element.remove(var);
            }
            if (var.getType().equals(ElementTypeEnum.DEFUALT)) {
                element.remove(var);
            }
        }
        List<Element> eles = element.getElements();
        for (int i = 0; i < element.size(); i++) {
            Element var = eles.get(i);
            if (var instanceof FunctionMeta) {
                Element var1 = eles.get(i - 1);
                if (var1 instanceof ElementMeta) {
                    var.setCompareValue(var1.getParamterName());
                    eles.remove(var1);
                    i--;
                }
            }
        }
    }

    private void mergeSelect(Element element, MetaDataStatement metaDataStatement) {

        while (element.hasNext()) {
            Element var = element.next();
            if (var.getType().equals(ElementTypeEnum.DEFUALT)) {
                element.remove(var);
            }
        }
    }

    private void mergeFrom(Element element, MetaDataStatement metaDataStatement) {
        if (element.size() == 3 || element.size() == 1) {
            if (element.size() > 1) {
                element.setPrefix(element.getElements().get(2).getParamterName());
            }
            String indexAndType = element.getElements().get(0).getParamterName();
            if (Validation.notEmptyAndBlankStr(indexAndType) && indexAndType.contains(Constant.POINT)) {
                element.setParamterName(indexAndType.split(Constant.POINTREG)[0]);
                element.setPrefix(indexAndType.split(Constant.POINTREG)[1]);
            } else {
                element.setParamterName(indexAndType);
            }

        } else {
            throw new ParserException("error parser please check index is exits or missing");
        }


    }


    private Element mergeWhere(Element element, MetaDataStatement metaDataStatement) {
        //合并参数
        List<Element> eles = element.getElements();
        for (int i = 0; i < eles.size(); i++) {
            Element var = eles.get(i);
            try {
                if (var instanceof SymbolMeta) {
                    var.setPrefix(var.getParamterName());
                    var.setParamterName(eles.get(i - 1).getParamterName());
                    var.setCompareValue(eles.get(i + 1).getParamterName());
                    eles.remove(i + 1);
                    eles.remove(i - 1);
                    i = i - 1;
                }
            } catch (Exception e) {
                throw new ParserException(String.format("error parser where condition,please check %s around", var.getParamterName()));
            }

        }
        Element result = null;
        //合并括号
        Stack<Element> stack = new Stack();
        for (int i = 0; i < eles.size(); i++) {
            Element var = eles.get(i);
            try {
                if (var instanceof NestElement && var.getType().equals(ElementTypeEnum.LEFTBRACKETS)) {
                    if (stack.size() > 0) {
                        Element ele = stack.peek();
                        ele.addElement(var);
                    }
                    stack.push(var);
                } else if (var instanceof NestElement && var.getType().equals(ElementTypeEnum.RIGHTRACKETS)) {
                    result = stack.pop();
                    result.addElement(var);
                } else {
                    if (stack.size() > 0) {
                        Element ele = stack.peek();
                        ele.addElement(var);
                        eles.remove(i);
                        i = i - 1;
                    } else {
                        result = new Element(null, null, ElementTypeEnum.WHERE);
                        stack.push(result);
                        result.addElement(var);
                    }
                }

            } catch (Exception e) {
                throw new ParserException(String.format("error parser where condition,please check %s around", var.getParamterName()));
            }

        }

        //递归合并条件
        if (result.getElements().size() > 0) {
            recusiveMergeFunc(result.getElements());
        }
        if (result.getElements().size() > 0) {
            mergeRangeFuc(result.getElements());
        }

        this.clear();
        this.addAllElement(result.getElements());
        setParamterName(result.getParamterName());
        setCompareValue(result.getCompareValue());
        setType(result.getType());
        return result;
    }

    private void getElementBinds(Map<String, Element> colsBindElement, Map<Element, Element> elementBinds, Element var1) {
        for (Element var : var1.getElements()) {
            if (Validation.notEmptyAndBlankStr(var.getParamterName())) {
                Element temp = colsBindElement.get(var.getParamterName());
                if (temp == null) {
                    colsBindElement.put(var.getParamterName(), var);
                } else {
                    if (var.getPrefix().contains(">")) {
                        elementBinds.put(temp, var);
                    } else {
                        elementBinds.put(var, temp);
                    }
                }
            }
        }
    }

    private Map<Element, Element> isSameColInRelations(Element var1, Element var2) {
        Map<String, Element> colsBindElement = new HashMap<>();
        Map<Element, Element> elementBinds = new HashMap<>();
        if (var1.size() > 0 && var2.size() > 0) {
            getElementBinds(colsBindElement, elementBinds, var1);
            getElementBinds(colsBindElement, elementBinds, var2);
        }
        elementBinds.entrySet().forEach(val -> {
            var1.remove(val.getKey());
            var1.remove(val.getValue());
        });
        elementBinds.entrySet().forEach(val -> {
            var2.remove(val.getKey());
            var2.remove(val.getValue());
        });
        return elementBinds;
    }


    private void mergeRangeFuc(List<Element> eles) {
        Map<Element, Element> colsBindElement = new HashMap<>();
        for (int i = 0; i < eles.size(); i++) {
            for (int j = i + 1; j < eles.size(); j++) {
                Element var1 = eles.get(i);
                Element var2 = eles.get(j);
                colsBindElement.putAll(isSameColInRelations(var1, var2));
                if (var1.size() == 0) {
                    eles.remove(var1);
                    i--;
                }
                if (var2.size() == 0) {
                    eles.remove(var2);
                    j--;
                }
            }
        }
        if (colsBindElement.size() > 0) {
            List<Element> rangeElements = colsBindElement.entrySet().stream()
                    .map(val -> new SymbolMeta(val.getKey().getParamterName(), val.getKey().getCompareValue(),
                            val.getValue().getCompareValue(), ElementTypeEnum.RANGE, metaDataStatement)).collect(Collectors.toList());
            eles.addAll(rangeElements);
        }
    }

    private void recusiveMergeFunc(List<Element> eles) {
        for (int i = 0; i < eles.size(); i++) {
            Element var = eles.get(i);

            try {
                if (var instanceof FunctionMeta) {
                    if (i + 1 < eles.size() && eles.get(i + 1) instanceof SymbolMeta) {
                        var.addElement(eles.get(i + 1));
                    }
                    if (eles.get(i - 1) instanceof SymbolMeta) {
                        var.addElement(eles.get(i - 1));
                    }
                    if (i + 1 < eles.size() && eles.get(i + 1) instanceof SymbolMeta) {
                        eles.remove(i + 1);
                    }
                    if (eles.get(i - 1) instanceof SymbolMeta) {
                        eles.remove(i - 1);
                        i = i - 1;
                    }


                }
            } catch (Exception e) {
                throw new ParserException(String.format("error parser where condition,please check %s around", var.getParamterName()));
            }
            if (var.size() > 0) {
                recusiveMergeFunc(var.getElements());
            }

        }
    }

}

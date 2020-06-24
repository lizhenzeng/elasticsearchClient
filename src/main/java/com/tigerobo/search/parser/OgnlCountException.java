package com.tigerobo.search.parser;

/**
 * @title: CallResourceException
 * @description: 联系资源异常
 * @author: lzy
 * @data: 2019/9/07
 */
public class OgnlCountException extends RuntimeException {
    public String msg;
    public OgnlCountException(String msg) {
        super(msg);
    }
    public String getMsg(){
        return this.msg;
    }

}

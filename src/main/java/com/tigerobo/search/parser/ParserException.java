package com.tigerobo.search.parser;

public class ParserException extends RuntimeException {
    private String msg;
    public ParserException(String msg) {
        super(msg);
    }
    public String getMsg(){
        return this.msg;
    }

}

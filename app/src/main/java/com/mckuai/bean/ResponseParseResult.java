package com.mckuai.bean;

/**
 * Created by kyly on 2015/6/29.
 */
public class ResponseParseResult {
    public boolean isSuccess ;
    public String msg;
    public String data;

    public  ResponseParseResult(){
        isSuccess = false;
        msg = "";
    }
}

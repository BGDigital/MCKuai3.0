package com.mckuai.bean;

import com.google.common.base.Objects;

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

    public String toString(){
        String result = "{\"dataObject\":\"" + data + "\"}" ;
        return result;
    }
}

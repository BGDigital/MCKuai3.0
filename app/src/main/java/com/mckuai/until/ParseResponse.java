package com.mckuai.until;

import android.support.design.widget.TabLayout;
import android.util.Log;

import com.mckuai.bean.ResponseParseResult;

import org.json.JSONObject;

/**
 * Created by kyly on 2015/6/29.
 */
public class ParseResponse {

    private static  final  String TAG = "ParseResponse";

    /**
     * 获取解析后的数据
     * 统一的解析方式，将从网络上获取的数据解析成为ResponseParseResult格式的数据以方便处理
     * @param response 要解析的Object数据
     * @return 解析成功返回ResponseParseResult对象，否则返回空
     */
    public ResponseParseResult parse(JSONObject response){
        ResponseParseResult result = new ResponseParseResult();
        if (null == response){
            result.msg = "返回数据为空！";
            return  result;
        }

        if (!response.has("state")){
            result.msg = "返回数据格式不正确！";
        }

        try {
            if (response.getString("").equalsIgnoreCase("ok")){
                result.data = response.getString("dataObject");
                result.isSuccess = true;
            }
            else{
                if (response.has("msg")) {
                    result.msg = response.getString("msg");
                }
                else {
                    result.msg = "获取数据失败，原因未知！";
                }
            }
        }
        catch (Exception e){
            result.msg = "解析数据失败，原因："+e.getLocalizedMessage();
            Log.w(TAG,result.msg);
        }
        return  result;
    }
}

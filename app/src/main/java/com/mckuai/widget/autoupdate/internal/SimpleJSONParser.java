package com.mckuai.widget.autoupdate.internal;

import com.mckuai.widget.autoupdate.ResponseParser;
import com.mckuai.widget.autoupdate.Version;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SimpleJSONParser implements ResponseParser {

	@Override
	public Version parser(String response) {
		try{
			JSONTokener jsonParser = new JSONTokener(response);
			JSONObject json = (JSONObject) jsonParser.nextValue();
			boolean success = json.getBoolean("success");
			Version version = null;
			if(success){
				JSONObject dataField = json.getJSONObject("data");
				int code = dataField.getInt("code");
				String name = dataField.getString("version");
				String feature = dataField.getString("content");
				String targetUrl = dataField.getString("downloadUrl");
				version = new Version(code, name, feature, targetUrl);
			}
			return version;
		}catch(JSONException exp){
			exp.printStackTrace();
			return null;
		}
	}

}

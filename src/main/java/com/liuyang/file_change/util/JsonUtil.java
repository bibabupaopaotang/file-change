package com.liuyang.file_change.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.io.*;

public class JsonUtil {

    private JsonUtil(){

    }

    public static String getString(JSONObject json, String key, String defaultValue){
        return json.containsKey(key) ? json.getString(key) :defaultValue;
    }

    public static JSONObject removeKey(JSONObject json, String key){
        if(json.containsKey(key)){
            json.remove(key);
        }
        return json;
    }

    public static String readJsonStringFromFile(String path, String fileName) throws IOException {

        String data = "";
        InputStream is = StringUtils.isEmpty(path) ? JsonUtil.class.getClassLoader().getResourceAsStream(fileName)
                : new FileInputStream(path + File.separator + fileName);
        if(null != is){
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder out = new StringBuilder();
            String line = null;
            while((line = reader.readLine())!= null){
                out.append(line);
            }
            data = out.toString();

        }
        return data;
    }
}

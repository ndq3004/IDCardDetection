package com.example.myapplicationdemo;

import android.util.Log;
import android.widget.TextView;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Response;

public class ResponseHandler {
    public static void handleResponse(Response response, TextView textView){
        Map<String, String> fieldName = new HashMap<String, String>();
        fieldName.put("id", "Số");
        fieldName.put("name", "Họ tên");
        fieldName.put("birth", "Ngày sinh");
        fieldName.put("country", "Quê quán");
        fieldName.put("home", "Nơi ĐKHK thường trú");

        try {
            String res = response.body().string();
            res = res.replace("\\", "");
            res = res.replace("\'", "\"");
            res = res.substring(res.indexOf("{"), res.lastIndexOf("}") + 1);
            JSONObject reader = new JSONObject(res);
            boolean isSuccess = reader.getBoolean("success");

            if(isSuccess){
                String info = reader.getString("predictions");
                byte[] tmp2 = Base64.decode(info);
                String val2 = new String(tmp2, "UTF-8");
                reader = new JSONObject(val2);
                String result = "";
                Iterator<String> iter = reader.keys();
                while (iter.hasNext()){
                    String key = iter.next();
                    result += String.format("%s: %s \n", fieldName.get(key), reader.getString(key));
                }
                textView.setText(result);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void test() throws JSONException {
        String sample = "{'success': True, 'predictions': {'infomation': {'id': '142885669', 'name': 'NGUYỄN THỊ MINH NGỌC', 'birth': '03-04-1998', 'country': 'Nguyễn quân Tram TT Thanh Miện Thị Thanh Miên Hải Dương ', 'home': 'Nơi ĐKHK thương trú TT.Thanh miện Thanh Miên Hải Dương '}}}";
        JSONObject obj = new JSONObject(sample);
        System.out.println(obj.getJSONObject("predictions")); //John
    }
}
class IDInfo{
    public String id;
    public String name;
    public String birth;
    public String country;
    public String home;
}
package com.xxxx.note.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpResponse;

public class JsonUtil {

    /**
     * 将对象转换成JSON类型的字符串，进行ajax的调用的
     * @param resp
     * @param result
     */
    public static void toJson(HttpServletResponse resp, Object result){

        try {
            //设置响应类型和编码格式
            resp.setContentType("application/json;charset=UTF-8");
            //得到字符输出流
            PrintWriter out = resp.getWriter();
            //通过fastjson的方法，将ResultInfo对象转换成JSON格式的字符串
            String json = JSON.toJSONString(result);
            //通过输出流输出JSON字符串
            out.write(json);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

package com.mol.ddmanage.Util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HttpCommunication
{
    public static String HttpGet(String Url)
    {
        try {
            URL url = new URL(Url/*"http://140.249.22.202:8082/ac/deploy?name=1111&processId=testname&processName=test1&orgId=1202851016954982400&buyChannelId="+buy_channel_id*/);//实例化一个URL对象，用百度有道翻译进行了测试/hello
            URLConnection connection = url.openConnection();//通过URL对象的openConnection()方法得到一个java.net.URLConnection;
            InputStream is = connection.getInputStream();  // 获取输入流
            InputStreamReader isr = new InputStreamReader(is,"utf-8");//在InputStreamReader中指定编码，手动指定为UTF-8
            BufferedReader br = new BufferedReader(isr);//实例化一个BufferedReader对象用来存放转换后的字符
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {  // 读取数据
                builder.append(line+"\n");
            }
            br.close();//关闭流
            isr.close();
            is.close();
            return builder.toString();
           // System.out.println(builder.toString());
        }
        catch (Exception e)
        {
            return e.toString();
        }

    }
}

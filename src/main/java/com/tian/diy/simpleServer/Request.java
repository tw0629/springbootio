package com.tian.diy.simpleServer;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author David Tian
 * @desc
 * @since 2020-03-17 18:25
 */
public class Request
{
    private InputStream input;
    private String uri;

    public Request(InputStream input) {
        this.input = input;
    }

    public void parse()
    {
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];

        //先将input 读到字节数组buffer, 再将字节数组buffer 读到StringBuffer request里面
        try
        {
            // 读取流中内容
            i = input.read(buffer);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            i = -1;
        }

        for (int j=0; j<i; j++)
        {
            // 将每个字节转换为字符
            request.append((char) buffer[j]);
        }
        System.out.print(request.toString());
        // 根据转换出来的字符解析URI
        uri = parseUri(request.toString());
    }

    private String parseUri(String requestString)
    {
        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1)
                return requestString.substring(index1 + 1, index2);
        }
        return null;
    }

    public String getUri()
    {
        return uri;
    }
}
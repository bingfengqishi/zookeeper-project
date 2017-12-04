package cn.com.zkconfigmanagement.consumer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lenovo on 2017/12/1.
 * httpCient工具类
 */
public class HttpClientUtil {
    public static String getMethod(String path) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(path);
        HttpResponse httpResponse = client.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        String res = "";
        InputStream inputStream = httpEntity.getContent();
        res = streamToString(inputStream);
        return  res;
    }

    private  static  String streamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer bf = new StringBuffer();
        String line = null;
        try {
            while ((line=bufferedReader.readLine())!=null){
                bf.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            inputStream.close();
        }
        return  bf.toString();
    }
}

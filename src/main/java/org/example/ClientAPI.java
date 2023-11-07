package org.example;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientAPI extends Thread{
    String IPAddr;
    CountDownLatch latch;

    int threadGroupId;  // 第几个线程组

    int threadId;    // 第几个线程
    ConcurrentHashMap<String, AtomicInteger> map;


    public ClientAPI(String IPAddr,CountDownLatch latch,int threadGroupId, int threadId,ConcurrentHashMap<String, AtomicInteger> map){
        this.IPAddr = IPAddr;
        this.latch = latch;
        this.threadGroupId = threadGroupId;
        this.threadId = threadId;
        this.map = map;
    }

    public void get() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            long start = System.currentTimeMillis();
            HttpGet httpget = new HttpGet(IPAddr);
            CloseableHttpResponse response = httpclient.execute(httpget);
//                HttpEntity entity = response.getEntity();
//                String result = EntityUtils.toString(entity);
//                EntityUtils.consume(entity);
            long end = System.currentTimeMillis();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/youyun/Documents/java project/client2/src/main/resources/result/java5.csv", true));
            if (response.getStatusLine().getStatusCode() == 200) {
                bufferedWriter.append(String.valueOf(start)).append(" Get ").append(String.valueOf(end - start)).append(" ms status:200\n");
                map.get("success").incrementAndGet();
            } else {
                bufferedWriter.append(String.valueOf(start)).append(" Get ").append(String.valueOf(end - start)).append(" ms status:" + response.getStatusLine().getStatusCode() + "\n");
                map.get("fail").incrementAndGet();
            }
            bufferedWriter.close();
            map.get("success").incrementAndGet();
        } catch (Exception e){
            e.printStackTrace();
            map.get("fail").incrementAndGet();
        }
    }

    public void post() throws Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // Create a MultipartEntityBuilder
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // This attaches the file to the POST:
        File file = new File("/Users/youyun/Documents/java project/client2/src/main/resources/image/WechatIMG740.jpg");
        builder.addBinaryBody(
                "image", // This should be the name of the field expected by the server
                new File("/Users/youyun/Documents/java project/client2/src/main/resources/image/WechatIMG740.jpg"),
                ContentType.APPLICATION_OCTET_STREAM,
                file.getName()
        );

        // This attaches another form field with the file description
        builder.addTextBody("artist", "yummy");
        builder.addTextBody("year", "2000");
        builder.addTextBody("title", "xixi");
        // Build the multipart entity
        HttpEntity multipart = builder.build();

        try {
            long start = System.currentTimeMillis();
            HttpPost httpPost = new HttpPost(IPAddr);
            // Set the multipart entity as the request entity
            httpPost.setEntity(multipart);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            long end = System.currentTimeMillis();

            //HttpEntity entity = response.getEntity();
            //String result = EntityUtils.toString(entity);
            //EntityUtils.consume(entity);

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/youyun/Documents/java project/client2/src/main/resources/result/java5.csv",true));
            if (response.getStatusLine().getStatusCode()==200){
                bufferedWriter.append(String.valueOf(start)).append(" Post ").append(String.valueOf(end - start)).append(" ms status:200\n");
                map.get("success").incrementAndGet();
            }else{
                bufferedWriter.append(String.valueOf(start)).append(" Post ").append(String.valueOf(end - start)).append(" ms status:"+response.getStatusLine().getStatusCode()+"\n");
                map.get("fail").incrementAndGet();
            }
            bufferedWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        for (int i = 0; i < 1000; i++) {
            System.out.println("thread group " + threadGroupId + " 中的第 " + threadId + " 个thread "+(i+1)+" 个请求");
            for (int retry = 0; retry < 5; retry++) {
                try {
                    post();
                    break;
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("crowded");
                }
            }
            for (int retry = 0; retry < 5; retry++) {
                try {
                    get();
                    break;
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("crowded");
                }
            }
        }
        latch.countDown();
    }
}

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

    // JDBC URL
    String writeEndpoint = "database-1.cxbykd0hqw1f.us-west-2.rds.amazonaws.com";

    String[] temp = {"mydbinstance.cxbykd0hqw1f.us-west-2.rds.amazonaws.com"};
    List<String> readEndpoints = Arrays.asList(temp);

    DatabaseConnectionFactory databaseConnectionFactory;
    public ClientAPI(String IPAddr,CountDownLatch latch,int threadGroupId, int threadId,ConcurrentHashMap<String, AtomicInteger> map){
        this.IPAddr = IPAddr;
        this.latch = latch;
        this.threadGroupId = threadGroupId;
        this.threadId = threadId;
        this.map = map;
        this.databaseConnectionFactory = new DatabaseConnectionFactory(writeEndpoint,readEndpoints);
    }

    public void get() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection conn = null;
        long start = System.currentTimeMillis();
        try {
            conn = databaseConnectionFactory.getReadConnection();

            String sql = "select * from album where album_id = 604";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            conn.close();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/youyun/Documents/java project/client2/src/main/resources/result/java6.csv", true));
//            HttpGet httpget = new HttpGet(IPAddr);
//            try (CloseableHttpResponse response = httpclient.execute(httpget)) {
//                HttpEntity entity = response.getEntity();
//                String result = EntityUtils.toString(entity);
//                EntityUtils.consume(entity);
//                long end = System.currentTimeMillis();
//                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/youyun/Documents/java project/client2/src/main/resources/result/java1.csv", true));
//                if (response.getStatusLine().getStatusCode() == 200) {
//                    bufferedWriter.append(String.valueOf(start)).append(" Get ").append(String.valueOf(end - start)).append(" ms status:200\n");
//                    map.get("success").incrementAndGet();
//                } else {
////                    bufferedWriter.append(String.valueOf(start)).append(" Get ").append(String.valueOf(end - start)).append(" ms status:" + response.getStatusLine().getStatusCode() + "\n");
//                    map.get("fail").incrementAndGet();
//                }
//                bufferedWriter.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            long end = System.currentTimeMillis();
            bufferedWriter.append(String.valueOf(start)).append(" Get ").append(String.valueOf(end - start)).append(" ms status:200\n");
            bufferedWriter.close();
            map.get("success").incrementAndGet();

        }catch (Exception e){
            e.printStackTrace();
            if (conn!=null) conn.close();
            map.get("fail").incrementAndGet();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void post() throws Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Connection conn = null;
        long start = System.currentTimeMillis();
        try{
            conn = databaseConnectionFactory.getWriteConnection();
            // 执行数据插入
            File file = new File("/Users/youyun/Documents/java project/client2/src/main/resources/image/part1/10-10-2-process.png");
            String sql = "INSERT INTO album (image_data, artist, year,title) VALUES (?, ?, ?, ?)";
            PreparedStatement  preparedStatement= conn.prepareStatement(sql);
            preparedStatement.setBlob(1, new FileInputStream(file));
            preparedStatement.setString(2, "yy");
            preparedStatement.setString(3, "2000");
            preparedStatement.setString(4, "xx");
            preparedStatement.executeUpdate();
            conn.close();
            long end = System.currentTimeMillis();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/youyun/Documents/java project/client2/src/main/resources/result/java6.csv", true));
            bufferedWriter.append(String.valueOf(start)).append(" Post ").append(String.valueOf(end - start)).append(" ms status:200\n");
            bufferedWriter.close();
            map.get("success").incrementAndGet();
        }catch (Exception e){
            e.printStackTrace();
            if(conn!=null) conn.close();
            map.get("fail").incrementAndGet();
        }finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

//        HttpPost httpPost = new HttpPost(IPAddr);
//        // Create a MultipartEntityBuilder
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//
//        // This attaches the file to the POST:
//        File file = new File("/Users/youyun/Documents/java project/client2/src/main/resources/image/part1/go/Screenshot 2023-10-07 at 12.34.32 PM.png");
//        builder.addBinaryBody(
//                "image", // This should be the name of the field expected by the server
//                new File("/Users/youyun/Documents/java project/client2/src/main/resources/image/part1/go/Screenshot 2023-10-07 at 12.34.32 PM.png"),
//                ContentType.APPLICATION_OCTET_STREAM,
//                file.getName()
//        );
//
//        // This attaches another form field with the file description
//        builder.addTextBody("artist", "yummy");
//        builder.addTextBody("year", "2000");
//        builder.addTextBody("title", "xixi");
//        // Build the multipart entity
//        HttpEntity multipart = builder.build();
//
//        // Set the multipart entity as the request entity
//        httpPost.setEntity(multipart);
//
//        long start = System.currentTimeMillis();
//        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//            HttpEntity entity = response.getEntity();
//            String result = EntityUtils.toString(entity);
//            EntityUtils.consume(entity);
//            long end = System.currentTimeMillis();
//            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/youyun/Documents/java project/client2/src/main/resources/result/java1.csv",true));
//            if (response.getStatusLine().getStatusCode()==200){
//                bufferedWriter.append(String.valueOf(start)).append(" Post ").append(String.valueOf(end - start)).append(" ms status:200\n");
//                map.get("success").incrementAndGet();
//            }else{
//                bufferedWriter.append(String.valueOf(start)).append(" Post ").append(String.valueOf(end - start)).append(" ms status:"+response.getStatusLine().getStatusCode()+"\n");
//                map.get("fail").incrementAndGet();
//            }
//            bufferedWriter.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
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

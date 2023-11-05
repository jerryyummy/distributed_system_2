package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadGroupHandler {
    int threadGroupSize;
    int numThreadGroups;
    int delaySeconds;
    String IPAddr;

    public ThreadGroupHandler(int threadGroupSize, int numThreadGroups, int delaySeconds, String IPAddr) {
        this.threadGroupSize = threadGroupSize;
        this.numThreadGroups = numThreadGroups;
        this.delaySeconds = delaySeconds;
        this.IPAddr = IPAddr;
    }

    public void request() throws Exception {
        String fileName = "/Users/youyun/Documents/java project/client2/src/main/resources/result/java4.csv";
        ConcurrentHashMap<String, AtomicInteger> map = new ConcurrentHashMap<>();
        map.put("success",new AtomicInteger(0));
        map.put("fail",new AtomicInteger(0));
        long totalRequests = 0;
        CountDownLatch threadGroupLatch = new CountDownLatch(numThreadGroups);  // 线程组总数 10
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
        long startTime = System.currentTimeMillis();     // 线程组运行前时间

        for (int i = 0; i <numThreadGroups; i++) {
            new Thread(new MainThread(threadGroupSize, delaySeconds, IPAddr, i + 1, threadGroupLatch, out,map)).start();
            Thread.sleep(delaySeconds * 1000L);
            totalRequests += 2000;
        }
        boolean finish = threadGroupLatch.await(200000, TimeUnit.MILLISECONDS);
        System.out.println("threadGroup all Done");
        long endTime = System.currentTimeMillis();       //  线程组运行完时间
        System.out.println("thread group run all time: " + (endTime - startTime) + " ms");
        System.out.println("throughput: " + (double) totalRequests / ((endTime - startTime) / 1000.0));
        System.out.println("total successful requests: "+map.get("success"));
        System.out.println("total failed requests: "+map.get("fail"));
    }

    public static void main(String[] args) throws Exception {
        ThreadGroupHandler threadGroupHandler = new ThreadGroupHandler(10, 30, 2, "http://localhost:8081/assignment_war_exploded/AlbumStore/albums");
        threadGroupHandler.request();
    }
}

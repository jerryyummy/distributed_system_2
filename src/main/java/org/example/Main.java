package org.example;


import com.sun.jmx.snmp.Timestamp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class Main {
    public void handleData(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        List<Long> postlist = new ArrayList<>();
        List<Long> getlist = new ArrayList<>();
        Long pres = 0L;
        Long gres = 0L;
        String temp = "";
        while ((temp=bufferedReader.readLine())!=null){
            if (Objects.equals(temp.split(" ")[1], "Get")){
                Long responseTime = Long.valueOf(temp.split(" ")[2]);
                getlist.add(responseTime);
                gres+=responseTime;
            }else{
                Long responseTime = Long.valueOf(temp.split(" ")[2]);
                postlist.add(responseTime);
                pres+=responseTime;
            }

        }
        Collections.sort(getlist);
        Collections.sort(postlist);
        System.out.println("for post:");
        System.out.println("mean response time: "+(pres/postlist.size()));
        System.out.println("median response time: "+postlist.get(postlist.size()/2));
        System.out.println("99% response time: "+postlist.get(postlist.size()/100));
        System.out.println("min response time: "+postlist.get(0));
        System.out.println("max response time: "+postlist.get(postlist.size()-1));
        System.out.println("succeed request:"+postlist.size());

    }
    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.handleData("/Users/youyun/Documents/java project/client2/src/main/resources/assignment3/java1.csv");
        main.handleData("/Users/youyun/Documents/java project/client2/src/main/resources/assignment3/java2.csv");
        main.handleData("/Users/youyun/Documents/java project/client2/src/main/resources/assignment3/java3.csv");
    }
}
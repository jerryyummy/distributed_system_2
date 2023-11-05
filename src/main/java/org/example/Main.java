package org.example;


import com.sun.jmx.snmp.Timestamp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class Main {
    public void handleData(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        List<Long> list = new ArrayList<>();
        Long res = 0L;
        String temp = "";
        while ((temp=bufferedReader.readLine())!=null){
            Long responseTime = Long.valueOf(temp.split(" ")[2]);
            list.add(responseTime);
            res+=responseTime;
        }
        Collections.sort(list);
        System.out.println("mean response time: "+(res/list.size()));
        System.out.println("median response time: "+list.get(list.size()/2));
        System.out.println("99% response time: "+list.get(list.size()/100));
        System.out.println("min response time: "+list.get(0));
        System.out.println("max response time: "+list.get(list.size()-1));
    }
    public static void main(String[] args) throws IOException {
        Main main = new Main();
        main.handleData("/Users/youyun/Documents/java project/client2/src/main/resources/result/java7.csv");
    }
}
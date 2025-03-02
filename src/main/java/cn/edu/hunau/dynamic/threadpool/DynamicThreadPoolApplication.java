package cn.edu.hunau.dynamic.threadpool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author 苏佳
 * @Date 2024/11/10 20:38
 * @Description DynamicThreadPoolApplication类
 **/
@SpringBootApplication
public class DynamicThreadPoolApplication {
    public static void main(String[] args) {
        SpringApplication.run(DynamicThreadPoolApplication.class,args);
    }

}
package cn.edu.hunau.dynamic.threadpool.controller;

import cn.edu.hunau.dynamic.threadpool.DynamicThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author sj
 * @Date 2025/03/02 15:20
 * @Description TODO
 **/
@RestController
@RequestMapping("/threadpool")
public class ThreadPoolController {

    @Autowired
    private DynamicThreadPool dynamicThreadPool;

    /**
     * 打印当前线程池的状态
     */
    @GetMapping("/print")
    public String printThreadPoolStatus() {
        return dynamicThreadPool.printThreadPoolStatus();
    }

    /**
     * 给线程池增加任务
     *
     * @param count
     */
    @GetMapping("/add")
    public String dynamicThreadPoolAddTask(int count) {
        dynamicThreadPool.dynamicThreadPoolAddTask(count);
        return String.valueOf(count);
    }
}
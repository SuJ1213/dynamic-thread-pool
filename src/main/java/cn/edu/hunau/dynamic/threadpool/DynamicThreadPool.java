package cn.edu.hunau.dynamic.threadpool;

import cn.edu.hunau.dynamic.threadpool.service.ThreadPoolExecutorService;
import cn.edu.hunau.dynamic.threadpool.service.impl.SafeThreadPoolExecutorDecorator;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RefreshScope
public class DynamicThreadPool {
    private final ThreadPoolExecutorService threadPool;

    @Value("${core.size:10}")
    private int coreSize;

    @Value("${max.size:20}")
    private int maxSize;

    @Autowired
    private NacosConfigManager nacosConfigManager;

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    public DynamicThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            10, 
            20,
            60L, 
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "dynamic-thread-" + threadNumber.getAndIncrement());
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        this.threadPool = new SafeThreadPoolExecutorDecorator(executor);
    }

    @PostConstruct
    public void init() {
        threadPool.setCorePoolSize(coreSize);
        threadPool.setMaximumPoolSize(maxSize);
    }

    public String printThreadPoolStatus() {
        return threadPool.getThreadPoolStatus();
    }

    public void dynamicThreadPoolAddTask(int count) {
        for (int i = 0; i < count; i++) {
            final int taskId = i;
            threadPool.execute(() -> {
                try {
                    Thread.sleep(1000);
                    System.out.println("Task " + taskId + " executed by " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
}

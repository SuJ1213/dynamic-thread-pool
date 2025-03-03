package cn.edu.hunau.dynamic.threadpool.service;

/**
 * @Author sj
 * @Date 2025/03/03 23:04
 * @Description TODO
 **/
public interface ThreadPoolExecutorService {
    void execute(Runnable command);
    void setCorePoolSize(int corePoolSize);
    void setMaximumPoolSize(int maximumPoolSize);
    int getCorePoolSize();
    int getMaximumPoolSize();
    String getThreadPoolStatus();
}
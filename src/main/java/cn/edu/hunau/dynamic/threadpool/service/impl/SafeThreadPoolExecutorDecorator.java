package cn.edu.hunau.dynamic.threadpool.service.impl;

import cn.edu.hunau.dynamic.threadpool.service.ThreadPoolExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author sj
 * @Date 2025/03/03 23:19
 * @Description TODO
 **/
public class SafeThreadPoolExecutorDecorator implements ThreadPoolExecutorService {
    private final ThreadPoolExecutor executor;
    private final BlockingQueue<Runnable> backupQueue;
    private final ReentrantLock adjustLock = new ReentrantLock();

    public SafeThreadPoolExecutorDecorator(ThreadPoolExecutor executor) {
        this.executor = executor;
        this.backupQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }

    @Override
    public void setCorePoolSize(int corePoolSize) {
        adjustLock.lock();
        try {
            // 参数校验
            if (corePoolSize < 0 || corePoolSize > executor.getMaximumPoolSize()) {
                throw new IllegalArgumentException("Invalid core pool size");
            }

            // 备份当前队列中的任务
            backupPendingTasks();

            // 安全地调整核心线程数
            executor.setCorePoolSize(corePoolSize);

            // 恢复备份的任务
            restoreBackupTasks();
        } finally {
            adjustLock.unlock();
        }
    }

    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        adjustLock.lock();
        try {
            // 参数校验
            if (maximumPoolSize < executor.getCorePoolSize()) {
                throw new IllegalArgumentException("Maximum pool size cannot be less than core pool size");
            }

            // 备份当前队列中的任务
            backupPendingTasks();

            // 安全地调整最大线程数
            executor.setMaximumPoolSize(maximumPoolSize);

            // 恢复备份的任务
            restoreBackupTasks();
        } finally {
            adjustLock.unlock();
        }
    }

    private void backupPendingTasks() {
        BlockingQueue<Runnable> queue = executor.getQueue();
        List<Runnable> pendingTasks = new ArrayList<>();
        queue.drainTo(pendingTasks);
        backupQueue.addAll(pendingTasks);
    }

    private void restoreBackupTasks() {
        List<Runnable> tasks = new ArrayList<>();
        backupQueue.drainTo(tasks);
        tasks.forEach(executor::execute);
    }

    @Override
    public int getCorePoolSize() {
        return executor.getCorePoolSize();
    }

    @Override
    public int getMaximumPoolSize() {
        return executor.getMaximumPoolSize();
    }

    @Override
    public String getThreadPoolStatus() {
        return String.format(
                "ThreadPool Status: CorePoolSize=%d, MaximumPoolSize=%d, ActiveThreads=%d, QueueSize=%d",
                executor.getCorePoolSize(),
                executor.getMaximumPoolSize(),
                executor.getActiveCount(),
                executor.getQueue().size()
        );
    }
}

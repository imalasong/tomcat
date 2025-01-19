package org.apache.catalina.sample;


import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author imalasong
 * @date 2025-01-17 22:15
 */
public class ExecutorStudy {

    // 创建线程池
    final static ExecutorService executor = new ThreadPoolExecutor(
            5, // 核心线程数
            10, // 最大线程数
            10, // 存活时间
            TimeUnit.SECONDS, // 时间单位，这里是秒
            new LinkedBlockingQueue<>(10), // 任务队列
            new ThreadFactory() {
                AtomicLong incre = new AtomicLong();
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    //定义线程名称
                    thread.setName("my-thread-"+incre.incrementAndGet());
                    return thread;
                }
            },//线程工厂，负责创建线程
            new ThreadPoolExecutor.DiscardPolicy() //拒绝策略
    );

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // 使用方式一：没有返回值模式，提交任务到线程池，
        executor.execute(() -> {
            System.out.println(Thread.currentThread().getName() + " is running");
            try {
                Thread.sleep(1000); // 模拟任务执行时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " has finished");
        });
        // 使用方式二：有返回值模式，提交任务到线程池，
        Future<String> submit = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println(Thread.currentThread().getName() + " is running");
                try {
                    Thread.sleep(1000); // 模拟任务执行时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " has finished");
                return "imalasong";
            }
        });

        System.out.println("执行结果："+submit.get());
        //阻塞程序，防止立马终止
        System.in.read();
        // 关闭线程池
        executor.shutdown();
    }
}

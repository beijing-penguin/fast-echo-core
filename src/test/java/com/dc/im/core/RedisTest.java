package com.dc.im.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RedisTest {
    static ExecutorService service = new ThreadPoolExecutor(16, 16, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    public static void main(String[] args) throws Exception {
        AtomicInteger add = new AtomicInteger(0);
        int num = 10000;
        final CountDownLatch wait = new CountDownLatch(num);
        JedisPool pool = new JedisPool("localhost", 6379);
        long begin = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            
            service.submit(new Runnable() {
                
                @Override
                public void run() {
                  Jedis jedis = pool.getResource();
                  System.err.println(jedis.incr("dc"));
                  //System.err.println(add.incrementAndGet());
                  wait.countDown();
                }
            });
        }
        wait.await();
        System.out.println("NettyTest执行耗时: " + (System.currentTimeMillis() - begin));
        pool.close();
        service.shutdown();
        
    }
}

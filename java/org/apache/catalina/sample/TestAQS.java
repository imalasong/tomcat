package org.apache.catalina.sample;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author xiaochangbai
 * @date 2025-01-19 21:20
 */
public class TestAQS {

    public static void main(String[] args) throws InterruptedException {
        Sync sync = new Sync(1);
//        sync.acquireSharedInterruptibly(1);
//        sync.releaseShared(0);
//        sync.acquireSharedInterruptibly(1);
//        sync.releaseShared(0);
//        sync.acquireSharedInterruptibly(1);
        sync.acquireSharedInterruptibly(1);
        System.out.println("1111");
        sync.releaseShared(0);
        sync.acquireSharedInterruptibly(1);
        System.out.println("2222");
        sync.acquireSharedInterruptibly(1);
        System.out.println("3333");

        System.out.println(sync);
    }

    public static class Sync extends AbstractQueuedSynchronizer {

        private AtomicLong count = null;
        private volatile long limit;
        private volatile boolean released = false;

        private static final long serialVersionUID = 1L;

        Sync(long limit) {
            this.count = new AtomicLong(0);
            this.limit = limit;
        }

        @Override
        protected int tryAcquireShared(int ignored) {
            System.out.println("tryAcquireShared:"+ignored);
            long newCount = count.incrementAndGet();
            if (!released && newCount > limit) {
                // Limit exceeded
                count.decrementAndGet();
                return -1;
            } else {
                return 1;
            }
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            System.out.println("tryReleaseShared:"+arg);
            count.decrementAndGet();
            return true;
        }
    }
}

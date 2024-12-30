/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.collections;

/**
 * This is intended as a (mostly) GC-free alternative to
 * {@link java.util.concurrent.ConcurrentLinkedQueue} when the requirement is to
 * create an unbounded queue with no requirement to shrink the queue. The aim is
 * to provide the bare minimum of required functionality as quickly as possible
 * with minimum garbage.
 *
 * @param <T> The type of object managed by this queue
 */
public class SynchronizedQueue<T> {

    public static final int DEFAULT_SIZE = 128;

    private Object[] queue;
    private int size;
    private int insert = 0;
    private int remove = 0;

    public SynchronizedQueue() {
        this(DEFAULT_SIZE);
    }

    public SynchronizedQueue(int initialSize) {
        queue = new Object[initialSize];
        size = initialSize;
    }

    public synchronized boolean offer(T t) {
        queue[insert++] = t;

        // 当数组写满当时候，写指针指向数组第一个元素以达到循环利用的原则
        // tip：这里不用担心，下次写的时候会覆盖以前的元素。因为队列遵循先进先出的原则，
        // 如果队列有移除过一个元素的话，0号元素已经出队列了，覆盖也就没什么影响。
        // 如果没有移除过元素的话remove也是=0的，所以就会执行下面的扩容操作
        if (insert == size) {
            insert = 0;
        }

        //写指针和移除指针重合，表示已经没有位置写了，就该扩容
        if (insert == remove) {
            expand();
        }
        return true;
    }

    public synchronized T poll() {
        //写指针和移除指针相等，表示队列还是空的。
        // 因为元素插入之后，在offer里面进行判断处理：insert==remove就会扩容，扩容之后insert肯定不会等于remove
        // 当所有元素的都移除之后，insert就会重新等于remove
        if (insert == remove) {
            // empty
            return null;
        }

        @SuppressWarnings("unchecked")
        T result = (T) queue[remove];
        queue[remove] = null;
        remove++;

        //数组最后一个元素移除完了，下次该移除数组第一个元素了
        if (remove == size) {
            remove = 0;
        }

        return result;
    }

    private void expand() {
        int newSize = size * 2;
        //建立一个新数组：大小是原来的两倍
        Object[] newQueue = new Object[newSize];

        //这里采用了一个native的方法来实现数据拷贝，没有为什么，就是因为高效
        //此处拷贝的流程是这样的，假设有这样一个长度为8的数组：
        //    索引: 0 1 2 3 4 5 6 7
        //    元素: J A V A W U D I
        //此时的insert=1，remove=1，队头元素是A，队尾元素是J
        //拷贝到新数组应该是这样的
        //    索引: 0 1 2 3 4 5 6 7 8 9 10 11 12 14 15 16 17 18
        //    元素: A V A W U D I J

        // 因为要保持先进先出的原则，所以拷贝的时候先拷贝：A V A W U D I
        System.arraycopy(queue, insert, newQueue, 0, size - insert);

        //再拷贝：J，因为J是最后进队列的，是队尾
        System.arraycopy(queue, 0, newQueue, size - insert, insert);

        //不理解的同学：请注意扩容时机，是数组完全满了之后再扩容了，结合这个再去看看上面逻辑
        insert = size;
        remove = 0;
        queue = newQueue;
        size = newSize;
    }

    public synchronized int size() {
        int result = insert - remove;
        if (result < 0) {
            result += size;
        }
        return result;
    }

    public synchronized void clear() {
        queue = new Object[size];
        insert = 0;
        remove = 0;
    }
}

package com.zlikun.jee;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试关键字volatile在并发环境中的应用
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/6/7 14:25
 */
public class VolatileTest {

    private int index0 = 0 ;
    // volatile 保证变量可见性，即：当一个线程修改了变量的值后，另一个线程立即能看到修改后的值，
    // 实际是通过控制变量只存在于主存(未使用该关键字修饰的变量多个线程会复制其副本到线程工作内存中，并在工作内存中操作数据，这样修改的数据在未同步回主存时，其它线程是看不到的)
    // 但该关键字并不保证变量操作的原子性，而只保证变量的可见性，因此应用该关键字的四个原则：
    // 1、写入变量不依赖此变量的值，或者只有一个线程修改此变量
    // 2、变量的状态不需要与其它变量共同参与不变约束
    // 3、访问变量不需要加锁(但读需要)
    // 4、由于使用volatile屏蔽掉了VM中必要的代码优化，所以在效率上比较低，因此一定在必要时才使用此关键字
    private volatile int index2 = 0 ;

    @Test
    public void multi_no_volatile() {

        ExecutorService exec = Executors.newFixedThreadPool(50) ;

        final ReentrantLock lock = new ReentrantLock() ;
        for (int i = 0; i < 1000 * 1000; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    // ++ ，实际上是read-modify-write三项操作，并非原子化的
                    // index0 ++ ;

                    // 这里使用代码拆解开来测试
                    // read
                    int t = index0 ;
                    // modify
                    t = t + 1 ;
                    // write
                    index0 = t ;
                }
            });
        }

        exec.shutdown();
        while (!exec.isTerminated()) ;

        Assert.assertTrue(this.index0 < 1000 * 1000);
        System.out.println(this.index0);
    }

    /**
     * volatile 究竟有什么用？仍然需要加锁!!!
     */
    @Test
    public void multi_has_volatile() {

        ExecutorService exec = Executors.newFixedThreadPool(50) ;

        final ReentrantLock lock = new ReentrantLock() ;
        for (int i = 0; i < 1000 * 1000; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    // 修改数据则需要加锁
                    try {
                        lock.lock();

                        index2 ++ ;

//                        // read，volatile 只保证变量可见性，相当于读锁，所以这里不用再加锁
//                        int t = index2 ;
//                        // modify
//                        t = t + 1;
//                        // write
//                        index2 = t ;
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        exec.shutdown();
        while (!exec.isTerminated()) ;

        // 并发下的 ++ ，实际上是read-modify-write三项操作，并非原子化的
        Assert.assertEquals(1000 * 1000 ,this.index2);
    }

}

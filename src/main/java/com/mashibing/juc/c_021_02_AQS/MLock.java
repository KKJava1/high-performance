/**
 * 通过AQS实现自定义锁，目前仅实现了lock和unlock
 * @author 马士兵
 */
package com.mashibing.juc.c_021_02_AQS;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

//实现独享锁
public class MLock implements Lock {

//    private Sync sync = new Sync();

    volatile AtomicReference<Thread> owner = new AtomicReference<>();

    //保存等待的线程
    volatile LinkedBlockingDeque<Thread> waiters = new LinkedBlockingDeque<>();
    @Override
    public void lock() {
        boolean addQ = true;
        if(!tryLock()){
            if(addQ){
                //等待
                waiters.offer(Thread.currentThread());
                //挂起当前的线程
               addQ = false;
            }else {
                LockSupport.park();
            }

        }
        waiters.remove(Thread.currentThread());
    }

    @Override
    public void unlock() {
//        sync.release(1);
        if(owner.compareAndSet(Thread.currentThread(),null)){
            Iterator<Thread> iterator = waiters.iterator();
            while (iterator.hasNext()){
                Thread next = iterator.next();
                LockSupport.unpark(next);
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return owner.compareAndSet(null,Thread.currentThread());
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}

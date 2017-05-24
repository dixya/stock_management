/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.BrokerWithThread;

import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.Order;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Multithreaded order queue, a separate thread is used to dispatch orders.
 * @author dixya
 * @param <T> the dispatch threshold type
 * @param <E> the order type contained in the queue.
 */
public class ThreadedOrderQueue<T, E extends Order> implements OrderQueue<T,E>, Runnable {
private final TreeSet<E> queue;
private final BiPredicate<T,E> filter;
private  T threshold;
private Thread dispatchThread;
private Consumer<E> orderProcessor;
/** the lock used to control access to the queue */
private final ReentrantLock queueLock=new ReentrantLock();
/** Condition used to initiate processing orders */
private final Condition dispatchCondition =queueLock.newCondition();
/** The lock used to control access to the processor callback object */
private final ReentrantLock processorLock=new ReentrantLock();

/** Constructor
     * @param name
     * @param threshold
     * @param filter*/
public ThreadedOrderQueue(final String name, final T threshold, final BiPredicate<T,E> filter){
    queue=new TreeSet<>();
    this.threshold=threshold;
    this.filter=filter;
    startDispatchThread(name);
}
/** Constructor
     * @param name
     * @param threshold
     * @param filter
     * @param cmp*/
public ThreadedOrderQueue(final String name, final T threshold, final BiPredicate<T,E> filter,final Comparator<E> cmp){
    queue=new TreeSet<>(cmp);
    this.threshold=threshold;
    this.filter=filter;
    startDispatchThread(name);
}
/**
 * Adds the orderto the queue
 * @param order the order to be added to the queue
 */
    @Override
    public void enqueue(final E order) {
        queueLock.lock();
        try{
            queue.add(order);
        } finally{
            queueLock.unlock();
        }
        dispatchOrders();
        
    }

    @Override
    public E dequeue() {
        E order=null;
        queueLock.lock();
        try{
            if(!queue.isEmpty()){
                order=queue.first();
                if((filter!=null) && !filter.test(threshold, order)){
                    order=null;
                }else{
                    queue.remove(order);
                }
            }
        }finally{
            queueLock.unlock();
        }
        return order;
    }

    /**
     * Signals the waiting dispatch thread to process orders.
     */
    @Override
    public void dispatchOrders() {
        queueLock.lock();
        try{
            dispatchCondition.signal();
        }finally{
            queueLock.unlock();
        }
    }

    @Override
    public void setOrderProcessor(Consumer<E> cnsmr) {
        processorLock.lock();
        try{
            orderProcessor=cnsmr;
        } finally{
            processorLock.unlock();
        }
    }

    @Override
    public void setThreshold(final T t) {
        this.threshold=t;
        dispatchOrders();
    }

    @Override
    public T getThreshold() {
        return threshold;
    }

    /** Dispatches orders as long as there are dispatchable orders available. */
    @Override
    public void run() {
        while(true){
            E order;
            queueLock.lock();
            try{
                while((order=dequeue())==null){
                    try{
                        dispatchCondition.await();
                    } catch(final InterruptedException ex){
                        final Logger log=LoggerFactory.getLogger(this.getClass());
                        log.info("Order queue interrupted while waiting");
                    }
                }
            }finally{
                queueLock.unlock();
            }
            processorLock.lock();
            try{
                if(orderProcessor!=null){
                    orderProcessor.accept(order);
                }
            } finally{
                processorLock.unlock();
            }
        }
        
    }

    private void startDispatchThread(final String name) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        dispatchThread=new Thread(this,name+"-OrderDispatchThread");
        dispatchThread.setDaemon(true);
        dispatchThread.start();
    }
    /**
     * Sets the priority of the order queue.
     * @param priority the priority of the order queue.
     */
    public void setPriority(final int priority){
        dispatchThread.setPriority(priority);
    }
    
}

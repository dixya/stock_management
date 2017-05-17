/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.broker;

import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.broker.OrderQueue;

import java.util.TreeSet;
import java.util.Comparator;
import java.util.function.BiPredicate;

import java.util.function.Consumer;

/**
 * Implementation of an OrderQueue.
 *
 * @author dixya
 * @param <T> dispatch threshold type
 * @param <E>the type of the order contained in the queue.
 */
public final class OrderQueueImpl<T, E extends Order> implements OrderQueue<T, E> {

    /**
     * The queue data structure
     */
    private TreeSet<E> queue;
    /**
     * The filter that determines if an order is dispatchable
     */
    /**
     * Using BiPredicate interface in place of Dispatchfilter
     */
    private BiPredicate<T, E> filter;
    /**
     * Order processor used to process disatchable orders
     */
    private Consumer<E> orderProcessor;
    private T threshold;

    /**
     * Constructor
     *
     * @param threshold initial threshold
     * @param filter the dispatch filter used to control dispatching from this
     * queue.
     */
    public OrderQueueImpl(final T threshold, final BiPredicate<T, E> filter) {
        queue = new TreeSet<>();
        this.threshold = threshold;
        this.filter = filter;
    }

    /**
     * Constructor
     *
     * @param threshold initial threshold
     * @param filter the dispatch filter used to control dispatching from this
     * queue.
     * @param cmp Comparator to be used for ordering.
     */
    public OrderQueueImpl(final T threshold, final BiPredicate<T, E> filter, final Comparator<E> cmp) {
        queue = new TreeSet<>(cmp);
        this.threshold = threshold;
        this.filter = filter;
    }

    /**
     * Adds the specified order to the queue and dispatches dispatchable orders.
     *
     * @param order the order to be added to the queue
     */
    @Override
    public void enqueue(final E order) {
        queue.add(order);
        dispatchOrders();

    }

    /**
     * Deletes the highest dispatchable order in the queue. If there are orders
     * in the queue but they do not meet the dispatch threshold order will not
     * be removed and null will be returned.
     *
     * @return the first dispatchable order in the queue or null if there are no
     * dispatchable orders in the queue.
     */

    @Override
    public E dequeue() {
        E order = null;
        if (!queue.isEmpty()) {
            order = queue.first();
            if (filter.test(threshold, order)) {
                queue.remove(order);
            } else {
                order = null;
            }
        }
        return order;

    }

    /**
     * Executes the callback for each dispatchable order. Each dispatchable
     * order is in turn removed from the queue and passed to the callback. If no
     * callback is registered the order is simply removed from the queue.
     */
    @Override
    public void dispatchOrders() {
        E order;
        while ((order = dequeue()) != null) {
            if (orderProcessor != null) {
                orderProcessor.accept(order);
            }
        }
    }

    /**
     * Registers the callback to be used during order processing
     *
     * @param cnsmr the callback to be registered
     */
    @Override
    public void setOrderProcessor(Consumer<E> cnsmr) {
        orderProcessor = cnsmr;
    }

    /**
     * Adjusts the threshold and dispatches orders.
     *
     * @param threshold new threshold
     */
    @Override
    public final void setThreshold(final T threshold) {
        this.threshold = threshold;
        dispatchOrders();
    }

    /**
     * Obtains current threshold
     *
     * @return threshold
     */
    @Override
    public final T getThreshold() {
        return threshold;
    }

}

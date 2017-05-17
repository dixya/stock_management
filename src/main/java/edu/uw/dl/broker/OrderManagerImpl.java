/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.broker;

import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import java.util.function.Consumer;
import java.util.Comparator;

/**
 *
 * @author dixya
 */
public class OrderManagerImpl implements OrderManager {

    /**
     * The symbol of the stock this order manager is for
     */
    private String stockTickerSymbol;
    /**
     * Queue for stop buy orders
     */
    protected OrderQueue<Integer,StopBuyOrder> stopBuyOrderQueue;
    /**
     * Queue for stop sell orders
     */
    protected OrderQueue<Integer,StopSellOrder> stopSellOrderQueue;

    /**
     * Constructor Used by sub classes to finish initialization
     *
     * @param stockTickerSymbol the ticker symbol
     */
    public OrderManagerImpl(final String stockTickerSymbol) {
        this.stockTickerSymbol = stockTickerSymbol;
    }

    /**
     * Multiple arguments constructor.
     *
     * @param stockTickerSymbol the ticker symbol of this stock
     * @param price the current price of the stock to be managed.
     */
    public OrderManagerImpl(final String stockTickerSymbol, final int price) {
        this(stockTickerSymbol);
        //Create the stop buy order queue and associated pieces
        stopBuyOrderQueue = new OrderQueueImpl<>(price,(t, o) -> o.getPrice() <= t, 
          Comparator.comparing(StopBuyOrder::getPrice).thenComparing(StopBuyOrder::compareTo));
        //Create the stop sell order queue...
        stopSellOrderQueue = new OrderQueueImpl<>(price,(t, o) -> o.getPrice() >= t,
          Comparator.comparing(StopSellOrder::getPrice).reversed().thenComparing(StopSellOrder::compareTo));
    }

    /**
     * Gets the stock ticker symbol for the stock managed by this stock manager.
     *
     * @return the stock ticker symbol
     */
    @Override
    public String getSymbol() {
        return stockTickerSymbol;
    }

    /**
     * Respond to a stock price adjustment by setting threshold on dispatch
     * filters.
     *
     * @param price the new price
     */
    @Override
    public void adjustPrice(final int price) {
        stopBuyOrderQueue.setThreshold(price);
        stopSellOrderQueue.setThreshold(price);
    }

    /**
     * Queue a stop buy order
     *
     * @param order the order to be queued
     */
    @Override
    public void queueOrder(final StopBuyOrder order) {
        stopBuyOrderQueue.enqueue(order);
    }

    /**
     * Queue a stop sell order
     *
     * @param order the order to be queued
     */
    @Override
    public void queueOrder(StopSellOrder order) {
        stopSellOrderQueue.enqueue(order);
    }

    /**
     * Sets the orderProcessor to stopBuyOrderQueue and will be passed on to the
     * order queue as the dispatch callback.
     *
     * @param processor the callback to be registered.
     */
    @Override
    public void setBuyOrderProcessor(Consumer<StopBuyOrder> processor) {
        stopBuyOrderQueue.setOrderProcessor(processor);
    }

    /**
     * Registers the processor to be used during sell order processing. This
     * will be passed on to the order queue as the dispatch call back.
     *
     * @param processor the callback to be registered.
     */

    @Override
    public void setSellOrderProcessor(Consumer<StopSellOrder> processor) {
        stopSellOrderQueue.setOrderProcessor(processor);
    }

}

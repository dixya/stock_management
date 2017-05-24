/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.BrokerWithThread;

import edu.uw.dl.broker.OrderManagerImpl;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import java.util.Comparator;

/**
 *
 * @author dixya
 */
public class ThreadedOrderManager extends OrderManagerImpl{
    
    public ThreadedOrderManager(final String stockTickerSymbol,final int price) {
        super(stockTickerSymbol);
        stopBuyOrderQueue=new ThreadedOrderQueue<>(stockTickerSymbol+"-StopBuy",price,(t,o)->o.getPrice()<= t, 
          Comparator.comparing(StopBuyOrder::getPrice).thenComparing(StopBuyOrder::compareTo));
        //Create the stop sell order queue...
        stopSellOrderQueue = new ThreadedOrderQueue<>(stockTickerSymbol+"-StopSell",price,(t, o) -> o.getPrice() >= t,
          Comparator.comparing(StopSellOrder::getPrice).reversed().thenComparing(StopSellOrder::compareTo));
    }
     
        
    }


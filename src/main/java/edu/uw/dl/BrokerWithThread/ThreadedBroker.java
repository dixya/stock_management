/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.BrokerWithThread;

import edu.uw.dl.broker.BrokerImpl;
import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.order.Order;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dixya
 */
public final class ThreadedBroker extends BrokerImpl {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerImpl.class);

    /**
     * Constructor
     *
     * @param brokerName name of the broker
     * @param exchg stock exchange to be used by the broker
     * @param acctMgr acount manager to be used by the broker.
     */
    public ThreadedBroker(final String brokerName, final StockExchange exchg, final AccountManager acctMgr) {
        super(brokerName, exchg, acctMgr);
        final ThreadedOrderQueue<Boolean, Order> marketQ;
        marketQ = new ThreadedOrderQueue<>("MARKET", exchg.isOpen(), (Boolean t, Order o) -> t);
        marketQ.setPriority(Thread.MAX_PRIORITY);
        marketOrders = marketQ;
        Consumer<Order> stockTrader = (order) -> {
            if (LOG.isInfoEnabled()) {
                LOG.info(String.format("Executing %s", order));
            }
            final int sharePrice = exchg.executeTrade(order);
            try {
                final Account acct = acctMgr.getAccount(order.getAccountId());
                acct.reflectOrder(order, sharePrice);
                if (LOG.isInfoEnabled()) {
                    LOG.info(String.format("New Balance=%d", acct.getBalance()));
                }
            } catch (final AccountException ex) {
                LOG.error(String.format("Unable to update account %s", order.getAccountId()));
            }
        };
        marketOrders.setOrderProcessor(stockTrader);
        initializeOrderManagers();
        exchg.addExchangeListener(this);
    }

    /**
     * Create an appropriate order manager for this broker.
     *
     * @param ticker the ticker symbolof the stock
     * @param initialPrice current price of the stock.
     * @return a new OrderManager for the specified stock.
     */
    protected OrderManager createOrderManager(final String ticker, final int initialPrice) {
        return new ThreadedOrderManager(ticker, initialPrice);
    }

}

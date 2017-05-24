/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.broker;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerException;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;
import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import java.util.HashMap;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the Broker interface, provides a full implementation less the creation of the order manager and market queue.


 * @author dixya
 */
public class BrokerImpl implements Broker, ExchangeListener {

    private final String name;
    public final StockExchange stockExchange;
    private final AccountManager accountManager;
    //private final MarketOrder marketOrders;
    private static final Logger LOG
      = (Logger) LoggerFactory.getLogger(BrokerImpl.class);
    /**
     * Set of order manager used by broker
     */
    public HashMap<String, OrderManager> orderManagerMap;
    /**
     * market order queue
     */
    protected  OrderQueue<Boolean, Order> marketOrders;

    /**
     * Constructor
     *
     * @param brokerName the name of the broker
     * @param exchg the stock exchange to be used by the broker
     * @param acctMgr the account manager to be used by the broker.
     */
    public BrokerImpl(final String brokerName, final StockExchange exchg, final AccountManager acctMgr) {
        name = brokerName;
        accountManager = acctMgr;
        stockExchange = exchg;
        //Create the market order queue and order processor
        marketOrders = new OrderQueueImpl<>(exchg.isOpen(), (t, o) -> t);
        Consumer<Order> stockTrader = (order) -> {
            LOG.info(String.format("Executing ..%s", order));
            final int sharePrice = stockExchange.executeTrade(order);
            try {
                final Account acct = accountManager.getAccount(order.getAccountId());
                acct.reflectOrder(order, sharePrice);
                LOG.info(String.format("new balance=%d", acct.getBalance()));
            } catch (final AccountException ex) {
                LOG.info(String.format("Unable to update account %s", order.getAccountId()));
            }
        };
        marketOrders.setOrderProcessor(stockTrader);
        // Create the order managers
        initializeOrderManagers();
        exchg.addExchangeListener((ExchangeListener) this);
    }

    /**
     * Gets the name of the broker
     *
     * @return broker name.
     */
    @Override
    public String getName() {
        //check the validity of the variants.
        checkInvariants();
        return name;
    }

    @Override
    public Account createAccount(String username, String password, int balance) throws BrokerException {
        checkInvariants();
        try {
            return accountManager.createAccount(username, password, balance);
        } catch (AccountException e) {
            throw new BrokerException("Unable to create account", e);
        }
    }

    @Override
    public synchronized void deleteAccount(String username) throws BrokerException {
        checkInvariants();
        try {
            accountManager.deleteAccount(username);
        } catch (final AccountException e) {
            throw new BrokerException("Unable to delete account", e);
        }
    }

    @Override
    public Account getAccount(String username, String password) throws BrokerException {
        checkInvariants();
        try {
            if (accountManager.validateLogin(username, password)) {
                return accountManager.getAccount(username);
            } else {
                throw new BrokerException("Invalid username or password");
            }
        } catch (AccountException ex) {
            throw new BrokerException("Can't access account", ex);

        }
    }

    @Override
    public StockQuote requestQuote(String symbol) throws BrokerException {
        checkInvariants();
        final StockQuote quote = stockExchange.getQuote(symbol);
        if (quote == null) {
            throw new BrokerException(String.format("Quote not available for %s", symbol));
        }
        return quote;
    }

    @Override
    public synchronized void placeOrder(MarketBuyOrder mbo) throws BrokerException {
        checkInvariants();
        marketOrders.enqueue(mbo);
    }

    @Override
    public synchronized void placeOrder(MarketSellOrder mso) throws BrokerException {
        checkInvariants();
        marketOrders.enqueue(mso);
    }

    @Override
    public void placeOrder(StopBuyOrder sbo) throws BrokerException {
        checkInvariants();
        orderManagerCheckStatus(sbo.getStockTicker()).queueOrder(sbo);
    }

    @Override
    public void placeOrder(StopSellOrder sso) throws BrokerException {
        checkInvariants();
        orderManagerCheckStatus(sso.getStockTicker()).queueOrder(sso);
    }

    @Override
    public void close() throws BrokerException {
        try {
            stockExchange.removeExchangeListener(this);
            accountManager.close();
            orderManagerMap = null;
        } catch (final AccountException e) {
            throw new BrokerException("Broker can't be closed", e);
        }
    }

    /**
     * Fetch the stock list from the exchange and initialize an order manager
     * for each stock. Only to be used during construction.
     */
    protected void initializeOrderManagers() {
        orderManagerMap = new HashMap<>();
        final Consumer<StopBuyOrder> moveBuy2MarketProc = (StopBuyOrder order) -> marketOrders.enqueue(order);
        final Consumer<StopSellOrder> moveSell2MarketProc = (StopSellOrder order) -> marketOrders.enqueue(order);
        for (String ticker : stockExchange.getTickers()) {
            final int currPrice = stockExchange.getQuote(ticker).getPrice();
            final OrderManager orderMgr = createOrderManager(ticker, currPrice);
            orderMgr.setBuyOrderProcessor(moveBuy2MarketProc);
            orderMgr.setSellOrderProcessor(moveSell2MarketProc);
            orderManagerMap.put(ticker, orderMgr);

            LOG.info(String.format("Initialized order manager for '%s' @ %d", ticker, currPrice));

        }
    }

    private OrderManager createOrderManager(String ticker, int currPrice) {
        return new OrderManagerImpl(ticker, currPrice);
    }

    /**
     * Checks either any of the fields has been properly initialized or not.
     *
     * @throws IllegalStateException if broker is in invalid state.
     */
    private void checkInvariants() {
        if (name == null || accountManager == null || stockExchange == null || orderManagerMap == null || marketOrders == null) {
            throw new IllegalStateException("Broker may not have been properly initialized");
        }
    }

    @Override
    public synchronized final void exchangeClosed(final ExchangeEvent event) {
        checkInvariants();
        marketOrders.setThreshold(Boolean.FALSE);
        LOG.info("Market closed");
    }

    @Override
    public synchronized final void priceChanged(final ExchangeEvent event) {
        checkInvariants();
        LOG.info(String.format("Processing price change [%s: %d", event.getTicker(), event.getPrice()));

        OrderManager orderMgr;
        orderMgr = orderManagerMap.get(event.getTicker());
        if (orderMgr != null) {
            orderMgr.adjustPrice(event.getPrice());
        }
    }

    @Override
    public synchronized final void exchangeOpened(final ExchangeEvent ee) {
        checkInvariants();
        LOG.info("Market opened");
        marketOrders.setThreshold(Boolean.TRUE);
    }

    private synchronized OrderManager orderManagerCheckStatus(final String ticker) throws BrokerException {
        final OrderManager orderMgr = orderManagerMap.get(ticker);
        if (orderMgr == null) {
            throw new BrokerException(String.format("Requested stock %s, does not exists", ticker));
        }
        return orderMgr;
    }

}

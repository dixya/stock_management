/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.order;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation for an Order. Deals with comparing the priority of
 * each order.
 *
 * @author dixya
 */
public abstract class AbstractOrder implements Order {

    private final String accountId;
    final int numberOfShares;
    private final String stockTicker;
    int orderId = 0;
    static final Logger LOG = LoggerFactory.getLogger(AbstractOrder.class);

    /**
     * Constructor.
     *
     * @param accountId
     * @param numberOfShares
     * @param stockTicker
     */
    AbstractOrder(String accountId, int numberOfShares, String stockTicker) {
        this.accountId = accountId;
        this.numberOfShares = numberOfShares;
        this.stockTicker = stockTicker;
        orderId++;
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    /**
     * Get the order id, order ids are assigned sequential as orders are created
     * and can be used to determine if one order where created before or after
     * another.
     *
     * @return
     */
    @Override
    public int getOrderId() {
        return orderId;
    }

    /**
     * Get the number of shares.
     *
     * @return number of shares.
     */
    @Override
    public int getNumberOfShares() {
        return numberOfShares;
    }

    @Override
    public String getStockTicker() {
        return stockTicker;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.accountId);
        hash = 53 * hash + this.numberOfShares;
        hash = 53 * hash + Objects.hashCode(this.stockTicker);
        hash = 53 * hash + this.orderId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractOrder other = (AbstractOrder) obj;
        if (this.numberOfShares != other.numberOfShares) {
            return false;
        }
        if (this.orderId != other.orderId) {
            return false;
        }
        if (!Objects.equals(this.accountId, other.accountId)) {
            return false;
        }
        if (!Objects.equals(this.stockTicker, other.stockTicker)) {
            return false;
        }
        return true;
    }

    /**
     * Calculates the value of the order at a given share price.
     *
     * @param pricePerShare the per share price to be used in the price
     * calculation, this value must always be greater than or equal to 0
     * @return the value of the order at the given per share price, sell orders
     * will yield a positive value buy orders a negative value
     */
    @Override
    public abstract int valueOfOrder(int pricePerShare);
        
    

    @Override
    public boolean isBuyOrder() {
        return true;
    }

    /**
     * Compares this object with the argument. Orders with the largest number of
     * shares have highest priority - lowest in the ordering. In the event of a
     * tie, the order with the lower order id number has priority, since it was
     * submitted first.
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Order o) {
        return this.compareTo(o);
    }

}

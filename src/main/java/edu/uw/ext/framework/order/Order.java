/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.order;

/**
 * Interface describing all the operations relevant to an order. As this
 * interface implies orders are immutable. Also specifies that all orders must
 * be Comparable so their priority can be determined.
 *
 * @author dixya
 */
public interface Order extends Comparable<Order> {

    /**
     * Get the account id.
     *
     * @return accountId.
     */
    String getAccountId();

    /**
     * Get the order id, order ids are assigned sequential as orders are created
     * and can be used to determine if one order where created before or after
     * another.
     *
     * @return the order id      *
     */
    int getOrderId();

    /**
     * Get the number of shares.
     *
     * @return the number of shares      *
     */
    int getNumberOfShares();

    /**
     * Get the stock ticker.
     *
     * @return the stock ticker      *
     */
    String getStockTicker();

    /**
     * Calculates the value of the order at a given share price.
     *
     * @param pricePerShare the per share price to be used in the price
     * calculation, this value must always be greater than or equal to 0
     *
     * @return the value of the order at the given per share price, sell orders
     * will yield a positive value buy orders a negative value      *
     */
    int valueOfOrder(int pricePerShare);

    /**
     * Indicates if an order is a buy order.
     *
     * @return true if the order is a buy order      *
     */

    boolean isBuyOrder();

}

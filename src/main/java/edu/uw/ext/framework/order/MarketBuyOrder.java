/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.order;

/**
 * Represents a market buy order. The order is to be executed at the prevailing market price.
 * @author dixya
 */
public class MarketBuyOrder extends AbstractOrder
implements MarketOrder, BuyOrder{
    
    public MarketBuyOrder(String accountId, int numberOfShares, String stockTicker) {
        super(accountId, numberOfShares, stockTicker);
    }

    @Override
    public int valueOfOrder(int pricePerShare) {
        int total = 1;
        if (pricePerShare > 0) {
            total = pricePerShare * this.getNumberOfShares();
        } else {
            String msg = "Price per share should be greater than 0";
            LOG.warn("msg");
        }
        return -total;

    }
    
}

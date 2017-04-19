/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.order;

/**
 *
 * @author dixya
 */
public class MarketSellOrder extends AbstractOrder
implements MarketOrder, SellOrder{
    
    public MarketSellOrder(String accountId, int numberOfShares, String stockTicker) {
        super(accountId, numberOfShares, stockTicker);
    }
    /**
     * Calculates the value of the order at a given share price.

     * @param pricePerShare
     * @return 
     */
    @Override
    public int valueOfOrder(int pricePerShare){
        int total = 0;
        if (pricePerShare > 0) {
            total = pricePerShare * numberOfShares;
        } else {
            String msg = "Price per share should be greater than 0";
            LOG.warn("msg");
        }
        return total;
    }
    
}

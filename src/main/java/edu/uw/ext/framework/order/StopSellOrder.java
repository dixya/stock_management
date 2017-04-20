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
public class StopSellOrder extends PricedOrder
implements SellOrder{
    
    public StopSellOrder(String accountId, int numberOfShares, String stockTicker) {
        super(accountId, numberOfShares, stockTicker);
    }


    
}

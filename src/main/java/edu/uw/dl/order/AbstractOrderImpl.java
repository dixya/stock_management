/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.order;

import edu.uw.ext.framework.order.AbstractOrder;
import edu.uw.ext.framework.order.Order;

/**
 *
 * @author dixya
 */
public abstract class AbstractOrderImpl extends AbstractOrder implements Order {

    public AbstractOrderImpl(String accountId, int numberOfShares, String stockTicker) {
        super(accountId, numberOfShares, stockTicker);
    }

    @Override
    public int valueOfOrder(int i) {
        return i;
    }
    
    
}

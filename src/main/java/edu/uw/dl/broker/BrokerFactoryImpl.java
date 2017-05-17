/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.broker;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerFactory;
import edu.uw.ext.framework.exchange.StockExchange;

/**
 *
 * @author dixya
 */
public class BrokerFactoryImpl implements BrokerFactory{

    /**
     * Instantiates a new BrokerImpl
     * @param string broker's name
     * @param am account manager that broker uses
     * @param se exchange that broker uses
     * @return new instance of BrokerImpl
     */
    @Override
    public Broker newBroker(String name, AccountManager am, StockExchange se) {
        return new BrokerImpl(name,se,am);
    }
    
}

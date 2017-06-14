/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.BrokerWithThread;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerFactory;
import edu.uw.ext.framework.exchange.StockExchange;

/**
 *
 * @author dixya
 */
public class ThreadedBrokerFactory implements BrokerFactory {

    @Override
    public Broker newBroker(String name, AccountManager am, StockExchange se) {
        return new ThreadedBroker(name, se, am);
    }

}

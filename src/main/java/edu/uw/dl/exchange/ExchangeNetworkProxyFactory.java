/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.exchange;
import edu.uw.ext.framework.exchange.NetworkExchangeProxyFactory;
import edu.uw.ext.framework.exchange.StockExchange;

/**
 * A factory interface for creating ExchangeNetworkProxy instances.
 * @author dixya
 */
public class ExchangeNetworkProxyFactory implements NetworkExchangeProxyFactory{
    public ExchangeNetworkProxyFactory(){
        
    }

    /**
     * Instantiates a enabled ExchangeNetworkProxy.
     * @param multicastIP
     * @param multicastPort
     * @param commandIP
     * @param commandPort
     * @return     a newly instantiated ExchangeAdapter.
     */
    @Override
    public StockExchange newProxy(String multicastIP, int multicastPort, String commandIP, int commandPort) {
        return new ExchangeNetworkProxy(multicastIP,multicastPort,commandIP,commandPort);
    }
    
}

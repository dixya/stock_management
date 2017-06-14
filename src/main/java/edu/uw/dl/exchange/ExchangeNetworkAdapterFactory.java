/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.exchange;
import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.NetworkExchangeAdapterFactory;
import edu.uw.ext.framework.exchange.StockExchange;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A NetworkExchangeAdapterFactory implementation for creating ExchangeNetworkAdapter instances.
 * @author dixya
 */
public class ExchangeNetworkAdapterFactory
implements NetworkExchangeAdapterFactory{
    StockExchange se;
    String multicastIp;
    int multicastPort;
    int commandPort;
    private static final Logger LOG=LoggerFactory.getLogger(ExchangeNetworkAdapterFactory.class);
    
    /**
     * Instantiates an ExchangeNetworkAdapter.
     */
    public ExchangeNetworkAdapterFactory(){
        
    }

    /**
     * 
     * @param se the underlying real exchange
     * @param multicastIp     the multicast ip address used to distribute events

     * @param multicastPort the port used to distribute events
     * @param commandPort the listening port to be used to accept command requests
     * @return a newly instantiated ExchangeNetworkAdapter, or null if instantiation fails.
     */
    @Override
    public ExchangeAdapter newAdapter(StockExchange se, String multicastIp, int multicastPort, int commandPort) {
        this.se=se;
        this.multicastIp=multicastIp;
        this.multicastPort=multicastPort;
        this.commandPort=commandPort;
        ExchangeNetworkAdapter ENA = null;
        try {
            ENA= new ExchangeNetworkAdapter(se,multicastIp,multicastPort,commandPort);
        } catch (UnknownHostException | SocketException ex) {
            LOG.warn("Error in exchange network adapter initialization");
        }
        return ENA;
    }
    
}

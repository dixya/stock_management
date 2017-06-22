/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.exchange;

import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.StockExchange;
import java.net.SocketException;
import java.net.UnknownHostException;
import edu.uw.ext.framework.exchange.ExchangeAdapter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides a network interface to an exchange.
 * @author dixya
 */
public class ExchangeNetworkAdapter implements ExchangeAdapter{
    private static final Logger LOG=LoggerFactory.getLogger(ExchangeNetworkAdapter.class);
    private final StockExchange realExchange;
    String multicastIP;
    int multicastPort;
    int commandPort;
    /** The event socket */
    private MulticastSocket eventSocket;
    /** Datagram packet used to multicast events */
    private DatagramPacket datagramPacket;
    private CommandListener cmdListener;

    /**
     * Constructor.

     * @param exchng the exchange used to service the network requests
     * @param multicastIP the ip address used to propagate price changes
     * @param multicastPort the ip port used to propagate price changes
     * @param commandPort the ports for listening for commands.
     * @throws UnknownHostException
     * @throws SocketException if any socket exception occurs.
     */
    public ExchangeNetworkAdapter(final StockExchange exchng, final String multicastIP, final int multicastPort, final int commandPort) throws UnknownHostException,
                              SocketException{
        this.realExchange=exchng;
        this.multicastIP=multicastIP;
        this.multicastPort=multicastPort;
        this.commandPort=commandPort;
        final InetAddress multicastGroup=InetAddress.getByName(multicastIP);
        final byte[] buf={};
        datagramPacket=new DatagramPacket(buf,0,multicastGroup,multicastPort);
        try{
            eventSocket=new MulticastSocket();
            eventSocket.setTimeToLive(5);
            if(LOG.isInfoEnabled()){
                LOG.info("Multicasting events to :" +multicastIP);
            }
        
    }catch(IOException e){
    LOG.error("Event socket initialization failed",e);
}
        cmdListener=new CommandListener(commandPort,exchng);
        Executors.newSingleThreadExecutor().execute(cmdListener);
        /* register the listener with the real exchange */
        realExchange.addExchangeListener(this);
    }

    /**
     * Close the adapter.
     */
    @Override
    public void close() {
        realExchange.removeExchangeListener(this);
        try {
            cmdListener.terminate();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ExchangeNetworkAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        eventSocket.close();
    }

    /**
     * The exchange has opened and prices are adjusting 
     * - add listener to receive price change events from the exchange and multicast them to brokers.
     * @param ee the event
     */
    @Override
    public void exchangeOpened(ExchangeEvent ee) {
        LOG.info("Exchange opening...");
        try{
            sendMulticastEvent(ProtocolConstants.OPEN_EVNT);
        } catch(final IOException ex){
            LOG.error("Error in joining price change group", ex);
        }
    }

    /**
     * The exchange has closed - notify clients and remove price change listener.
     * @param ee the event
     */
    @Override
    public void exchangeClosed(ExchangeEvent ee) {
        LOG.info("Exchange is closed");
        try{
            sendMulticastEvent(ProtocolConstants.CLOSED_EVNT);
        } catch(IOException ex){
            LOG.error("Error in multicasting exchange closed",ex);
        }
    }

    /**
     * Processes price change events.
     * @param ee the event
     */
    @Override
    public void priceChanged(ExchangeEvent ee) {
        final String symbol=ee.getTicker();
        final int price=ee.getPrice();
        final String msg=String.join(ProtocolConstants.ELEMENT_DELIMITER,ProtocolConstants.PRICE_CHANGE_EVNT,symbol,Integer.toString(price));
        LOG.info(msg);
        try{
            sendMulticastEvent(msg);
        } catch(final IOException ex){
            LOG.error("Error in multicasting price change",ex);
        }
    }

    /**
     * Method to write a buffer to the 'event' multicast socket.
     * @param msg the event
     * @throws IOException if any IOEception occurs in the network.
     */
    private synchronized void sendMulticastEvent(final String msg) throws IOException {
        final byte[] buff=msg.getBytes(ProtocolConstants.ENCODING);
        datagramPacket.setData(buff);
        datagramPacket.setLength(buff.length);
        eventSocket.send(datagramPacket);
    }
    
}

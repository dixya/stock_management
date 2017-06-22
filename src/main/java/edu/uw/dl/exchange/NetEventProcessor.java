/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.exchange;
import static edu.uw.dl.exchange.ProtocolConstants.CLOSED_EVNT;
import static edu.uw.dl.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.dl.exchange.ProtocolConstants.ENCODING;
import static edu.uw.dl.exchange.ProtocolConstants.EVENT_ELEMENT;
import static edu.uw.dl.exchange.ProtocolConstants.OPEN_EVNT;
import static edu.uw.dl.exchange.ProtocolConstants.PRICE_CHANGE_EVNT;
import static edu.uw.dl.exchange.ProtocolConstants.PRICE_CHANGE_EVNT_PRICE_ELEMENT;
import static edu.uw.dl.exchange.ProtocolConstants.PRICE_CHANGE_EVNT_TICKER_ELEMENT;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import javax.swing.event.EventListenerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
     * Listens for (by joining the multicast group) and 
     * processes events received from the exchange. 
     * Processing the events consists of propagating them to registered listeners.
     */
public class NetEventProcessor implements Runnable{
    private static final Logger LOG=LoggerFactory.getLogger(NetEventProcessor.class);
    private static final int BUFFER_SIZE=1024;
    private String eventIpAddress;
    private int eventPort;
    private EventListenerList listenerList=new EventListenerList();
    /**
     * Constructor.
     * @param eventIpAddress the multicast IP address to connect to
     * @param eventPort the multicast port to connect to.
     */
    public NetEventProcessor(String eventIpAddress, int eventPort){
        this.eventIpAddress=eventIpAddress;
        this.eventPort=eventPort;
    }
    

    /**
     * Continuously accepts and processes market and price change events.
     */
    @Override
    public void run() {
        try(MulticastSocket eventSocket=new MulticastSocket(eventPort)){
            final InetAddress eventGroup=InetAddress.getByName(eventIpAddress);
            eventSocket.joinGroup(eventGroup);
            if(LOG.isInfoEnabled()){
                LOG.info("Receiving events from eventipadress= "+eventIpAddress);
            }
            final byte[] buf=new byte[BUFFER_SIZE];
            final DatagramPacket packet=new DatagramPacket(buf, buf.length);
            while(true){
                eventSocket.receive(packet);
                final String msg=new String(packet.getData(),packet.getOffset(),packet.getLength(),ENCODING);
                final String[] elements=msg.split(ELEMENT_DELIMITER);
                final String eventType=elements[EVENT_ELEMENT];
                switch(eventType){
                    case OPEN_EVNT:
                        fireListeners(ExchangeEvent.newOpenedEvent(this));
                        break;
                    case CLOSED_EVNT:
                        fireListeners(ExchangeEvent.newClosedEvent(this));
                        break;
                    case PRICE_CHANGE_EVNT:
                        final String ticker=elements[PRICE_CHANGE_EVNT_TICKER_ELEMENT];
                        final String priceStr=elements[PRICE_CHANGE_EVNT_PRICE_ELEMENT];
                        int price=-1;
                        try{
                            price=Integer.parseInt(priceStr);
                        }catch(final NumberFormatException ex){
                            LOG.warn(String.format("String to integer conversion failed"));
                        }
                        fireListeners(ExchangeEvent.newPriceChangedEvent(this, ticker, price));
                        break;
                    default:
                        break;
                }
            }
        } catch(final IOException ex){
            LOG.warn("Server error",ex);
        }
        LOG.warn("Completed processing events");
    }
    /**
     * Adds a market listener.

     * @param l the listener to add 
     */
    public void addExchangeListener(ExchangeListener l){
        listenerList.add(ExchangeListener.class, l);
    }
    /**
     * Removes a market listener.
     * @param l the listener to remove 
     */
    public void removeExchangeListener(ExchangeListener l){
                listenerList.remove(ExchangeListener.class, l);

    }

    private void fireListeners(ExchangeEvent event) {
        ExchangeListener[] listeners;
        listeners=listenerList.getListeners(ExchangeListener.class);
        for(ExchangeListener listener:listeners){
            switch(event.getEventType()){
                case OPENED:
                    listener.exchangeOpened(event);
                    break;
                case CLOSED:
                    listener.exchangeClosed(event);
                    break;
                case PRICE_CHANGED:
                    listener.priceChanged(event);
                    break;
                default:
                    LOG.error(String.format("Bad exchange event %s", event.getEventType()));
                    break;
            }
        }
    }






}

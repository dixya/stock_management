/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.exchange;

import static edu.uw.dl.exchange.ProtocolConstants.BUY_ORDER;
import static edu.uw.dl.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.dl.exchange.ProtocolConstants.ENCODING;
import static edu.uw.dl.exchange.ProtocolConstants.EXECUTE_TRADE_CMD;
import static edu.uw.dl.exchange.ProtocolConstants.GET_QUOTE_CMD;
import static edu.uw.dl.exchange.ProtocolConstants.GET_STATE_CMD;
import static edu.uw.dl.exchange.ProtocolConstants.GET_TICKERS_CMD;
import static edu.uw.dl.exchange.ProtocolConstants.OPEN_STATE;
import static edu.uw.dl.exchange.ProtocolConstants.INVALID_STOCK;
import static edu.uw.dl.exchange.ProtocolConstants.SELL_ORDER;

import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.Order;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for interacting with a network accessible exchange. This
 * SocketExchange methods encode the method request as a string, per
 * ProtocolConstants, and send the command to the ExchangeNetworkAdapter,
 * receive the response decode it and return the result.
 *
 * @author dixya
 */
public class ExchangeNetworkProxy implements StockExchange {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeNetworkProxy.class);
    private String commandIpAddress;
    private int commandPort;
    private NetEventProcessor eventProcessor;
    String eventIpAddress;
    int eventPort;

    /**
     * Constructor.
     *
     * @param eventIpAddress the multicast IP address to connect to
     * @param eventPort the multicast port to connect to
     * @param cmdIpAddress the address the exchange accepts request on
     * @param cmdPort the address the exchange accepts request on.
     */
    public ExchangeNetworkProxy(String eventIpAddress, int eventPort, String cmdIpAddress, int cmdPort) {
        this.eventIpAddress = eventIpAddress;
        this.eventPort = eventPort;
        this.commandIpAddress = cmdIpAddress;
        this.commandPort = cmdPort;
        eventProcessor = new NetEventProcessor(eventIpAddress, eventPort);
        Executors.newSingleThreadExecutor().execute(eventProcessor);
    }

    /**
     * The state of the exchange.
     *
     * @return true if the exchange is open otherwise false
     */
    @Override
    public boolean isOpen() {
        final String response = sendTcpCmd(GET_STATE_CMD);
        final boolean state = OPEN_STATE.equals(response);
        return state;
    }

    /**
     * Gets the ticker symbols for all of the stocks in the traded on the
     * exchange.
     *
     * @return the stock ticker symbols
     */
    @Override
    public String[] getTickers() {
        final String response = sendTcpCmd(GET_TICKERS_CMD);
        final String[] tickers = response.split(ELEMENT_DELIMITER);
        return tickers;
    }

    /**
     * Gets a stocks current price.
     *
     * @param ticker
     * @return the quote, or null if the quote is unavailable.
     */
    @Override
    public StockQuote getQuote(String ticker) {
        final String cmd = String.join(ELEMENT_DELIMITER, GET_QUOTE_CMD, ticker);
        final String response = sendTcpCmd(cmd);
        int price = INVALID_STOCK;
        try {
            price = Integer.parseInt(response);
        } catch (final NumberFormatException ex) {
            LOG.warn("String to integer conversion failed");
        }
        StockQuote quote = null;
        if (price >= 0) {
            quote = new StockQuote(ticker, price);
        }
        return quote;
    }

    /**
     * Adds a market listener. Delegates to the NetEventProcessor.
     *
     * @param el the listener to add.
     */
    @Override
    public void addExchangeListener(ExchangeListener el) {
        eventProcessor.addExchangeListener(el);
    }

    /**
     * Removes a market listener. Delegates to the NetEventProcessor.
     *
     * @param el the listener to remove.
     */
    @Override
    public void removeExchangeListener(ExchangeListener el) {
        eventProcessor.removeExchangeListener(el);
    }

    /**
     * Creates a command to execute a trade and sends it to the exchange.
     *
     * @param order the order to execute
     * @return the price the order was executed at
     */
    @Override
    public int executeTrade(Order order) {
        final String orderType = (order.isBuyOrder()) ? BUY_ORDER : SELL_ORDER;
        final String cmd = String.join(ELEMENT_DELIMITER, EXECUTE_TRADE_CMD, orderType, order.getAccountId(), order.getStockTicker(), Integer.toString(order.getNumberOfShares()));
        final String response = sendTcpCmd(cmd);
        int executionPrice = 0;
        try {
            executionPrice = Integer.parseInt(response);
        } catch (NumberFormatException ex) {
            LOG.warn("String to integer conversion failed", ex);
        }
        return executionPrice;
    }

    private String sendTcpCmd(String cmd) {
        PrintWriter printWriter = null;
        BufferedReader br = null;
        String response = "";
        try (Socket socket = new Socket(commandIpAddress, commandPort)) {
            if (LOG.isInfoEnabled()) {
                LOG.info(String.format("Connected to server %s %d", socket.getLocalAddress(), socket.getLocalPort()));
            }
            final InputStream inputStream = socket.getInputStream();
            final Reader reader = new InputStreamReader(inputStream, ENCODING);
            br = new BufferedReader(reader);
            final OutputStream outputStream = socket.getOutputStream();
            final Writer writer = new OutputStreamWriter(outputStream, ENCODING);
            printWriter = new PrintWriter(writer, true);
            printWriter.println(cmd);
            response = br.readLine();
        } catch (final IOException ex) {
            LOG.warn("Error in sending command to exchange", ex);
        }
        return response;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.exchange;

import static edu.uw.dl.exchange.ProtocolConstants.BUY_ORDER;
import static edu.uw.dl.exchange.ProtocolConstants.CMD_ELEMENT;
import static edu.uw.dl.exchange.ProtocolConstants.ELEMENT_DELIMITER;
import static edu.uw.dl.exchange.ProtocolConstants.ENCODING;
import static edu.uw.dl.exchange.ProtocolConstants.EXECUTE_TRADE_CMD;
import static edu.uw.dl.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT;
import static edu.uw.dl.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_SHARES_ELEMENT;
import static edu.uw.dl.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_TICKER_ELEMENT;
import static edu.uw.dl.exchange.ProtocolConstants.EXECUTE_TRADE_CMD_TYPE_ELEMENT;
import static edu.uw.dl.exchange.ProtocolConstants.GET_QUOTE_CMD;
import static edu.uw.dl.exchange.ProtocolConstants.GET_STATE_CMD;
import static edu.uw.dl.exchange.ProtocolConstants.GET_TICKERS_CMD;
import static edu.uw.dl.exchange.ProtocolConstants.INVALID_STOCK;
import static edu.uw.dl.exchange.ProtocolConstants.QUOTE_CMD_TICKER_ELEMENT;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An instance of this class is dedicated to executing commands received from
 * clients.
 *
 * @author dixya
 */
public final class CommandHandler
  implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);
    private final Socket socket;
    private final StockExchange realExchange;
    //private PrintWriter printWriter;

    /**
     * Constructor.
     *
     * @param sock the socket for communication with the client
     * @param realExchange the "real" exchange to dispatch commands to.
     */
    protected CommandHandler(Socket sock, StockExchange realExchange) {
        this.socket = sock;
        this.realExchange = realExchange;
    }

    /**
     * Processes the command.
     */
    @Override
    public void run() {
        try {
            final InputStream inputStream = socket.getInputStream();
            final Reader rdr = new InputStreamReader(inputStream, ENCODING);
            final BufferedReader br = new BufferedReader(rdr);

            final OutputStream outputStream = socket.getOutputStream();
            // final Writer writer = new OutputStreamWriter(outputStream, "true");
            final Writer writer = new OutputStreamWriter(outputStream, ENCODING);

            String msg = br.readLine();
            if (null == msg) {
                msg = "";
            }
            final PrintWriter printWriter = new PrintWriter(writer, true);
            if (LOG.isInfoEnabled()) {
                LOG.info(String.format("Received command %s ", msg));

            }
            final String[] elements = msg.split(ELEMENT_DELIMITER);
            final String cmd = elements[CMD_ELEMENT];

            //Dispatch command
            if (LOG.isInfoEnabled()) {
                LOG.info(String.format("Processing command :%s", cmd));
            }
            switch (cmd) {
                case GET_STATE_CMD:
                    doGetState(printWriter);
                    break;
                case GET_TICKERS_CMD:
                    doGetTickers(printWriter);
                    break;
                case GET_QUOTE_CMD:
                    doGetQuote(elements, printWriter);
                    break;
                case EXECUTE_TRADE_CMD:
                    doExecuteTrade(elements, printWriter);
                    break;
                default:
                    LOG.error(String.format("Unrecognized command :%s", cmd));
                    break;
            }
        } catch (final IOException ex) {
            LOG.error("Error sending response", ex);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                    LOG.info("Socket has been closed");
                }
            } catch (final IOException ioex) {
                LOG.info("Error closing socket", ioex);
            }
        }

    }

    private void doGetState(final PrintWriter printWriter) {
        final String response = realExchange.isOpen() ? ProtocolConstants.OPEN_STATE : ProtocolConstants.CLOSED_STATE;
        printWriter.println(response);
    }

    private void doGetTickers(final PrintWriter printWriter) {
        final String[] tickers = realExchange.getTickers();
        final String tickersStr = String.join(ELEMENT_DELIMITER, tickers);
        printWriter.println(tickersStr);
    }

    private void doGetQuote(final String[] elements, final PrintWriter printWriter) {
        String ticker = elements[QUOTE_CMD_TICKER_ELEMENT];
        final StockQuote quote = realExchange.getQuote(ticker);
        int price = (quote == null) ? INVALID_STOCK : quote.getPrice();
        printWriter.println(price);
    }

    private void doExecuteTrade(final String[] elements, final PrintWriter printWriter) {
        if (realExchange.isOpen()) {
            final String orderType = elements[EXECUTE_TRADE_CMD_TYPE_ELEMENT];
            final String acctID = elements[EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT];
            String ticker = elements[EXECUTE_TRADE_CMD_TICKER_ELEMENT];
            final String shares = elements[EXECUTE_TRADE_CMD_SHARES_ELEMENT];
            int qty = -1;
            try {
                qty = Integer.parseInt(shares);
            } catch (final NumberFormatException ex) {
                LOG.warn(String.format("Unable to convert string into integer"));
            }
            Order order;
            if (BUY_ORDER.equals(orderType)) {
                order = new MarketBuyOrder(acctID, qty, ticker);
            } else {
                order = new MarketSellOrder(acctID, qty, ticker);
            }
            int price = realExchange.executeTrade(order);
            printWriter.println(price);
        } else {
            printWriter.println(0);
        }
    }
}

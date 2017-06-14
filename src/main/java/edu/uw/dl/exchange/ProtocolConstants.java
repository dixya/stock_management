/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.exchange;

/**
 * Constants for the command strings composing the exchange protocol. The
 * protocol supports events and commands. Events are one way messages sent from
 * the exchange to the broker(s).
 *
 * @author dixya
 */
public class ProtocolConstants {

    public static final String OPEN_EVNT = "OPEN_EVNT";
    public static final String CLOSED_EVNT = "CLOSED_EVNT";
    public static final String PRICE_CHANGE_EVNT = "PRICE_CHANGE_EVNT";
    public static final String GET_TICKERS_CMD = "GET_TICKERS_CMD";
    public static final String GET_QUOTE_CMD = "GET_QUOTE_CMD";
    public static final String GET_STATE_CMD = "GET_STATE_CMD";
    public static final String EXECUTE_TRADE_CMD = "EXECUTE_TRADE_CMD";
    public static final String OPEN_STATE = "OPEN_STATE";
    public static final String CLOSED_STATE = "CLOSED_STATE";
    public static final String BUY_ORDER = "BUY_ORDER";
    public static final String SELL_ORDER = "SELL_ORDER";
    public static final String ELEMENT_DELIMITER = ":";
    public static final int EVENT_ELEMENT=0;
    public static final int PRICE_CHANGE_EVNT_TICKER_ELEMENT=1;
    public static final int PRICE_CHANGE_EVNT_PRICE_ELEMENT=2;
    public static final int CMD_ELEMENT = 0;
    public static final int QUOTE_CMD_TICKER_ELEMENT = 1;
    public static final int EXECUTE_TRADE_CMD_TYPE_ELEMENT = 1;
    public static final int EXECUTE_TRADE_CMD_ACCOUNT_ELEMENT = 2;
    public static final int EXECUTE_TRADE_CMD_TICKER_ELEMENT = 3;
    public static final int EXECUTE_TRADE_CMD_SHARES_ELEMENT = 4;
    public static final int INVALID_STOCK = -1;
    public static final String ENCODING="ISO-8859-1";

    private ProtocolConstants() {

    }

}

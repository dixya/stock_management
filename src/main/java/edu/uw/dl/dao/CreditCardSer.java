/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.CreditCard;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author dixya
 */
public class CreditCardSer {

    private static final String NULL_STR = "<null>";
    private static final Logger LOG = LoggerFactory.getLogger(CreditCardSer.class);

    public static CreditCard read(FileInputStream in) throws AccountException, IOException, BeansException {
        final BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
        try (ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml")) {
            final CreditCard card = appContext.getBean(CreditCard.class);
            String tmp = null;
            tmp = rdr.readLine();
            card.setIssuer((NULL_STR.equals(tmp)) ? null : tmp);
            tmp = rdr.readLine();
            card.setType((NULL_STR.equals(tmp)) ? null : tmp);
            tmp = rdr.readLine();
            card.setHolder((NULL_STR.equals(tmp)) ? null : tmp);
            tmp = rdr.readLine();
            card.setAccountNumber((NULL_STR.equals(tmp)) ? null : tmp);
            tmp = rdr.readLine();
            card.setExpirationDate((NULL_STR.equals(tmp)) ? null : tmp);
            return card;
        }

    }

    public static void write(FileOutputStream out, CreditCard card) {
        final PrintWriter writer = new PrintWriter(out);
        if (card != null) {
            String s;
            s = card.getIssuer();
            writer.println(s == null ? NULL_STR : s);
            s = card.getType();
            writer.println(s == null ? NULL_STR : s);
            s = card.getHolder();
            writer.println(s == null ? NULL_STR : s);
            s = card.getAccountNumber();
            writer.println(s == null ? NULL_STR : s);
            s = card.getExpirationDate();
            writer.println(s == null ? NULL_STR : s);
        }
        writer.flush();
    }

}

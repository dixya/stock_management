/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.account;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dixya
 */
public class AccountFactoryImpl implements AccountFactory {

    String accountName;
    byte[] hashedPassword;
    int initialBalance;
    Account account;

    private static final Logger LOG = LoggerFactory.getLogger(AccountFactoryImpl.class);

    /**
     * No argument constructor.
     */
    AccountFactoryImpl() {

    }

    /**
     * Creates new Account
     * @param accountName the name of account
     * @param hashedPassword hashed password
     * @param initialBalance initial balance of the account.
     * @return new instance of account.
     */
    @Override
    public Account newAccount(final String accountName, final byte[] hashedPassword, final int initialBalance) {
        account = null;

        try {
            account = new AccountImpl(accountName, hashedPassword, initialBalance);
            if (LOG.isInfoEnabled()) {
                LOG.info("Account created successfully of " + accountName + " with balance= " + initialBalance);
            }
        } catch (final AccountException ex) {
            LOG.warn("Error in creation of new account" + ex);
        }

        return account;
    }

}

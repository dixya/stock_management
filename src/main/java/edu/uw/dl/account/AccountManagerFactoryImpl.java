/*
 * To

change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.account;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.AccountManagerFactory;
import edu.uw.ext.framework.dao.AccountDao;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dixya
 */
public class AccountManagerFactoryImpl implements AccountManagerFactory {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AccountManagerFactoryImpl.class);

    public AccountManagerFactoryImpl() {

    }

    /**
     * Instantiates a new account manager instance.
     *
     * @param dao the data access object to be used by the account manager
     * @return a newly instantiated account manager.
     *
     */
    @Override
    public AccountManager newAccountManager(AccountDao dao) {

        AccountManagerImpl accManager = null;
        try {
            accManager = new AccountManagerImpl(dao);
        } catch (AccountException ex) {
            LOG.info("Error occurred in creation of new account manager");
        }

        return accManager;
    }

}

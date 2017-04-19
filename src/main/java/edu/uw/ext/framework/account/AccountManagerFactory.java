/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.account;

import edu.uw.ext.framework.dao.AccountDao;

/**
 * Factory interface for the creation of account managers. Implementations of this class must provide a no argument constructor.
 * @author dixya
 */
public interface AccountManagerFactory {
    /**
     * Instantiates a new account manager instance.
     * @param dao the data access object to be used by the account manager
     * @return a newly instantiated account manager.
     */
    AccountManager newAccountManager(AccountDao dao);

    
}

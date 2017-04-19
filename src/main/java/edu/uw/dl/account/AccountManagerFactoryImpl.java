/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.account;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.AccountManagerFactory;
import edu.uw.ext.framework.dao.AccountDao;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dixya
 */
public class AccountManagerFactoryImpl implements AccountManagerFactory{
    AccountManagerFactoryImpl(){
        
    }
/**
 * Instantiates a new account manager instance.
 * @param dao the data access object to be used by the account manager
 * @return a newly instantiated account manager.
 * 
 */
    @Override
    public AccountManager newAccountManager(AccountDao dao) {     
        
        AccountManagerImpl accManager = null;
        try {
             accManager=new AccountManagerImpl(dao);
        } catch (AccountException ex) {
            Logger.getLogger(AccountManagerFactoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
return accManager;
    }
    
}

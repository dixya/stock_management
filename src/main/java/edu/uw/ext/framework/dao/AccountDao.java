/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.dao;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;

/**
 * Defines the methods needed to store and load accounts from a persistent storage mechanism. The implementing class must provide a no argument constructor.
 * @author dixya
 */
public interface AccountDao extends AutoCloseable{
    /**
     * Lookup an account in based on account name.
     * @param accountName the name of the desired account
     * @return the account if located otherwise null
     */
    Account getAccount(String accountName);
    /**
     * Adds or updates an account.
     * @param account   the account to add/update
     * @throws AccountException if operation fails.
     */
    void setAccount(Account account) throws AccountException;
    /**
     * Remove the account.
     * @param accountName the name of the account to be deleted
     * @throws AccountException if operation fails.
     */
    void deleteAccount(String accountName) throws AccountException;
    /**
     * Remove all accounts. This is primarily available to facilitate testing.
     * @throws AccountException if operation fails
     */
    void reset() throws AccountException;
    /**
     * Close the DAO. Release any resources used by the DAO implementation. If the DAO is already closed then invoking this method has no effect.
     * @throws AccountException if operation fails.
     */
    @Override
    void close() throws AccountException;




    
}

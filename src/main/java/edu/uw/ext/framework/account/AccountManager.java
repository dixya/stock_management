/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.account;


/**
 * Interface for managing accounts. Provides interfaces for the basic account operations; create, delete, authentication and persistence.
 * @author dixya
 */
public interface AccountManager {
    /**
     * Used to persist an account.


     * @param account the account to persist

     * @throws AccountException if operation fails.
     */
    void persist(Account account) throws AccountException;
    /**
     * Lookup an account based on account name.
     * @param accountName the name of the desired account.
     * @return the account if located otherwise null
     * @throws AccountException if operation fails.
     */
    Account getAccount(String accountName) throws AccountException;
    /**
     * Remove the account.
     * @param accountName the name of the account to remove.
     * @throws AccountException if operation fails.
     */
    void deleteAccount(String accountName) throws AccountException;
    /**
     * Creates an account. The creation process should include persisting the account and 
     * setting the account manager reference (through the Account registerAccountManager operation).
     * @param accountName the name for account to add
     * @param password the password used to gain access to the account.
     * @param balance the initial balance of the account.
     * @return the newly created account
     * @throws AccountException if operation fails.
     */
    Account createAccount(String accountName, String password, int balance) throws AccountException;
    /**
     * Check whether a login is valid. An account must exist with the account name and the password must match.
     * @param accountName name of account the password is to be validated for
     * @param password password is to be validated
     * @return  true if password is valid for account identified by accountName
     * @throws AccountException if error occurs accessing accounts.
     */
    boolean validateLogin(String accountName, String password) throws AccountException;
    /**
     * Release any resources used by the AccountManager implementation. 
     * Once closed further operations on the AccountManager may fail.
     * @throws AccountException if error occurs accessing accounts.
     */
    void close() throws AccountException;
    
}

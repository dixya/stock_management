/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.account;

/**
 * Factory interface for the creation of accounts. Implementations of this class must provide a no argument constructor.
 * @author dixya
 */
public interface AccountFactory {
    /**
     * Instantiates a new account instance.
     * @param accountName the account name
     * @param hashedPassword the password hash
     * @param initialBalance the balance
     * @return the newly instantiated account, or null if unable to instantiate the account
     */
    Account newAccount(String accountName, byte[] hashedPassword, int initialBalance);
    
}

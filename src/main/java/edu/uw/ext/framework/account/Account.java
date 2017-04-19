/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.account;

import edu.uw.ext.framework.order.Order;
import java.io.Serializable;
//import org.springframework.core.annotation.Order;

/**
 * A pure JavaBean representation of an account.
 * @author dixya
 */
public interface Account
extends Serializable {
    /** Get the account name
     * @return the name of the account.*/      
    public String getName(); 
    /**
     * Sets the account name. This operation is not generally used but is provided for JavaBean conformance.
     * @param acctName the value to be set for the account name
     * @throws AccountException if the account name is unacceptable.
     */
    void setName(String acctName) throws AccountException;
    /**
     * Gets the hashed password.
     * @return the hashed password.
     */
    byte[] getPasswordHash();
    /**
     * Sets the hashed password.
     * @param passwordHash the value to be st for the password hash
     */
    void setPasswordHash(byte[] passwordHash);
    /**
     * Gets the account balance, in cents.
     * @return  the current balance of the account.
     */
    int getBalance();
    /**
     * Sets the account balance.
     * @param balance the value to set the balance to in cents
     */
    void setBalance(int balance);
    /**
     * Gets the full name of the account holder.
     * @return the account holders full name
     */
    String getFullName();
    /**
     * Sets the full name of the account holder.
     * @param fullName the account holders full name.
     */
    void setFullName(String fullName);
    /**
     * Gets the account address.
     * @return the accounts address
     */
    Address getAddress();
    /** 
     * Sets the account address.
     * @param address the address for the account
     */
    void setAddress(Address address);
    /**
     * Gets the phone number.
     * @return     the phone number
     */
    String getPhone();
    /**
     * Sets the account phone number.


     * @param phone value for the account phone number.
     */
    
    void setPhone(String phone);
    /**
     * Gets the email address.
     * @return the email address
     */
    String getEmail();
    /**
     * Sets the account email address.
     * @param email the email address.
     */
    void setEmail(String email);
    /**
     * Gets the account credit card.
     * @return the credit card
     */
    CreditCard getCreditCard();
    /**
     * Sets the account credit card.
     * @param card the value to be set for the credit card
     */
    void setCreditCard(CreditCard card);
    /**
     * Sets the account manager responsible for persisting/managing this account. This may be invoked exactly once on any given account,
     * any subsequent invocations should be ignored. The account manager member should not be serialized with implementing class object.
     * @param m the account manager.
     */
    void registerAccountManager(AccountManager m);
    /**
     * Incorporates the effect of an order in the balance.
     * @param order the order to be reflected in the account.
     * @param executionPrice the price the order was executed at.
     */
    void reflectOrder(Order order,  int executionPrice);




   


    
}

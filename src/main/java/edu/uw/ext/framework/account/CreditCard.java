/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.account;

import java.io.Serializable;

/**
 * Interface for a pure JavaBean implementation of a credit card. The implementing class must provide a no argument constructor.
 * @author dixya
 */
public interface CreditCard extends Serializable{
    /**
     * Gets the card account number.
     * @return account number.
     */
    String getAccountNumber();
    /**
     * 
     Gets the card expiration date.
     * @return expiration date.
     */
    String getExpirationDate();
    /**
     * Gets the card holder's name.
     * @return the card holders name
     */
    String getHolder();
    /**
     * Gets the card issuer.

     * @return the card issuer


     */
    String getIssuer();
    /**
     * Gets the card type.
     * @return the card type
     */
    String getType();
    /**
     * Sets the card account number.
     * @param accountNumber 
     */
    void setAccountNumber(String accountNumber);
    /**
     * Sets the card expiration date.

     * @param expDate card expiration date.
     */
    void setExpirationDate(String expDate);
    /**
     * Sets the card holder's name.

     * @param name card holder's name.
     */
    void setHolder(String name);
    /**
     * Sets the card issuer.

     * @param issuer card issuer
     */
    void setIssuer(String issuer);
    /**
     * Sets the card type.

     * @param type the card type.
     */
    void setType(String type);
    
}

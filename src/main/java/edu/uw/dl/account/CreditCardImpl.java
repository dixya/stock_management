/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.account;

import edu.uw.ext.framework.account.CreditCard;

/**
 *
 * @author dixya
 */
public class CreditCardImpl implements CreditCard {

    private static final long serialVersionUID = 1L;
    String accountNumber;
    String expirationDate;
    String holder;
    String issuer;
    String type;

    /**
     * No argument constructor.
     */
    public CreditCardImpl() {
        super();
    }

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String getExpirationDate() {
        return expirationDate;
    }

    @Override
    public String getHolder() {
        return holder;
    }

    @Override
    public String getIssuer() {
        return issuer;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public void setExpirationDate(String expDate) {
        this.expirationDate = expDate;
    }

    @Override
    public void setHolder(String name) {
        this.holder = name;
    }

    @Override
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.account;

import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dixya
 */
public final class AccountImpl implements Account {

    private static final long serialVersionUID = 1L;
    String accountName;
    byte[] passwordHash;
    int balance;
    private String fullName;
    private Address address;
    private String phone;
    String email;
    CreditCard creditCard;
    private transient AccountManager accountManager;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AccountImpl.class);

    /**
     * No argument constructor.
     */
    public AccountImpl() {

    }

    //check balance here with constructor
    public AccountImpl(final String accountName, final byte[] passwordHash, final int balance) throws AccountException {
        if (balance < 100000) {
            final String msg = String.format("Account cannot be created since balance= %s", balance);
            LOG.warn(msg);
            throw new AccountException(msg);
        } else {
            setName(accountName);
            setPasswordHash(passwordHash);
            this.balance = balance;
        }

    }

    @Override
    public String getName() {
        return accountName;
    }

    @Override
    public void setName(String acctName) throws AccountException {
        if (acctName == null || acctName.length() < 8) {
            String msg = String.format("Account name=%s is not acceptable ", acctName);
            LOG.warn(msg);
            throw new AccountException(msg);
        }
        this.accountName = acctName;
    }

    /**
     * Gets the hashed password
     *
     * @return hashed password
     */
    @Override
    public byte[] getPasswordHash() {

        return passwordHash;
    }

    @Override
    public void setPasswordHash(byte[] passwordHash) {
        byte[] password = null;
        if (passwordHash != null) {
            password = new byte[passwordHash.length];
            System.arraycopy(passwordHash, 0, password, 0, passwordHash.length);
        }

        this.passwordHash = password;
    }

    @Override
    public int getBalance() {
        return balance;
    }

    @Override
    public void setBalance(int balance) {

        this.balance = balance;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public CreditCard getCreditCard() {
        return creditCard;
    }

    @Override
    public void setCreditCard(CreditCard card) {
        this.creditCard = card;
    }

    /**
     * sets the account manager for persisting and managing account. This may be
     * invoked exactly once on any given account and any subsequent invocations
     * should be ignored.
     *
     * @param m account manager.
     */
    @Override
    public void registerAccountManager(AccountManager m) {
        if (this.accountManager == null) {
            this.accountManager = m;
        } else {
            LOG.info("Account manager is already set");
        }
    }

    /**
     *
     * @param order
     * @param executionPrice the price at which the order was executed.
     */
    @Override
    public void reflectOrder(final Order order, int executionPrice) {
        try {
            balance = order.valueOfOrder(executionPrice);
            if (accountManager != null) {
                accountManager.persist(this);
            } else {
                LOG.error("Account manager not initilaized", new Exception());
            }
        } catch (final AccountException ex) {
            LOG.error("Failed to persist account " + ex);
        }

    }

}

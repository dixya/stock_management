/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.uw.dl.account.AccountImpl;
import edu.uw.dl.account.AddressImpl;
import edu.uw.dl.account.CreditCardImpl;
import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An AccountDao that persists the account information in a json file.
 *
 * @author dixya
 */
public class JsonAccountDao implements AccountDao {

    /* The name of the file holding the account data */
//    private static final String ACCOUNT_FILE = "%s.json";

    private final ObjectMapper mapper;
    /**
     * logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JsonAccountDao.class);
    /**
     * created new directory inside target folder
     */
    private final File accountsDir = new File("target", "accounts");

    /**
     * Constructor
     */
    public JsonAccountDao() {
        //map interfaces to implementation classes
        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(Account.class, AccountImpl.class);
        // module.addAbstractTypeMapping(Account.class,AccountImpl.class);
        module.addAbstractTypeMapping(Address.class, AddressImpl.class);
        module.addAbstractTypeMapping(CreditCard.class, CreditCardImpl.class);

        mapper = new ObjectMapper();
        mapper.registerModule(module);

    }

    /**
     * Reads the account with the specific account name.
     * @param accountName the name of the account.
     * @return account.
     */
    @Override
    public Account getAccount(String accountName) {
        Account account = null;
        FileInputStream in = null;
        final File accountDir = new File(accountsDir, accountName);
        if (accountDir.exists() && accountDir.isDirectory()) {
            try {
                final File inFile = new File(accountDir, accountName+".json");
                // Write it out
                account = mapper.readValue(inFile, Account.class);
            } catch (IOException e) {
                LOG.warn("unable to access or read account data" + account);
            }

        }
        return account;
    }

    /**
     * Sets the account
     * @param account the type of account.
     * @throws AccountException if any error occurs in account.
     */
    @Override
    public void setAccount(Account account) throws AccountException {
        try {
            final File accountDir = new File(accountsDir, account.getName());

            if (!accountDir.exists()) {
                final boolean success = accountDir.mkdirs();
                if (!success) {
                    throw new AccountException(String.format("Unable to create account directory,%s", accountsDir));
                }
            }
            File outFile = new File(accountDir, account.getName()+".json");
            if (outFile.exists()) {
                //delete
                boolean deleted = outFile.delete();
                if (!deleted) {
                    LOG.warn("Unable to delete account file" + accountsDir.getAbsolutePath());
                }
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, account);

        } catch (final IOException ex) {
            throw new AccountException("Unable to store account" + ex);
        } 
    }

    /**
     * Deletes the account.
     * @param accountName the name of the account.
     * @throws AccountException if any error occurs in account.
     */
    @Override
    public void deleteAccount(String accountName) throws AccountException {
        deleteFile(new File(accountsDir, accountName));

    }

    /**
     * Resets the account.
     * @throws AccountException if any error occurs in account.
     */
    @Override
    public void reset() throws AccountException {
        deleteFile(accountsDir);

    }

    /**
     * Closes the account if open.
     * @throws AccountException if any error occurs in account.
     */
    @Override
    public void close() throws AccountException {
    }

    /**
     * Deletes the file.
     * @param file the name of the file.
     */
    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                final File[] files = file.listFiles();
                for (File currFile : files) {
                    deleteFile(currFile);
                }
            }

            if (!file.delete()) {
                LOG.warn(String.format("File deletion failed, %s", file.getAbsolutePath()));
           }
        }
    }

}

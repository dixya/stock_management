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
    private static final String ACCOUNT_FILE = "Account.json";
    /* The name of the file holding the address data */

    private static final String ADDRESS_FILE = "Address.json";
    /* The name of the file holding the credit card data */

    private static final String CREDITCARD_FILE = "Creditcard.json";
    ObjectMapper mapper;
    /**
     * logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JsonAccountDao.class);
    /**
     * created new directory inside target folder
     */
    private final File accountsDir = new File("target", "accounts");

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

    @Override
    public Account getAccount(String accountName) {
        Account account = null;
        FileInputStream in = null;
        final File accountDir = new File(accountsDir, accountName);
        if (accountDir.exists() && accountDir.isDirectory()) {
            try {
                final File inFile = new File(accountDir, ACCOUNT_FILE);
                // Write it out
                account = mapper.readValue(inFile, Account.class);
            } catch (IOException e) {
                LOG.warn("unable to access or read account data" + account);
            }


        }
        return account;
    }

    @Override
    public void setAccount(Account account) throws AccountException {
        FileOutputStream out = null;
        try {
            final File accountDir = new File(accountsDir, account.getName());
            final Address address = account.getAddress();
            final CreditCard card = account.getCreditCard();
            //deleteFile(accountDir);
            if (!accountDir.exists()) {
                final boolean success = accountDir.mkdirs();
                if (!success) {
                    throw new AccountException(String.format("Unable to create account directory,%s", accountsDir));
                }
            }
            File outFile = new File(accountDir, ACCOUNT_FILE);
            if (outFile.exists()) {
                //delete
                boolean deleted = outFile.delete();
                if (!deleted) {
                    LOG.warn("Unable to delete account file" + accountsDir.getAbsolutePath());
                }
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, account);
            // mapper.writeValue(outFile, account);


        } catch (final IOException ex) {
            throw new AccountException("Unable to store account" + ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOG.warn("Attempt to close stream failed", e);
                }
            }
        }
    }

    @Override
    public void deleteAccount(String accountName) throws AccountException {
        deleteFile(new File(accountsDir, accountName));

    }

    @Override
    public void reset() throws AccountException {
        deleteFile(accountsDir);

    }

    @Override
    public void close() throws AccountException {
    }

    private void deleteFile(File file) {
        if (accountsDir.exists()) {
            if (accountsDir.isDirectory()) {
                final File[] files = accountsDir.listFiles();
                for (File currFile : files) {
                    deleteFile(currFile);
                }
            }
            if (!accountsDir.delete()) {
                LOG.warn(String.format("file deletion failed,%s", accountsDir.getAbsolutePath()));
            }
            // accountsDir.delete();

        }
    }

}

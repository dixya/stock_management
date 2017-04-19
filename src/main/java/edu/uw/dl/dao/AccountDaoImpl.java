/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.dao;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An AccountDao that persists the account information in a file.
 * @author dixya
 */
public class AccountDaoImpl implements AccountDao{
    /* The name of the file holding the account data */
    private static final String ACCOUNT_FILENAME="account.dat";
        /* The name of the file holding the address data */

    private static final String ADDRESS_FILENAME="address.properties";
        /* The name of the file holding the crdit card data */

    private static final String CREDITCARD_FILENAME="creditcard.txt";
    private static final Logger LOG=LoggerFactory.getLogger(AccountDaoImpl.class);
    private final File accountsDir=new File("target","accounts");
    /**
     * Creates an instance of AccountDao
     * @throws AccountException if any error occurs during load operation.
     */
    public AccountDaoImpl() throws AccountException{
        
    }

    /**
     * Search for the account in the Hashmap based on accountname
     * @param accountName the name of the account
     * @return the account if located otherwise null.
     */
    @Override
    public Account getAccount(String accountName) {
        Account account = null;
        FileInputStream in =null;
        final File accountDir=new File(accountsDir,accountName);
        if(accountDir.exists() && accountDir.isDirectory()){
            try{
                File inFile=new File(accountDir,ACCOUNT_FILENAME);
                in=new FileInputStream(inFile);
                account=AccountSer.read(in);
                in.close();
                inFile=new File(accountDir, ADDRESS_FILENAME);
                if(inFile.exists()){
                    in=new FileInputStream(inFile);
                    final Address address=AddressSer.read(in);
                    in.close();
                    account.setAddress(address);
                }
                inFile=new File(accountDir,CREDITCARD_FILENAME);
                if(inFile.exists()){
                    in=new FileInputStream(inFile);
                    final CreditCard creditCard=CreditCardSer.read(in);
                    in.close();
                    account.setCreditCard(creditCard);
                }
            }catch(final IOException ex){
                LOG.warn("Unable to access or read account data"+ex);
            } catch (AccountException ex) {
                java.util.logging.Logger.getLogger(AccountDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                if(in!=null){
                    try{
                        in.close();
                    }catch(IOException e){
                        LOG.warn("Attempt to close stream failed"+e);
                    }
                }
            }
        }
        return account;
    }

    /**
     * Sets the account.
     * @param account the account
     * @throws AccountException if any exception occurs.
     */
    @Override
    public void setAccount(Account account) throws AccountException {
        FileOutputStream out=null;
        try{
            final File accountDir=new File(accountsDir,account.getName());
            final Address address=account.getAddress();
            final CreditCard card=account.getCreditCard();
            deleteFile(accountDir);
            if(!accountDir.exists()){
                final boolean success=accountDir.mkdirs();
                if(!success){
                    throw new AccountException(String.format("Unable to create account directory,%s", accountsDir));
                }
            }
            File outFile=new File(accountDir, ACCOUNT_FILENAME);
            out=new FileOutputStream(outFile);
            AccountSer.write(out,account);
            out.close();
            
            if(address!=null){
                outFile=new File(accountDir,ADDRESS_FILENAME);
                            out=new FileOutputStream(outFile);
                            AddressSer.write(out,address);
                            out.close();

                
          
        }
            if(card!=null){
                outFile=new File(accountDir,CREDITCARD_FILENAME);
                                            out=new FileOutputStream(outFile);
                                            CreditCardSer.write(out,card);
                                            out.close();

            }
    }catch(final IOException ex){
        throw new AccountException("Unable to store account"+ex);
    }
        finally{
            if(out!=null){
                try{
                    out.close();
                }catch(IOException e){
                    LOG.warn("Attempt to close stream failed",e);
                }
            }
        }
    }

    /**
     * Deletes the account with specified name
     * @param accountName the name of the account
     * @throws AccountException if operation fails.
     */
    @Override
    public void deleteAccount(final String accountName) throws AccountException {
        deleteFile(new File(accountsDir,accountName));
    }

    /**
     * Removes all accounts
     * @throws AccountException if operation fails.
     */
    @Override
    public void reset() throws AccountException {
        deleteFile(accountsDir);
    }

    @Override
    public void close() throws AccountException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
/**
 * A method to delete a file or directory.
 * @param accountsDir the file or directory to delete.
 */
    private void deleteFile(File accountsDir) {
        if(accountsDir.exists()){
            if(accountsDir.isDirectory()){
                final File[] files=accountsDir.listFiles();
                for(File currFile:files){
                    deleteFile(currFile);
                }
            }
            if(!accountsDir.delete()){
                LOG.warn(String.format("file deletion failed,%s", accountsDir.getAbsolutePath()));
            }
           // accountsDir.delete();
            
        }
    }
    
}


package edu.uw.dl.account;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.dao.AccountDao;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;



/**
 *
 * @author dixya
 */
public class AccountManagerImpl implements AccountManager{
    private static final String ENCODING="ISO-8859-1";
    private AccountDao dao;
    private static final String ALGORITHM="SHA1";
    private AccountFactory accountFactory;
    static final Logger LOG=Logger.getLogger("AccountManagerImpl.class");

    /**
     * Creates an account manager using AccountDao for persistence. 
     * @param dao Dao to use for persistence.
     * @throws AccountException if any operation fails.
     */
    public AccountManagerImpl(final AccountDao dao) throws AccountException {
        this.dao=dao;
        try(ClassPathXmlApplicationContext appContext=new ClassPathXmlApplicationContext("context.xml")){
            accountFactory=appContext.getBean(AccountFactory.class);
        }catch(final BeansException ex){
            throw new AccountException("Unable to create account manager"+ex);
        }
        
    }
    /**
     * Saves the account.
     * @param account the account to persist
     * @throws AccountException if operation fails
     */
    @Override
    public void persist(Account account) throws AccountException {
      
        dao.setAccount(account);
    }

    /**
     * Lookup an account based on accountName
     * @param accountName the name of the account
     * @return account if present otherwise null
     * @throws AccountException if operation fails.
     */
    @Override
    public synchronized Account getAccount(String accountName) throws AccountException {
        final Account acct= dao.getAccount(accountName);
        if(acct!=null){
            acct.registerAccountManager(this);
            
        }
        return acct;
    }

    /**
     * Deletes an account with given accountname.
     * @param accountName the name of the account.
     * @throws AccountException if operation fails.
     */
    @Override
    public void deleteAccount(String accountName) throws AccountException {
       final Account acct= dao.getAccount(accountName);
        if(acct!=null){
            
            dao.deleteAccount(accountName);
        }
    }
    /**
     * Creates a new account.
     * @param accountName the name of the account
     * @param password the password
     * @param balance balance of the account.
     * @return new account.
     * @throws AccountException if any operation fails.
     */

    @Override
    public Account createAccount(String accountName, String password, int balance) throws AccountException {
        if(dao.getAccount(accountName)==null){
                    byte[] passwordHash = null;
            try {
                passwordHash = hashPassword(password);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                Logger.getLogger(AccountManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
                    final Account acct=accountFactory.newAccount(accountName, passwordHash, balance);
                    acct.registerAccountManager(this);
                    persist(acct);
                    return acct;

        }else{
            throw new AccountException("This account name is already used: "+ accountName);
        }
//        AccountImpl newAccount=new AccountImpl();
//        newAccount.setName(accountName);
//        passwordHash=getByteArray(password);
//        newAccount.setPasswordHash(passwordHash);
//        newAccount.setBalance(balance);
//        return newAccount;
    }

    /**
     * Validates the account name and password.
     * @param accountName the name of the account
     * @param password the password of the account.
     * @return boolean value.
     * @throws AccountException if error occurs accessing accounts.
     */
    @Override
    public boolean validateLogin(String accountName, String password) throws AccountException {
        boolean valid=false;
        final Account account=getAccount(accountName);
        byte[] passwordHash = null;

        if(account!=null){
            try {
                passwordHash = hashPassword(password);
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
                Logger.getLogger(AccountManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            valid=MessageDigest.isEqual(account.getPasswordHash(), passwordHash);
        }
        return valid;
    }

    /**
     * Closes the account manager.
     * @throws AccountException if any account operation fails.
     */
    @Override
    public void close() throws AccountException {
        if(dao!=null){
        dao.close();
        dao=null;
        }
        else{
            LOG.info("Account manager is already closed");
        }
    }

    /**
     * Creates the hash value of password.
     * @param password the password.
     * @return hashed value of password.
     * @throws AccountException if any operation fails.
     * @throws NoSuchAlgorithmException if algorithm is not found.
     * @throws UnsupportedEncodingException if the encoding is not supported.
     */
    private byte[] hashPassword(final String password) throws AccountException, NoSuchAlgorithmException, UnsupportedEncodingException {
        try{
            final MessageDigest md=MessageDigest.getInstance(ALGORITHM);
            md.update(password.getBytes(ENCODING));
            return md.digest();
        } catch(final NoSuchAlgorithmException e){
            throw new AccountException("Hash algorithm not found"+e);
        } catch(final UnsupportedEncodingException e){
            throw new AccountException("Unable to find character encoding"+e);
        }
    }
    
}

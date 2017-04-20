/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Exception to indicate that something went wrong with an Account operation. 
 * May indicate that there was a problem with a file or database connection.
 * @author dixya
 */
public class AccountException extends Exception{
    
    private static final long serialVersionUID = 1L;
        static final Logger LOG=LoggerFactory.getLogger("AccountException.class");

    /**
     * Constructor.

     * @param msg 
     */
    public AccountException(String msg){
        LOG.info("AccountException occured.. " );
    }
    
}

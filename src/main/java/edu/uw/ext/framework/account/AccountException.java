/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.account;

/**
 * Exception to indicate that something went wrong with an Account operation. 
 * May indicate that there was a problem with a file or database connection.
 * @author dixya
 */
public class AccountException extends Exception{
    
    private static final long serialVersionUID = 1L;
    /**
     * Constructor.

     * @param msg
     * @param cause 
     */
    public AccountException(String msg){
        System.out.println("AccountException occured: "+msg );
    }
    
}

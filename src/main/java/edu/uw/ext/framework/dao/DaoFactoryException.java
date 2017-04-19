/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.dao;

/**
 * Exception thrown when errors occur within the DaoFactory.
 * @author dixya
 */
public class DaoFactoryException extends Exception{
    
    private static final long serialVersionUID = 1L;
   public  DaoFactoryException(String msg){
       System.out.println("Error occurred :"+msg );
   }

    
}

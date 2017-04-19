/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.ext.framework.dao;

/**
 * An interface for factory that creates a family of DAO objects. There is currently only a single member of the DAO family, the Account DAO.
 * @author dixya
 */
public interface DaoFactory {
    /**
     * Instantiates a new AccountDao object.
     * @return a newly instantiated account DAO object.
     * @throws DaoFactoryException if unable to instantiate the DAO object.
     */
    AccountDao getAccountDao() throws DaoFactoryException;


    
}

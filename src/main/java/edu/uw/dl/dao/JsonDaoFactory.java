/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.dl.dao;

import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;

/**
 *
 * @author dixya
 */
public class JsonDaoFactory implements DaoFactory{
/**
 * Initializes JsonAccountDao.
 * @return an instance of AccountDao
 */
    @Override
    public AccountDao getAccountDao() {
        return new JsonAccountDao();
    
}
}

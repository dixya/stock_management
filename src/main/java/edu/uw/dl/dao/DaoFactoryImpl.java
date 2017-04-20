package edu.uw.dl.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;

/**
 * Implements the interface DaoFactory.
 *
 * @author dixya
 */
public final class DaoFactoryImpl implements DaoFactory {

    public DaoFactoryImpl() {

    }

    /**
     * Instantiates an instance of AccountDaoImpl
     *
     * @return new instance of AccountDaoImpl
     * @throws DaoFactoryException if instantiation fails.
     */
    @Override
    public AccountDao getAccountDao() throws DaoFactoryException {
        try {
            return new AccountDaoImpl();
        } catch (AccountException e) {
            throw new DaoFactoryException("Instantiation of AccountDaoImpl failed" + e);
        }
    }

}

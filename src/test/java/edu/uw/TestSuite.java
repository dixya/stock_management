package edu.uw;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.AccountTest;
import test.AccountManagerTest;
import test.DaoTest;
import test.BrokerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({AccountTest.class, AccountManagerTest.class, DaoTest.class, BrokerTest.class})
public class TestSuite{
}

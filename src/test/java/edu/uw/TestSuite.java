package edu.uw;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.AccountTest;
import test.AccountManagerTest;
import test.DaoTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({AccountTest.class, AccountManagerTest.class, DaoTest.class})
public class TestSuite{
}

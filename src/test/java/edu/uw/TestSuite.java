package edu.uw;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.AccountTest;
import test.AccountManagerTest;
import test.DaoTest;
import test.BrokerTest;
import test.ClientOrderCodecTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({AccountTest.class, AccountManagerTest.class, DaoTest.class, ClientOrderCodecTest.class})
public class TestSuite{
}

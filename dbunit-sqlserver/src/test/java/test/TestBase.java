package test;

import org.dbunit.IDatabaseTester;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.dbunit.DatabaseTesterFactory;

public class TestBase {
	private static final Logger logger = LoggerFactory.getLogger(DbBackupTestBase.class);

	/**
	 * DBテスター
	 */
	protected static IDatabaseTester dbTester;

	/**
	 * 前処理
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logger.info("TestBase @BeforeClass start");

		dbTester = DatabaseTesterFactory.create();

		logger.info("TestBase @BeforeClass end");
	}

}

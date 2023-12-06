package test;

import org.dbunit.IDatabaseTester;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.dbunit.BackupDbHelper;
import test.dbunit.DatabaseTesterFactory;
import test.dbunit.MyDbUnitExtension;

/**
 * 指定したテーブルのバックアップをサポートするテストケースを作成します。
 * 
 */
public abstract class DbBackupTestBase {
	
    private static final Logger logger = LoggerFactory.getLogger(DbBackupTestBase.class);

	/**
	 * DBテスター
	 */
	protected static IDatabaseTester dbTester;

	/**
	 * バックアップ・リストア サポート
	 */
	private static BackupDbHelper backupHelper;
	
	/**
	 * DbUnit拡張
	 */
	protected static MyDbUnitExtension myDbUnitExtension;

	/**
	 * 前処理
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logger.info("DbBackupTestBase @BeforeClass start");
		
		dbTester = DatabaseTesterFactory.create();
		backupHelper = new BackupDbHelper(dbTester, new String[]{"TestTable"});
		myDbUnitExtension = new MyDbUnitExtension(dbTester);
		
		backupHelper.doBackup();
		
		logger.info("DbBackupTestBase @BeforeClass end");
	}

	/**
	 * 後処理
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logger.info("DbBackupTestBase @AfterClass start");
		backupHelper.doRestore();
		
		logger.info("DbBackupTestBase @AfterClass end");
	}
}

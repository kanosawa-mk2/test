package test;

import java.io.File;

import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * テストサンプル
 */
public class TestSample extends DbBackupTestBase {
	
	private static final Logger logger = LoggerFactory.getLogger(TestSample.class);
	
	@Rule
	public TestName testName = new TestName(); // 実行しているメソッド名を取得する
	
	/**
	 * 前処理
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		logger.info("TestSample @BeforeClass start");
		DbBackupTestBase.setUpBeforeClass();
		IDataSet testData = new XlsDataSet(new File("./data/before/テストデータ.xlsx"));
	    dbTester.setDataSet(testData);
	    // DELETE→INSERTで事前準備データを用意する
	    dbTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
	    dbTester.onSetup();
	    logger.info("TestSample @BeforeClass end");
	}
	
	@Before
	public void before() throws Throwable {
		logger.info("テスト前処理：" + testName.getMethodName());
	}
	
//	@Before
//	public void before() throws Throwable {
//		System.out.println("前処理開始：" + testName.getMethodName());
//		IDataSet testData = new XlsDataSet(new File("./data/before/テストデータ.xlsx"));
//	    dbTester.setDataSet(testData);
//	    // DELETE→INSERTで事前準備データを用意する
//	    dbTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
//	    dbTester.onSetup();
//		System.out.println("前処理終了：");
//	}
	
	@Test
	public void test1() throws Exception {
		
		myDbUnitExtension.sql("delete TestTable where id = '1' ");

		// テーブル情報のログ出力
//		myDbUnitExtension.printTable("TestTable");
		
		// テーブル情報のファイル出力
		myDbUnitExtension.outputTableToXls(new String[] {"TestTable"}, new File("./data/backup/バックアップ.xls"));
		
		ITable sqlResultTb = dbTester.getConnection().createQueryTable("TestTable", "SELECT * FROM TestTable");
		
		IDataSet testData2 = new XlsDataSet(new File("./data/after/テストデータOK.xlsx"));
		Assertion.assertEquals(sqlResultTb, testData2.getTable("TestTable"));
	}
	

}

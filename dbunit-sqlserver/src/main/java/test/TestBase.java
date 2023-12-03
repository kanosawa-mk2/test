package test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Types;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

public abstract class TestBase {
	/** DBUnitのテスター */
	public static IDatabaseTester databaseTester;

	/** ログ設定ファイル */
	public static final String APP_CONF_XML = "/applog.xml";

	private static File backupFile;

	/** バックアップするテーブル名 **/
	public static String[] backupTable = new String[] { "MHI_PDF_CONV_OPERATION_LIST" };

	//	private static Charset propcharset = StandardCharsets.UTF_8;

	/** テスト結果のデータを保存するパス */
	private static String testOutPutPath = "/result";

	public static boolean isOutputTable = false;

	public static boolean isRoolback = true;

	/**
	 * SQL Serverの接続文字列
	 */
	private static final String connectionUrl = "jdbc:sqlserver://localhost:1433;"
			+ "database=TestDb;"
			+ "user=test;"
			+ "password=test;"
			+ "encrypt=true;"
			+ "trustServerCertificate=false;"
			+ "loginTimeout=30;";

	@Rule
	public TestName testName = new TestName(); // 実行しているメソッド名を取得する

	@BeforeClass
	public static void init() throws SQLException, Exception {
		databaseTester = new JdbcDatabaseTester("com.microsoft.sqlserver.jdbc.SQLServerDriver", connectionUrl);

		if (backupTable != null) {
			// テーブルのバックアップを取得し、添付ファイルに保存する
			IDataSet dataSet = databaseTester.getConnection().createDataSet(backupTable);
			backupFile = File.createTempFile("backup", "xml");
			FlatXmlDataSet.write(dataSet, new FileOutputStream(backupFile));
		}

	}

	@Before
	public void before() throws Throwable {
		System.out.println("前処理開始：" + testName.getMethodName());
		
		// --------------------------------------
	    // テストデータ投入
	    // --------------------------------------
	    IDataSet dataSet = new FlatXmlDataSetBuilder().build(new File("./data/Before.xml"));
	    databaseTester.setDataSet(dataSet);
	    // DELETE→INSERTで事前準備データを用意する
	    databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
	        databaseTester.onSetup();
		
		System.out.println("前処理終了：");
	}

	@After
	public void after() throws Throwable {
		System.out.println("後処理開始：" + testName.getMethodName());
		if (isOutputTable) {

			IDatabaseConnection iconn = databaseTester.getConnection();
			DatabaseConfig config = iconn.getConfig();
			config.setProperty(
					DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
					new OracleDataTypeFactory() {
						@Override
						public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
							if (sqlType == Types.DATE) {
								return DataType.VARCHAR;
							} else if (sqlType == Types.TIMESTAMP) {
								return DataType.VARCHAR;
							} else if (sqlType == Types.FLOAT) {
								return DataType.VARCHAR;
							} else {
								return super.createDataType(sqlType, sqlTypeName);
							}
						}
					});

			IDataSet dataSet = iconn.createDataSet(backupTable);
			File resultFile = Paths.get(testOutPutPath, testName.getMethodName() + ".xls").toFile();
			resultFile.createNewFile();
			XlsDataSet.write(dataSet, new FileOutputStream(resultFile));
		}
		System.out.println("後処理終了：" + testName.getMethodName());
	}

	/**
	 * [後処理]<br>
	 * テスト後の後処理を行う。<br>
	 * DBUnitの後片付けを行う。<br>
	 * @throws SQLException
	 * @throws Exception
	 */
	@AfterClass
	public static void end() throws SQLException, Exception {
		try {
			databaseTester.onTearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isRoolback && backupTable != null) {
			IDataSet dataSet = new FlatXmlDataSetBuilder().build(backupFile);
			DatabaseOperation.CLEAN_INSERT.execute(databaseTester.getConnection(), dataSet);
		}
	}

	public IDataSet getDataSetByXls(String filename) throws Exception {
		IDataSet dataSet = new XlsDataSet(this.getClass().getResourceAsStream(filename));
		return dataSet;
	}

	public void onSetupTestData(String filename) throws Exception {
		IDataSet dataSet = getDataSetByXls(filename);
		databaseTester.setDataSet(dataSet);
		databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
		databaseTester.onSetup();
	}
}

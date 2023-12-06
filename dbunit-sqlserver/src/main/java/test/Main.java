package test;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;

import org.dbunit.Assertion;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

/**
 * 独自のテストケースを作成する流れを確認
 * 参考 : https://t-ita.hatenadiary.org/entry/20121126/1353951558
 */
public class Main {

	/**
	 * SQL Serverの接続文字列
	 */
	private static final String connectionUrl = "jdbc:sqlserver://localhost:1433;"
			+ "database=TestDb;"
			+ "user=test;"
			+ "password=test;"
			+ "encrypt=false;"
			+ "trustServerCertificate=false;"
			+ "loginTimeout=30;";

	/** バックアップするテーブル名 **/
	public static String[] backupTable = new String[] { "TestTable" };
//	public static String[] backupTable = null;

	public static void main(String[] args) throws DataSetException, SQLException, Exception {

		IDatabaseTester databaseTester = new JdbcDatabaseTester("com.microsoft.sqlserver.jdbc.SQLServerDriver",
				connectionUrl) {
			
			@Override
			public IDatabaseConnection getConnection() throws Exception {
				IDatabaseConnection conn = super.getConnection();
                conn.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MsSqlDataTypeFactory());
                return conn;
			}
			
		};

		File backupFile = null;
		try {

			// テーブルのバックアップを取得し、添付ファイルに保存する
			// メモ：createDataSetで取得したIDataSetを復元時に渡してもうまく復元できなかった原因不明
			if (backupTable != null) {
				IDataSet restoreDataSet = databaseTester.getConnection().createDataSet(backupTable);
				backupFile = File.createTempFile("backup", ".xml");
				FlatXmlDataSet.write(restoreDataSet, new FileOutputStream(backupFile));
				System.out.println("テーブルデータのバックアップ：" + backupFile);
			}

			// --------------------------------------
			// テストデータ投入
			// --------------------------------------
			IDataSet testData = new XlsDataSet(new File("./data/before/テストデータ.xlsx"));
			databaseTester.setDataSet(testData);
			// DELETE→INSERTで事前準備データを用意する
			databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
			databaseTester.onSetup();
			databaseTester.onSetup();
			
			// --------------------------------------
			// SQLでデータを検索
			// --------------------------------------
			ITable sqlResultTb = databaseTester.getConnection().createQueryTable("TestTable", 
		            "SELECT * FROM TestTable");
			// --------------------------------------
			// 用意したExcelファイルと結果を比較し検証(テーブル単位の検証)
			// --------------------------------------
			IDataSet testData2 = new XlsDataSet(new File("./data/after/テストデータOK.xlsx"));
			Assertion.assertEquals(sqlResultTb, testData2.getTable("TestTable"));
			System.out.println("テーブル単位の検証OK");
			
			
			// --------------------------------------
			// 用意したExcelファイルと結果を比較し検証(データセット単位の検証)
			// --------------------------------------
			//IDataSet result = new DefaultDataSet(sqlResult);
			DefaultDataSet result = new DefaultDataSet();
			result.addTable(sqlResultTb);
			
			Assertion.assertEquals(result, testData2);
			
			System.out.println("データセット単位の検証OK");

		} finally {
			// テスト終了時に呼び出す DatabaseOperation を設定します。
			if (backupTable != null) {
				databaseTester.setTearDownOperation(DatabaseOperation.CLEAN_INSERT);
				IDataSet dataSet = new FlatXmlDataSetBuilder().build(backupFile);
				databaseTester.setDataSet(dataSet);
				System.out.println("バックアップからテーブルデータを復元");
			}else {
				databaseTester.setTearDownOperation(DatabaseOperation.NONE);
			}
			databaseTester.onTearDown();
		}

	}

}

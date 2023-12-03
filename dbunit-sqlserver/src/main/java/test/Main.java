package test;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

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

	public static void main(String[] args) throws DataSetException, SQLException, Exception {

		IDatabaseTester databaseTester = new JdbcDatabaseTester("com.microsoft.sqlserver.jdbc.SQLServerDriver",
				connectionUrl);

		File backupFile = null;
		try {

			// テーブルのバックアップを取得し、添付ファイルに保存する
			IDataSet dataSet = databaseTester.getConnection().createDataSet(backupTable);
			backupFile = File.createTempFile("backup", ".xml");
			System.out.println(backupFile);
			FlatXmlDataSet.write(dataSet, new FileOutputStream(backupFile));

			// --------------------------------------
			// テストデータ投入
			// --------------------------------------

			System.out.println(new File("./data/before/テストデータ.xlsx").exists());

			IDataSet testData = new XlsDataSet(new File("./data/before/テストデータ.xlsx"));
			databaseTester.setDataSet(testData);
			// DELETE→INSERTで事前準備データを用意する
			databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
			databaseTester.onSetup();

		} finally {
			// テスト終了時に呼び出す DatabaseOperation を設定します。
			databaseTester.setTearDownOperation(DatabaseOperation.NONE);
			databaseTester.onTearDown();
			
			// バックアップからテーブルを復元する
			if (backupTable != null) {
				IDataSet dataSet = new FlatXmlDataSetBuilder().build(backupFile);
				DatabaseOperation.CLEAN_INSERT.execute(databaseTester.getConnection(), dataSet);
			}
		}

	}

}

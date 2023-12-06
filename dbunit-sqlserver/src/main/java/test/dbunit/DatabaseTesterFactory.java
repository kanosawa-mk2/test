package test.dbunit;

import java.sql.Types;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;

/**
 * データベーステスターファクトリ
 */
public class DatabaseTesterFactory {

	/**
	 * SQL Serverの接続文字列
	 */
	private static final String CONNECTION_URL = "jdbc:sqlserver://localhost:1433;"
			+ "database=TestDb;"
			+ "user=test;"
			+ "password=test;"
			+ "encrypt=false;"
			+ "trustServerCertificate=false;"
			+ "loginTimeout=30;";

	/**
	 * データベーステスターの作成
	 * @return
	 */
	public static final IDatabaseTester create() {
		try {
			IDatabaseTester dbTester = new JdbcDatabaseTester("com.microsoft.sqlserver.jdbc.SQLServerDriver",
					CONNECTION_URL) {

				@Override
				public IDatabaseConnection getConnection() throws Exception {
					IDatabaseConnection conn = super.getConnection();
					conn.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MsSqlDataTypeFactory());
					return conn;
				}

			};

			return dbTester;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * データタイプ　データベースに併せて変更
	 * @return
	 */
	public static final IDataTypeFactory createDataType() {
		return new MsSqlDataTypeFactory();
	}
	
	/**
	 * XLS出力用のデータタイプ
	 * @return
	 */
	public static final IDataTypeFactory createXlsDataType() {
		return new MsSqlDataTypeFactory() {
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
		};
	}

}

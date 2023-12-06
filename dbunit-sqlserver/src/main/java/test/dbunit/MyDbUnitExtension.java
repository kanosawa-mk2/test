package test.dbunit;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.excel.XlsDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DbUnit拡張
 * 参考：https://qiita.com/opengl-8080/items/c92b6b687c9b5e277995
 */
public class MyDbUnitExtension {

	private static final Logger logger = LoggerFactory.getLogger(BackupDbHelper.class);

	/** データベーステスター */
	private IDatabaseTester databaseTester;
	/** コネクション */
	private IDatabaseConnection connection;

	/**
	 * 初期化
	 * @param databaseTester
	 */
	public MyDbUnitExtension(IDatabaseTester databaseTester) {
		this.databaseTester = databaseTester;

		try {
			this.connection = databaseTester.getConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * SQLを実行
	 * @param sql
	 */
	public void sql(String sql) {
		try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
			ps.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * テーブル情報を出力
	 * @param tableName
	 */
	public void printTable(String tableName) {
		try {
			logger.info(tableName + " {");
			ITable table = connection.createDataSet().getTable(tableName);
			ITableMetaData metaData = table.getTableMetaData();

			for (int row = 0; row < table.getRowCount(); row++) {
				List<String> values = new ArrayList<>();
				for (Column column : metaData.getColumns()) {
					Object value = table.getValue(row, column.getColumnName());
					values.add(column.getColumnName() + "=" + format(value));
				}
				logger.info("  " + String.join(", ", values));
			}
			logger.info("}");
		} catch (DataSetException | SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * オブジェクト情報を出力
	 * @param value 値
	 * @return
	 */
	private String format(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof String) {
			return "'" + value + "'";
		}
		return value.toString();
	}

	
	public void outputTableToXls(String[] tableNames,File xls) throws Exception {
		IDatabaseConnection iconn = connection;
		DatabaseConfig config = iconn.getConfig();
		
		Object bak = config.getProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY);
		
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,DatabaseTesterFactory.createXlsDataType());
		
		IDataSet dataSet = iconn.createDataSet(tableNames);
		xls.createNewFile();
		XlsDataSet.write(dataSet, new FileOutputStream(xls));
		
		// 念のためもとに戻す
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,bak);

	}
	

	/**
	 * データベーステスターを取得
	 * @return データベーステスター
	 */
	public IDatabaseTester getDatabaseTester() {
		return databaseTester;
	}

	/**
	 * データベースコネクションを取得
	 * @return コネクション
	 */
	public IDatabaseConnection getConnection() {
		return connection;
	}

}

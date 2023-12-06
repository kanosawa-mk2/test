package test.dbunit;

import java.io.File;
import java.io.FileOutputStream;

import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 指定のテーブルのバックアップとリストアを行う
 */
public class BackupDbHelper {
	
	  /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(BackupDbHelper.class);

	/**
	 * DBテスター
	 */
	private IDatabaseTester dbTester;

	/**
	 * バックアップするテーブル
	 */
	private String[] backupTable;

	/**
	 * バックアップファイル
	 */
	private File backupFile = null;

	public BackupDbHelper(IDatabaseTester dbTester, String[] backupTable) {
		this.dbTester = dbTester;
		this.backupTable = backupTable;
	}

	/**
	 * テーブルのバックアップを行う
	 */
	public void doBackup() {
		try {
			if (backupTable != null) {
				
				
				
				IDataSet restoreDataSet;
				restoreDataSet = dbTester.getConnection().createDataSet(backupTable);
				backupFile = File.createTempFile("backup", ".xml");
				FlatXmlDataSet.write(restoreDataSet, new FileOutputStream(backupFile));
				logger.info("テーブルデータのバックアップ：" + backupFile);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * テーブルのリストアを行う
	 */
	public void doRestore() {
		try {
			if (backupTable != null) {
				dbTester.setTearDownOperation(DatabaseOperation.CLEAN_INSERT);
				IDataSet dataSet = new FlatXmlDataSetBuilder().build(backupFile);
				dbTester.setDataSet(dataSet);
				dbTester.onTearDown();
				
				logger.info("バックアップからテーブルデータを復元");
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}

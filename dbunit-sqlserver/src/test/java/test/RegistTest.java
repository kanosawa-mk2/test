package test;

import java.io.File;

import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

/**
 * 登録実験
 */
public class RegistTest extends TestBase {
	
	
	@Test
	public void test1() throws Exception {
		
		IDataSet testData = new XlsDataSet(new File("./data/before/test_table.xlsx"));
		// 自動採番の列を除外
		ITable filteredTable = DefaultColumnFilter.excludedColumnsTable(testData.getTable("test_table"), new String[] {"ID"}); 
		DefaultDataSet result = new DefaultDataSet(filteredTable);
		
	    dbTester.setDataSet(result);
	    // DELETE→INSERTで事前準備データを用意する
	    dbTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
	    dbTester.onSetup();
	}
	

}

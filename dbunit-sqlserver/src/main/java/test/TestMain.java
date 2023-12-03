package test;

import java.io.File;

import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Test;

public class TestMain extends TestBase {

	@Test
	public void testSelectPk() throws Exception {

		// ----------------------------------
		// DBUnitで更新後データチェック
		// ----------------------------------
		IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("./data/After.xml"));
		ITable expectedTable = expectedDataSet.getTable("EMP");

		IDataSet databaseDataSet = databaseTester.getConnection().createDataSet();
		ITable actualTable = databaseDataSet.getTable("EMP");

		// 時間に対するAssertionはほぼ確実に失敗するので検証対象から除外する
		ITable filteredExpectedTable = DefaultColumnFilter.excludedColumnsTable(
				expectedTable, new String[] { "HIREDATE" });
		ITable filteredActualTable;
		filteredActualTable = DefaultColumnFilter.excludedColumnsTable(
				actualTable, new String[] { "HIREDATE" });

		// ---------------------------------------------------------------
		// 更新結果の検証はJUnitではなくDBUnitのAssertionを使用する
		// ---------------------------------------------------------------
		Assertion.assertEquals(filteredExpectedTable, filteredActualTable);
	}
}

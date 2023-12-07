# DBUnitについて

2023/12/5

CSV、XML、Excelなどで記述したテストデータをデータベースに登録、削除してくれるツール。  
他にもデータをエクスポートする機能や、テストケース実施後のデータが予想した値になっていることを検証する機能も提供されています。

# URL

* [DbUnitについて](https://www.dbunit.org/)

# 環境構築
mavenの場合は、mavenのdependencyに記載するだけ

# 使い方
以下の実装方法が存在する。
- DBTestCase サブクラスを使用したデータベースのセットアップ
- 独自の TestCase サブクラスを使用したデータベースのセットアップ


DBTestCaseはJunit4のTestCaseクラスを継承するため、Junit5では使用できない。  
今から使う場合は後者の実装を推奨。

参考  
* [はじめる](https://www.dbunit.org/howto.html)

# 独自の TestCase サブクラスを使用したデータベースのセットアップ

IDatabaseTesterを使用してデータベースのセットアップを行う。

<table border="1" style="border-collapse: collapse">
  <tr><td>JdbcDatabaseTester</td><td>DriverManager を使用して接続を作成します。</td></tr>
  <tr><td>PropertiesBasedJdbcDatabaseTester</td><td>DriverManager も使用しますが、構成はシステム プロパティから取得されます。<br>これは、DBTestCase で使用されるデフォルトの実装です</td></tr>
  <tr><td>DataSourceDatabaseTester</td><td>javax.sql.DataSource を使用して接続を作成します。</td></tr>
  <tr><td>JndiDatabaseTester</td><td>JNDI を介して配置された javax.sql.DataSource を使用します。</td></tr>
</table>

# 検証

DBUnitでは２つのデータセット(テーブル)の中身を比較することが可能。


	public void testMe() throws Exception 
	{ 
		// テストコード
	
		//コードの実行後にデータベース データを取得します
		IDataSet databaseDataSet = getConnection().createDataSet(); 
		ITableactualTable =databaseDataSet.getTable("TABLE_NAME"); 
		
		// XML データセットから期待されるデータを読み込みます
		IDataSet ExpectedDataSet = new FlatXmlDataSetBuilder().build(new File("expectedDataSet.xml")); 
		ITable ExpectedTable = ExpectDataSet.getTable("TABLE_NAME"); 
	
		// 実際のデータベース テーブルが期待されるテーブルと一致することをアサートします
		Assertion.assertEquals(expectedTable,actualTable); 
	}

# クエリを使用してデータベースのスナップショットを取得する

クエリの結果が予想されるデータのセットと一致するかどうかを検証することもできます。  
クエリを使用すると、テーブルのサブセットのみを選択したり、複数のテーブルを結合したりすることもできます。

~ 第一引数にはテーブル名を設定する
- この名前は、返却される ITable の getTableMetaData() で取得できる ITableMetaData の getTableName() が返す値に使用される
- 特に実際のテーブル名と異なっていてもエラーにはならないが、基本は実際のテーブル名に合わせるのがいい気がする
- join している場合とかは、識別しやすい名前にしとくのがいいのかもしれない

~ 第二引数には、実行するクエリを設定する

任意のクエリなので、 join 結果を受け取ることもできる

	 ITable actualJoinData = getConnection().createQueryTable("RESULT_NAME", 
	            "SELECT * FROM TABLE1, TABLE2 WHERE ...");


# 一部の列を無視して比較する

org.dbunit.dataset.filter.DefaultColumnFilterを使用する。
指定された列が除外されているテーブルを返します。
以下の例だとExpected.getTableMetaData().getColumns()で取得される列で比較を行っている。  
検証結果として比較したデータの列が少ない前提。

	ITable filteredTable = DefaultColumnFilter.includeColumnsTable(actual, 
	Expected.getTableMetaData().getColumns()); 
	Assertion.assertEquals(expected, filteredTable);

# 行の順序付け

デフォルトでは、DbUnit によって取得されたデータベース テーブルのスナップショットは主キーによって並べ替えられる。  
意図的に順番を指定したい場合はORDER BYを指定したり、SortedTableデコレータ クラスで順番を変更することができる。

# 相違点をアサートして収集する

デフォルトでは、dbunit は最初のデータの違いが見つかるとすぐに失敗します。  
dbunit 2.4 以降では、カスタムFailureHandlerを登録できるようになり 、ユーザーはスローされる例外の種類とデータ差異の発生の処理方法を指定できるようになりす。  DiffCollectingFailureHandlerを使用すると、 データの不一致によってスローされる例外を回避できるため、後でデータ比較のすべての結果を評価できます。

	IDataSet dataSet = getDataSet();
	DiffCollectingFailureHandler myHandler = new DiffCollectingFailureHandler();
	// カスタム ハンドラーを使用してアサーションを呼び出します
	assertion.assertEquals(dataSet.getTable("TEST_TABLE"),
	                    dataSet.getTable("TEST_TABLE_WITH_WRONG_VALUE"),
	                    myHandler);
	// 結果を評価し、必要に応じて失敗をスローします。
	List diffList = myHandler.getDiffList();
	Difference diff = (Difference)diffList.get(0);
	...


# コアコンポーネント

[コアコンポーネント](https://www.dbunit.org/components.html)

<table border="1" style="border-collapse: collapse">
  <tr><td>IDatabaseConnection</td><td>	データベースへの DbUnit 接続を表すインターフェイス。</td></tr>
  <tr><td>IDataSet</td><td>テーブルのコレクションを表すインターフェイス。</td></tr>
  <tr><td>DatabaseOperation</td><td>各テストの前後にデータベースで実行される操作を表す抽象クラス。</td></tr>
</table>

# QA


Q1. SQLServerでIDENTITYなど自動で連番のあるテーブルにINSERTするには？  
A1. テストデータでIDENTITYのついているテーブルを設定しない。  
    もしくは、以下のように自動連番の付与されている列を除外する。

	ITable filteredTable = DefaultColumnFilter.excludedColumnsTable(testData.getTable("test_table"), new String[] {"ID"}); 
	DefaultDataSet result = new DefaultDataSet(filteredTable);
	
Q2. 日付データの形式
A2. 以下の形式  

- DATE 型なら yyyy-[m]m-[d]d
- TIME 型なら hh:mm:ss
- TIMESTAMP 型なら yyyy-[m]m-[d]d hh:mm:ss[.f...]
- 以下の特殊な予約文字  
   [now] :現在時刻  
   [now{DIFF}{TIME}] :現在時刻からの相対日時

[DbUnit使い方メモ 日付型](https://qiita.com/opengl-8080/items/c92b6b687c9b5e277995#%E6%97%A5%E4%BB%98%E5%9E%8B)

Q3. BLOB 型の形式
A3. 以下の形式 

- [TEXT]value  
 value で指定した値を文字列として扱い、バイナリにエンコードした値を読み込む  
 [TEXT UTF-8]のようにして、エンコードするときの文字コードを指定できる  
 未指定の場合は、デフォルトで UTF-8 でエンコードされる  

- [BASE64]value  
 value で指定した値を、 Base64 でエンコードされた文字列として読み込む  

- [FILE]value  
 value で指定した値をファイルのパスとして扱い、ファイルの内容を読み込む  
  
- [URL]value  
 value で指定した値を URL として扱い、 URL から読み取った内容をバイナリとして読み込む  
 http://～ のような URL を指定すれば、インターネット経由でファイルを読み込むようなことも可能  


[DbUnit使い方メモ BLOB型](https://qiita.com/opengl-8080/items/c92b6b687c9b5e277995#blob-%E5%9E%8B)

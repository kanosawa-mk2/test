# Junit5について

2023/12/9

JUnit 5は 3つのサブプロジェクトに含まれる複数のモジュールで構成されます。  

JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage  

- JUnit Platform  
  JVM上で テストフレームワークを起動する ための基盤となり、このプラットフォーム上で動作するテストフレームワークを開発するための TestEngine APIを定義しています。
- JUnit Jupiter  
  JUnit 5でテストや拡張機能を書くための新しい プログラミングモデル と 拡張モデル の組み合わせです。
- JUnit Vintage  
  プラットフォーム上でJUnit 3またはJUnit 4 ベースのテストを実行するための TestEngine を提供します。

# URL

* [JUnit 5 ユーザーガイド](https://oohira.github.io/junit5-doc-jp/user-guide/#dependency-metadata)
* [JUnit 5 サンプル](https://github.com/junit-team/junit5-samples/tree/main)

# 環境構築
mavenの場合は、mavenのdependencyに記載するだけ  
基本的な機能だけならorg.junit.jupiterだけでよい。
junit-bomはライブラリを複数参照するときに依存関係の管理を容易にするツールなので、  
こちらも利用しておくとよい。

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.junit</groupId>
				<artifactId>junit-bom</artifactId>
				<version>5.10.1</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
	
	<dependencies>
		<dependency>
			<groupId>org.junit.jupiter</groupId>
			<artifactId>junit-jupiter</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>


# アノテーション

よく使用するものだけ


<table border="1" style="border-collapse: collapse">
  <tr><td>@Test</td><td>このメソッドが、テストメソッドであることを示します。。JUnit 4の @Test アノテーションとは異なり、このアノテーションはどんな属性も宣言しません。</td></tr>
  <tr><td>@DisplayName</td><td>テストクラスやテストメソッドにカスタムの表示名を指定します。このアノテーションは、継承 されません。</td></tr>
  <tr><td>@BeforeEach</td><td>JUnit 4の @Before と同じです。テストメソッド実行前に実行される。</td></tr>
  <tr><td>@AfterEach</td><td>JUnit 4の @After と同じです。テストメソッド実行後に実行される。</td></tr>
  <tr><td>@BeforeAll</td><td>JUnit 4の @BeforeClass と同じです。テスト開始前に1回だけ実行される。</td></tr>
  <tr><td>@AfterAll</td><td>JUnit 4の @AfterClass と同じです。テスト開始後に1回だけ実行される。</td></tr>
  <tr><td>@Disabled</td><td>テストクラスやテストメソッドを 無効化 するのに使われます。</td></tr>
</table>

# サードパーティーのアサーションライブラリ

AssertJ や Hamcrest、 Truth のようなサードパーティーの アサーションライブラリの使用も推奨している。

* [JUnitのアサーションライブラリHamcrest,AssertJ比較](https://qiita.com/disc99/items/31fa7abb724f63602dc9)

# Junit5の機能

- オペレーティングシステムを条件にしたテスト
- Javaランタイム環境を条件にしたテスト
- 環境変数を条件にしたテスト
- スクリプトベースの条件にしたテスト

	@Test // 静的なJavaScriptの式
	@EnabledIf("2 * 3 == 6")
	void willBeExecuted() {
	    // ...
	}

# 繰り返しテスト
@RepeatedTestを使用する。

# パラメーター化テスト
パラメーター化テストは、異なる実引数でテストを複数回実行できるようにします。 通常の @Test メソッドに似ていますが、@ParameterizedTest アノテーションを 使って宣言します。各呼び出しに対するパラメーターを生成するための ソース (source) を少なくとも1つは宣言し、そのパラメーターはテストメソッドで 消費 (consume) する必要があります。

	@ParameterizedTest
	@ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
	void palindromes(String candidate) {
    assertTrue(isPalindrome(candidate));
	}


<table border="1" style="border-collapse: collapse">
  <tr><td>@ValueSource</td><td>最もシンプルなソースの1つです。リテラル値の配列を指定できますが、 パラメーター化テストの1回の実行につき1つのパラメーターしか与えることができません。</td></tr>
  <tr><td>@EnumSource</td><td>Enum 定数を指定する便利な方法を提供します。<br>アノテーションは、テストメソッドに渡される列挙定数をさらに細かく制御するために、 省略可能な mode 属性も提供しています。</td></tr>
  <tr><td>@MethodSource</td><td>テストクラスまたは外部のクラスの ファクトリ メソッドによるパラメーターの指定を可能にします。</td></tr>
  <tr><td>@CsvSource</td><td>パラメーターのリストをカンマ区切りの値（String リテラル） で指定できるようにします。</td></tr>
  <tr><td>@CsvFileSource</td><td>クラスパスにあるCSVファイルを使えるようにします。 CSVファイルの各行は、パラメーター化テストの1回の実行になります。</td></tr>
  <tr><td>@ArgumentsSource</td><td>カスタムの再利用可能な ArgumentsProvider を指定する場合に使えます。</td></tr>
</table>


# テストスイート
複数のテストクラスがある場合、次の例のようにテストスイートを作成できます。

	import org.junit.platform.runner.JUnitPlatform;
	import org.junit.platform.suite.api.SelectPackages;
	import org.junit.platform.suite.api.SuiteDisplayName;
	import org.junit.runner.RunWith;
	
	@RunWith(JUnitPlatform.class)
	@SuiteDisplayName("JUnit 4 Suite Demo")
	@SelectPackages("example")
	public class JUnit4SuiteDemo {
	}

# 拡張モデル
 JUnit Jupiterの拡張モデルは、単一の一貫したコンセプトである Extension API で構成されます。  
 ただし、Extension 自身はただのマーカーインターフェースである点に 注意してください。

# 拡張機能の登録
拡張機能は、 @ExtendWith を使って 宣言的に 登録するか、 @RegisterExtension を使って 手続き的に 登録するか、あるいは Javaの ServiceLoader の仕組みを使って 自動的に 登録することができます。

# テストライフサイクルコールバック
以下のインターフェースは、テスト実行のライフサイクルにおける様々なタイミングでテストを拡張するための APIを定めています。詳細は、以降の節にあるサンプルと、org.junit.jupiter.api.extension パッケージにある これらのインターフェースのJavadocを参照してください。

- BeforeAllCallback
- BeforeEachCallback
- BeforeTestExecutionCallback
- AfterTestExecutionCallback
- AfterEachCallback
- AfterAllCallback
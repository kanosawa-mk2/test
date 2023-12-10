package test.junit5;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("標準テスト")
public class StandardTests {
	@BeforeAll
	static void initAll() {
	}

	@BeforeEach
	void init() {
	}

	@Test
	@DisplayName("成功するテスト")
	void succeedingTest() {
	}

	@Test
	void failingTest() {
		fail("失敗するテスト");
	}

	@Test
	@Disabled("デモ用")
	void skippedTest() {
		// 実行されない
	}

	@AfterEach
	void tearDown() {
	}

	@AfterAll
	static void tearDownAll() {
	}
}

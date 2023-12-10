package test.junit5;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ParameterizedTestDemo {
	@ParameterizedTest
	@ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
	void palindromes(String candidate) {
	    assertTrue(isPalindrome(candidate));
	}

	private BooleanSupplier isPalindrome(String candidate) {
		return () -> candidate.equals("racecar");
	}
	
	
	@ParameterizedTest
	@MethodSource("range")
	void testWithRangeMethodSource(int argument) {
	    assertNotEquals(9, argument);
	}

	static IntStream range() {
	    return IntStream.range(0, 20).skip(10);
	}
	
	
	@ParameterizedTest
	@CsvSource({ "foo, 1", "bar, 2", "'baz, qux', 3" })
	void testWithCsvSource(String first, int second) {
	    assertNotNull(first);
	    assertNotEquals(0, second);
	}
	
	
	@ParameterizedTest
    @CsvFileSource(files = "./data/testdata/test.csv")
    void CsvFileSourceTest(int id, String name){
        System.out.println("id: " + id +  " name: " + name + " test");
        assertEquals(id,1);
        assertEquals(name,"name");
    }
}

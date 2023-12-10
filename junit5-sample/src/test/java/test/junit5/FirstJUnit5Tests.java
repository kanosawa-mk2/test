package test.junit5;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FirstJUnit5Tests {
	
    @Test
    void myFirstTest() {
        assertEquals(2, 1 + 1);
    }

}

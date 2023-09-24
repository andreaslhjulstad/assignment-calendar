package calendar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CourseTest {

    private String invalidCode;
    private String validCode;

    @BeforeEach
    public void setup() {
        validCode = "TDT4100";
        invalidCode = "4100";
    }
    
    @Test
    @DisplayName("Test konstruktør med forskjellige argumenter")
    public void testConstructor() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Course(invalidCode);
        }, "Forventet IllegalArgumentException siden fagkode er ugyldig");
        
        Course course = new Course(validCode);
        assertEquals("TDT4100", course.getCode());
    }
    
}

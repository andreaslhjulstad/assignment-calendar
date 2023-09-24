package calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CalendarTest {

    private Calendar calendar;
    private Assignment assignment1;
    private Assignment assignment2;

    @BeforeEach
    public void setup() {
        calendar = new Calendar();
        assignment1 = new Assignment(new Course("TDT4100"), LocalDate.parse("2023-04-17"), "", "No notifications");
        assignment2 = new Assignment(new Course("TMA4115"), LocalDate.parse("2023-04-29"), "", "7 days before");
    }

    @Test
    @DisplayName("Test å legge til en innlevering")
    public void testAdd() {
        assertEquals(calendar.getAssignments(), Collections.emptyList());
        calendar.addAssignment(assignment1);
        assertTrue(calendar.containsAssignment(assignment1));
    }

    @Test
    @DisplayName("Test å fjerne en innlevering fra kalenderen i forskjellige tilstander")
    public void testRemove() {
        assertThrows(IllegalStateException.class, () -> {
            calendar.removeAssignment(0);
        }, "Forventet IllegalStateException når man prøver å fjerne et element når lista er tom");
        calendar.addAssignment(assignment1);
        assertThrows(IllegalStateException.class, () -> {
            calendar.removeAssignment(assignment2);
        }, "Forventet IllegalStateException når man prøver å fjerne et element som ikke er i lista");
        calendar.removeAssignment(assignment1);
        assertEquals(calendar.getAssignments(), Collections.emptyList());
    }

}

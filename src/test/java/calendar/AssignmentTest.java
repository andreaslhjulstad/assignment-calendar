package calendar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AssignmentTest {

    private Assignment assignment;
    private LocalDate invalidDueDate;
    private LocalDate validDueDate;
    private Course course;

    @BeforeEach
    public void setup() {
        invalidDueDate = LocalDate.parse("2021-04-17");
        validDueDate = LocalDate.parse("2023-04-17");
        course = new Course("TDT4100");
    }
    
    @Test
    @DisplayName("Test konstruktør med både gyldige og ugyldige verdier for innleveringsdato")
    public void testConstructor() {
        assertThrows(IllegalDateException.class, () -> {
            new Assignment(course, invalidDueDate, "", "No notifications");
        }, "Forventet IllegalDateException siden innleveringsdato er ugyldig");
        assertDoesNotThrow(() -> {
            new Assignment(course, validDueDate, "", "No notifications"); 
        }, "Dato i fremtiden, forventet ingen unntak");
    }

    @Test
    @DisplayName("Test at gjenstående dager blir kalkulert riktig")
    public void testRemainingDays() {
        assignment = new Assignment(course, validDueDate, "", "No notifications");
        assertEquals(ChronoUnit.DAYS.between(LocalDate.now(), validDueDate), assignment.getRemainingDays());
    }

    @Test
    @DisplayName("Test at varselinstillinger funker som de skal")
    public void testNotificationOptions() {
        Assignment assignment1 = new Assignment(course, validDueDate, "", "No notifications");
        Assignment assignment2 = new Assignment(course, validDueDate, "", "On due date");
        assertEquals("No", assignment1.getNotification());
        assertEquals("Yes", assignment2.getNotification());
    }

}

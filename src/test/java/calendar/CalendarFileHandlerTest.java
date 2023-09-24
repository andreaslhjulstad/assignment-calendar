package calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CalendarFileHandlerTest {

    private static final String testCalendarFileContent = """
            TMA4115,2023-04-29,Innlevering 7,1 day before
            TDT4100,2023-04-29,Prosjektinnlevering,7 days before
            TDT4180,2023-05-16,Innlevering av rapport,No notifications
            """;

    private static final String testInvalidDateCalendarFileContent = """
            TMA4115,2021-04-29,Innlevering 7,1 day before
            """;

    private static final String testInvalidFormatCalendarFileContent = """
            ,2023-04-28,Innlevering 7,1 day before
            TDT4100,2023-04-29,,On due date
            TDT4180,2023-05-16,Innlevering av rapport,
            """;

    private ICalendarFileHandler getCalendarHandler() {
        return new CalendarFileHandler();
    }

    private Calendar getFilledCalendarObject() {
        Calendar testCalendarObject = new Calendar();
        testCalendarObject.addAssignment(new Assignment(new Course("TMA4115"), LocalDate.parse("2023-04-29"), "Innlevering 7", "1 day before"));
        testCalendarObject.addAssignment(new Assignment(new Course("TDT4100"), LocalDate.parse("2023-04-29"), "Prosjektinnlevering", "7 days before"));
        testCalendarObject.addAssignment(new Assignment(new Course("TDT4180"), LocalDate.parse("2023-05-16"), "Innlevering av rapport", "No notifications")); // kan utvides
        return testCalendarObject;
    }

    @Test
    @DisplayName("Test å lese inn en gyldig kalender fra fil")
    public void testReadCalendar() throws IOException {
        File tempFile = File.createTempFile("temp_calendar", ".txt");
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), testCalendarFileContent.getBytes());

        Calendar expectedCalendarObject = getFilledCalendarObject();
        Calendar actualCalendarObject = getCalendarHandler().readFile(tempFile.toString(), new Calendar());

        Iterator<Assignment> expectedIterator = expectedCalendarObject.iterator();
        Iterator<Assignment> actualIterator = actualCalendarObject.iterator();

        while(expectedIterator.hasNext()) {
            try{
                Assignment expectedAssignment = expectedIterator.next();
                Assignment actualAssignment = actualIterator.next();
                assertEquals(expectedAssignment.toString(), actualAssignment.toString());
            }
            catch (NoSuchElementException e) {
                fail();
            }
        }
    }

    @Test
    @DisplayName("Test å lese inn en kalender med ugyldig dato fra fil")
    public void testReadInvalidDate() throws IOException {
        File tempFile = File.createTempFile("invalid_date_calendar", ".txt");
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), testInvalidDateCalendarFileContent.getBytes());

        assertThrows(IllegalDateException.class, ()  -> {
            getCalendarHandler().readFile(tempFile.toString(), new Calendar());
        }, "Forventet IllegalDateException pga. dato i fortiden.");
    }

    @Test
    @DisplayName("Test å lese inn en kalender på ugyldig format fra fil")
    public void testReadInvalidFormat() throws IllegalArgumentException, IOException {
        File tempFile = File.createTempFile("invalid_format_calendar", ".txt");
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), testInvalidFormatCalendarFileContent.getBytes());

        assertThrows(IllegalArgumentException.class, ()  -> {
            getCalendarHandler().readFile(tempFile.toString(), new Calendar());
        }, "Forventet IllegalArgumentException pga. fila er på feil format");
    }

    @Test
    @DisplayName("Test å lese inn en fil som ikke eksisterer")
    public void testReadNonExistingFile() {
        assertThrows(IOException.class, () -> {
            getCalendarHandler().readFile("non_existing_file", new Calendar());
        }, "Forventet IOException siden filen eksisterer ikke");
    }

    @Test
    @DisplayName("Test å skrive et kalender-objekt til fil")
    public void testWriteCalendar() throws IOException {
        File testFile = File.createTempFile("test_calendar", ".txt");
        testFile.deleteOnExit();
        Path path = testFile.toPath();
        Files.write(path, testCalendarFileContent.getBytes());
        File expectedFile = path.toFile();

        File tempFile = File.createTempFile("temp_calendar", ".txt");
        tempFile.deleteOnExit();
        getCalendarHandler().writeFile(tempFile.toString(), getFilledCalendarObject());

        try (Scanner expectedScanner = new Scanner(expectedFile); 
            Scanner actualScanner = new Scanner(tempFile)) {
            while (expectedScanner.hasNextLine()) {
                String expectedLine = expectedScanner.nextLine();
                String actualLine = actualScanner.nextLine();
                assertEquals(expectedLine, actualLine);
            }
        }
        catch (NoSuchElementException e) {
            fail();
        }
    }
    
}

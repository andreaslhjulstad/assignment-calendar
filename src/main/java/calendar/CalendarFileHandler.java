package calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

public class CalendarFileHandler implements ICalendarFileHandler {

    @Override
    public Calendar readFile(String filename, Calendar calendar) throws FileNotFoundException, IllegalArgumentException, IllegalDateException {
        try (Scanner scanner = new Scanner(getFile(filename))) {
            while (scanner.hasNextLine()) {
                String[] attributes = scanner.nextLine().split(",");
                Course course = new Course(attributes[0]);
                LocalDate dueDate = LocalDate.parse(attributes[1]);
                String description = attributes[2];
                String notificationOption = attributes[3];
                Assignment assignment = new Assignment(course, dueDate, description, notificationOption);
                calendar.addAssignment(assignment);
            }
        }
        return calendar;
    }

    @Override
    public void writeFile(String filename, Calendar calendar) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(getFile(filename))) {
            for (Assignment assignment : calendar) {
                writer.println(assignment.toString());
            }
        }
    }
    
    private static File getFile(String filename) {
        return new File(filename);
    }
    
}
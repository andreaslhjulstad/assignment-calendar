package calendar;

import java.io.FileNotFoundException;

public interface ICalendarFileHandler {
    
    Calendar readFile(String filename, Calendar calendar) throws FileNotFoundException;

    void writeFile(String filename, Calendar calendar) throws FileNotFoundException;

}
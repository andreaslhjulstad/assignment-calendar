package calendar;

import java.util.regex.Pattern;

public class Course {
    
    private String code;
    
    public Course(String code) {
        if (isValidCode(code)) this.code = code;
        else throw new IllegalArgumentException("Invalid course code!");
    }
    
    public String getCode() {
        return code;
    }

    private boolean isValidCode(String code) {
        return Pattern.matches("[A-Z]{3}[0-9]{4}", code);
    }

    @Override
    public String toString() {
        return code;
    }
    
}
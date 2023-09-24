package calendar;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Assignment {
    
    private Course course;
    private String dueDate;
    private String description;
    private long remainingDays;
    private String notification;
    private String notificationOption;
    private AssignmentNotification assignmentNotification;
    
    public Assignment(Course course, LocalDate dueDate, String description, String notificationOption) {
        if (isValidDate(dueDate)) {
            this.dueDate = dueDate.toString();
            remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        }
        else throw new IllegalDateException("Invalid date! Date must be in the future (or today)");
        this.course = course;
        this.description = description;
        this.notificationOption = notificationOption;
        if (notificationOption.equals("No notifications")) this.notification = "No";
        else {
            this.notification = "Yes";
            this.assignmentNotification = new AssignmentNotification(this);
            assignmentNotification.schedule();
        }   
    }

    public Course getCourse() {
        return course;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }

    public long getRemainingDays() {
        return remainingDays;
    }

    public String getNotification() { // returnerer "Yes" eller "No" for om varsler er skrudd på
        return notification;
    }

    public String getNotificationOption() {
        return notificationOption;
    }

    private boolean isValidDate(LocalDate date) {
        return ! date.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return course.toString() + "," + dueDate + "," + description + "," + notificationOption;
    }

}
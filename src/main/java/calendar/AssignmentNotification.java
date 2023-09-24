package calendar;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform; // litt dumt å ha javafx-elementer her, men er nødvendig dersom man skal få varsler når man importerer en eksisterende kalender

public class AssignmentNotification {
    
    private Assignment assignment;
    private long remainingMillis;
    private long daysBefore;
    private Timer timer = new Timer();
    
    public AssignmentNotification(Assignment assignment) {
        this.assignment = assignment;
        this.daysBefore = calculateNotificationDays(assignment.getNotificationOption());
        this.remainingMillis = (assignment.getRemainingDays() - daysBefore) * 86400000;// ganger dager med antall millisekunder i en dag, prøvde andre måter, men denne er den eneste som funker
    }

    public void schedule() throws IllegalNotificationOptionException {
        try {
            timer.schedule(new TimerTask() {
                @Override 
                public void run() { 
                    Platform.runLater(new Runnable() { // hentet fra https://stackoverflow.com/a/13792889/16687897
                        @Override 
                        public void run() { 
                            CalendarController.showNotification(assignment.getCourse(), daysBefore);
                        }
                    });
                }
            }, remainingMillis);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalNotificationOptionException("Invalid notification option!");
        }        
    }

    private int calculateNotificationDays(String notificationOption) {
        if (notificationOption.equals("On due date")) return 0;
        else return Character.getNumericValue(notificationOption.charAt(0));
    }

}
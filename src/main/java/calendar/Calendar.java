package calendar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Calendar implements Iterable<Assignment> {
    
    private List<Assignment> assignmentList = new ArrayList<>();

    public void addAssignment(Assignment assignment) {
        assignmentList.add(assignment);
    }

    public void removeAssignment(int index) {
        if (! (assignmentList.size() > 0)) {
            throw new IllegalStateException("Calendar is empty!");
        }
        assignmentList.remove(index);
    }

    public void removeAssignment(Assignment assignment) {
        if (! containsAssignment(assignment)) {
            throw new IllegalStateException("Assignment not in calendar!");
        }
        assignmentList.remove(assignment);
    }

    public Assignment getAssignment(int i) {
        return assignmentList.get(i);
    }

    public List<Assignment> getAssignments() {
        return new ArrayList<>(assignmentList);
    }

    public boolean containsAssignment(Assignment assignment) {
        return assignmentList.contains(assignment);
    }

    @Override
    public Iterator<Assignment> iterator() {
        return assignmentList.iterator();
    }

}
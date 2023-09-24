package calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class CalendarController implements Initializable{

    @FXML
    private Button addButton;

    @FXML
    private Button browseButton;

    @FXML
    private TableView<Assignment> calendarTable;

    @FXML
    private TableColumn<Assignment, String> date;

    @FXML
    private TableColumn<Assignment, Course> course;

    @FXML
    private TableColumn<Assignment, Long> remainingDays;

    @FXML
    private TableColumn<Assignment, String> notification;

    @FXML
    private TextField courseCodeInput;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button descriptionButton;

    @FXML
    private TextArea descriptionInput;

    @FXML
    private Button exportButton;

    @FXML
    private TextField fileLocationNameField;

    @FXML
    private Button importButton;

    @FXML
    private Button removeButton;

    @FXML
    private ChoiceBox<String> notificationChoiceBox;

    private Calendar calendar;

    private CalendarFileHandler cfh = new CalendarFileHandler();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        calendar = new Calendar();
        date.setCellValueFactory(new PropertyValueFactory<Assignment, String>("dueDate")); // hentet fra https://www.youtube.com/watch?v=fnU1AlyuguE&list=WL&index=25&t=190s
        course.setCellValueFactory(new PropertyValueFactory<Assignment, Course>("course"));
        remainingDays.setCellValueFactory(new PropertyValueFactory<Assignment, Long>("remainingDays"));
        notification.setCellValueFactory(new PropertyValueFactory<Assignment, String>("notification"));
        calendarTable.setPlaceholder(new Label("Table is empty, begin by adding an assigment"));
        
        String[] notificationOptions  = {"No notifications", "7 days before", "3 days before", "1 day before", "On due date"};
        notificationChoiceBox.getItems().addAll(notificationOptions);
        notificationChoiceBox.getSelectionModel().selectFirst();
        
        addButton.setDisable(true);
        importButton.setDisable(true);
        exportButton.setDisable(true);
        removeButton.disableProperty().bind(Bindings.isEmpty(calendarTable.getSelectionModel().getSelectedItems())); // hentet fra https://stackoverflow.com/a/28134126/16687897
        descriptionButton.disableProperty().bind(Bindings.isEmpty(calendarTable.getSelectionModel().getSelectedItems()));

        courseCodeInput.setTextFormatter(new TextFormatter<>((change) -> { // hentet fra https://stackoverflow.com/a/30885096/16687897
            change.setText(change.getText().toUpperCase());
            return change;
        }));
    }

    @FXML
    public void handleAdd(ActionEvent event) {
        LocalDate dueDate = datePicker.getValue();
        if (dueDate == null) {
            dueDate = datePicker.getConverter().fromString(datePicker.getEditor().getText()); // dersom brukeren skriver inn en dato i stedet for å velge en i datepickeren, hentet fra https://stackoverflow.com/a/67305569/16687897
        }
        try {
            Course course = new Course(courseCodeInput.getText());
            String description = descriptionInput.getText();
            try {
                String notificationOption = notificationChoiceBox.getValue();
                Assignment assignment = new Assignment(course, dueDate, description, notificationOption);
                calendar.addAssignment(assignment);
                updateGUI();
            }
            catch(IllegalDateException e) {
                showErrorMessage(e.getMessage());
            }
            catch (IllegalNotificationOptionException e) {
                showErrorMessage(e.getMessage());
            }
        }
        catch(IllegalArgumentException e) { // dersom feil courseCode
            showErrorMessage(e.getMessage());
        }
    }

    @FXML
    public void handleRemove(ActionEvent event) {
        int selectedIndex = calendarTable.getSelectionModel().getSelectedIndex();
        calendar.removeAssignment(selectedIndex);
        updateGUI();
    }

    @FXML
    public void handleBrowse(ActionEvent event) {
        importButton.setDisable(true);
        exportButton.setDisable(true);
        FileChooser fileChooser = new FileChooser();

        ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All formats", "*");
        ExtensionFilter allValidFilter = new FileChooser.ExtensionFilter("All valid formats", "*.txt" , "*.csv");
        ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"); 
        ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        Collection<ExtensionFilter> filters = Arrays.asList(allFilter, allValidFilter, txtFilter, csvFilter);
        fileChooser.getExtensionFilters().addAll(filters);

        Window window = fileLocationNameField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            String fileName = file.getName();
            int pos = fileName.lastIndexOf('.');
            if (fileName.substring(pos).equals(".txt") || fileName.substring(pos).equals(".csv")) {
                fileLocationNameField.setText(file.getParent() + File.separator + fileName);
                importButton.setDisable(false);
                exportButton.setDisable(false);
            }
            else showErrorMessage("Invalid file format, file must be .txt or .csv");
        }
    }

    @FXML
    public void handleExport(ActionEvent event) {
        try {
            cfh.writeFile(fileLocationNameField.getText(), calendar);
            showInfoMessage("Calendar exported successfully to " + fileLocationNameField.getText());
        }
        catch (FileNotFoundException e) {
            showErrorMessage("File not found!");
        }
        catch (NoSuchElementException e) {
            // do nothing
        }
    }

    @FXML
    public void handleImport(ActionEvent event) { 
        try {
            calendar = cfh.readFile(fileLocationNameField.getText(), new Calendar());
            showInfoMessage("Successfully imported calendar");
            updateGUI();
        }
        catch (FileNotFoundException e) {
            showErrorMessage("File not found!");
        }
        catch (IllegalArgumentException e) {
            showErrorMessage("Calendar was not imported successfully, it is in the wrong format!");
        }
        catch (IllegalDateException e) {
            showErrorMessage("Calendar was not imported successfully, at least one of the assignments has an invalid date!");
        }
        catch (NoSuchElementException e) {
            // do nothing
        }
    }

    @FXML
    public void handleShowDescription(ActionEvent event) {
        int selectedIndex = calendarTable.getSelectionModel().getSelectedIndex();
        Text text = new Text(calendar.getAssignment(selectedIndex).getDescription());
        if (! text.getText().isEmpty()) {
            Alert alert = new Alert(AlertType.NONE);
            alert.setTitle("Show description");
            alert.setContentText("Description:");
            alert.getButtonTypes().add(ButtonType.CLOSE);
            TextArea textArea  = new TextArea(text.getText());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            alert.getDialogPane().setExpandableContent(textArea);
            alert.getDialogPane().setExpanded(true);
            alert.showAndWait();
        }
        else {
            showInfoMessage("Assignment does not have a description");
        }
    }

    // kalles når noe skrives i input-feltene vha. javafx (står i fxml-fila)
    public void checkValidInput() { // hentet fra https://www.youtube.com/watch?v=oo8G4QfgPSI
        String courseCode = courseCodeInput.getText();
        LocalDate date = datePicker.getValue();
        String dateInput = datePicker.getEditor().getText();
        boolean addDisabled = (courseCode.isEmpty() || courseCode.trim().isEmpty()) || ((date == null && dateInput == null));
        addButton.setDisable(addDisabled);
    }

    static void showNotification(Course course, long days) { // static fordi den brukes i AssignmentNotification.java
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle("Notification");
        ImageView imageView = new ImageView(CalendarController.class.getResource("notification.png").toString());
        imageView.setFitHeight(64);
        imageView.setFitWidth(64);
        alert.setGraphic(imageView);

        String endText = null;
        if (days == 7)  endText = "in a week.";
        else if (days == 3) endText = "in three days";
        else if (days == 1) endText = "tomorrow!";
        else endText = "today!";

        alert.setContentText("Your assignment for " + course + " is due " + endText); 
        alert.getButtonTypes().add(ButtonType.OK);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateGUI() {
        ObservableList<Assignment> calendarObs = FXCollections.observableArrayList(calendar.getAssignments());
        calendarTable.setItems(calendarObs);
    }

}
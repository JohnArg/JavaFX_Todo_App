package john.tutorial_projects.javafx.ui_config;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import john.tutorial_projects.javafx.application.dataModel.TodoData;
import john.tutorial_projects.javafx.application.dataModel.TodoItem;

public class DialogController {
	@FXML
	private TextField shortDescriptionInput;
	@FXML
	private TextArea detailsInput;
	@FXML
	private DatePicker dateInput;
	
	public TodoItem processResults() {
		String shortDescStr  = shortDescriptionInput.getText().trim();
		String detailsStr = detailsInput.getText().trim();
		LocalDate date = dateInput.getValue();
		TodoItem newItem = new TodoItem(shortDescStr, detailsStr, date);
		TodoData.getInstance().addTodoItem(newItem);
		return newItem;
	}
}

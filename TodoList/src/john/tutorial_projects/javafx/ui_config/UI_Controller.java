package john.tutorial_projects.javafx.ui_config;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import john.tutorial_projects.javafx.application.dataModel.TodoData;
import john.tutorial_projects.javafx.application.dataModel.TodoItem;

public class UI_Controller {
	private List<TodoItem> todoItems;
	@FXML
	private ListView todoListView;
	@FXML
	private TextArea descriptionArea;
	@FXML
	private Label dueLabel;
	
	public void initialize() {
		TodoItem item1 = new TodoItem("Email", "Email the documents to Jane", LocalDate.of(2018, Month.JULY, 6));
		TodoItem item2 = new TodoItem("Appointment", "See doctor smith, 12 Stein Street", LocalDate.of(2018, Month.JUNE, 3));
		TodoItem item3 = new TodoItem("Garbage", "Throw the garbage", LocalDate.of(2018, Month.AUGUST, 16));
		TodoItem item4 = new TodoItem("Pickup Jane", "Pickup Jane from the train station", LocalDate.of(2018, Month.FEBRUARY, 12));
		TodoItem item5 = new TodoItem("Gym", "Go to the gym", LocalDate.of(2018, Month.JUNE, 19));
		
		todoItems = new ArrayList<TodoItem>();
		todoItems.add(item1);
		todoItems.add(item2);
		todoItems.add(item3);
		todoItems.add(item4);
		todoItems.add(item5);
		
		TodoData.getInstance().setTodoItems(todoItems);
		
		todoListView.getItems().setAll(todoItems);
		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	@FXML
	public void onItemSelected() {
		TodoItem task = (TodoItem) todoListView.getSelectionModel().getSelectedItem();
		descriptionArea.setText(task.getDetails());
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
		dueLabel.setText(df.format(task.getDeadline()));
	}
	
}

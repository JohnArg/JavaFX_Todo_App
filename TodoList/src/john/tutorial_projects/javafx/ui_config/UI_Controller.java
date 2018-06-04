package john.tutorial_projects.javafx.ui_config;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import john.tutorial_projects.javafx.application.dataModel.TodoData;
import john.tutorial_projects.javafx.application.dataModel.TodoItem;

public class UI_Controller {
	@FXML
	private ListView todoListView;
	@FXML
	private TextArea descriptionArea;
	@FXML
	private Label dueLabel;
	@FXML
	private BorderPane mainPane;
	
	public void initialize() {
		todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	}
	
	@FXML
	public void showNewItemDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainPane.getScene().getWindow());
		dialog.setTitle("New Todo");
		dialog.setHeaderText("Create a new todo here");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("views/todoItemDialog.fxml"));
		try {
			dialog.getDialogPane().setContent(loader.load());
		}catch(IOException e) {
			System.out.println("Cannot open dialog");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
		    DialogController dgController = loader.getController();
		    TodoItem newItem = dgController.processResults();
		    todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
		    todoListView.getSelectionModel().select(newItem);
		    onItemSelected();
		}else {
			
		}
	}
	
	@FXML
	public void onItemSelected() {
		TodoItem task = (TodoItem) todoListView.getSelectionModel().getSelectedItem();
		descriptionArea.setText(task.getDetails());
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
		dueLabel.setText(df.format(task.getDeadline()));
	}
	
}

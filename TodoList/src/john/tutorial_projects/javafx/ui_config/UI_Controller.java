package john.tutorial_projects.javafx.ui_config;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
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
	@FXML
	private ContextMenu listContextMenu;
	@FXML
	private Button newBtn;
	
	@SuppressWarnings("unchecked")
	public void initialize() {
		newBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("icons/new_item24.png"))));
		
		listContextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				TodoItem item = (TodoItem) todoListView.getSelectionModel().getSelectedItem();
				deleteItem(item);
			}
		});
		listContextMenu.getItems().addAll(deleteMenuItem);
		
		todoListView.setItems(TodoData.getInstance().getTodoItems());
		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		todoListView.setCellFactory(new Callback< ListView<TodoItem>, ListCell<TodoItem> >() {
			@Override
			public ListCell<TodoItem> call(ListView<TodoItem> param) {
				ListCell<TodoItem> cell = new ListCell<TodoItem>() {
					@Override
					protected void updateItem(TodoItem item, boolean empty) {
						super.updateItem(item, empty);
						if(empty) {
							setText(null);
						}else {
							setText(item.getShortDescription());
							if(item.getDeadline().isBefore(LocalDate.now())) {
								setTextFill(Color.RED);
							}else if(item.getDeadline().equals(LocalDate.now())){
								setTextFill(Color.BLUE);
							}else if(item.getDeadline().equals(LocalDate.now().plusDays(1))) {
								setTextFill(Color.ORANGE);
							}
						}
					}
				};
				
				cell.emptyProperty().addListener(
						(obs, wasEmpty, isNowEmpty) -> {
							if(isNowEmpty) {
								cell.setContextMenu(null);
							}else {
								cell.setContextMenu(listContextMenu);
							}
						}
				);
				
				return cell;
			};
		});
	}
	
	@FXML
	public void showNewItemDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.initOwner(mainPane.getScene().getWindow());
		dialog.setTitle("New Todo");
		dialog.setHeaderText("Create a new Todo");
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
		    TodoItem newItem = dgController.processResults(); //adds it to the list and returns reference
		    todoListView.getSelectionModel().select(newItem);
		    onItemSelected();
		}
	}
	
	@FXML
	public void onItemSelected() {
		TodoItem task = (TodoItem) todoListView.getSelectionModel().getSelectedItem();
		descriptionArea.setText(task.getDetails());
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
		dueLabel.setText(df.format(task.getDeadline()));
	}
	
	public void deleteItem(TodoItem item) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Delete Todo Item");
		alert.setHeaderText("This will delete the selected item : "+item.getShortDescription());
		alert.setContentText("Are you sure?");
		Optional<ButtonType> result = alert.showAndWait();
		if(result.isPresent() && (result.get() == ButtonType.OK)) {
			TodoData.getInstance().deleteTodoItem(item);
		}
	}
	
	public void handleKeyPressed(KeyEvent keyEvent) {
		TodoItem selectedItem = (TodoItem) todoListView.getSelectionModel().getSelectedItem();
		if(selectedItem != null) {
			if(keyEvent.getCode().equals(KeyCode.DELETE)) {
				deleteItem(selectedItem);
			}
		}
	}
	
}

package john.tutorial_projects.javafx.ui_config;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.ToggleButton;
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
	@FXML
	private ToggleButton filterToggleBtn;
	private FilteredList<TodoItem> filteredList;
	private Predicate<TodoItem> allItems;
	private Predicate<TodoItem> todayItems;
	
	@SuppressWarnings("unchecked")
	public void initialize() {
		//Add icon to Button manually
		newBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("icons/new_item24.png"))));
		
		//Use context menu to display a submenu with the Delete option when right clicking an item
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
		
		//Create a filtered list with today's items
		//Initialize the predicates for the list
		allItems = new Predicate<TodoItem>() {
			@Override
			public boolean test(TodoItem todoItem) {
				return true;
			}
		};
		todayItems = new Predicate<TodoItem>() {
			@Override
			public boolean test(TodoItem todoItem) {
				return (todoItem.getDeadline().equals(LocalDate.now()));
			}
		};
		filteredList = new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(), allItems);
		
		//Make the todos list sorted by date
		SortedList<TodoItem> sortedList = new SortedList<TodoItem>(filteredList, 
				new Comparator<TodoItem>() {
						@Override
						public int compare(TodoItem o1, TodoItem o2) {
							return o1.getDeadline().compareTo(o2.getDeadline());
						}
				}
		);
		
		//Populate Todo ListView and assign a cell to each Todo
		//todoListView.setItems(TodoData.getInstance().getTodoItems());
		todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {

			@Override
			public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
				if(newValue != null) {
					TodoItem item = (TodoItem) todoListView.getSelectionModel().getSelectedItem();
					descriptionArea.setText(item.getDetails());
					DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
					dueLabel.setText(df.format(item.getDeadline()));
				}
			}
			
		});
		todoListView.setItems(sortedList);
		todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		todoListView.getSelectionModel().selectFirst();
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
				
				//Bind the context menu to a non-null cell
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
		}
	}

	@FXML
	public void exitApp() {
		Platform.exit();
	}
	
	@FXML
	public void handleKeyPressed(KeyEvent keyEvent) {
		TodoItem selectedItem = (TodoItem) todoListView.getSelectionModel().getSelectedItem();
		if(selectedItem != null) {
			if(keyEvent.getCode().equals(KeyCode.DELETE)) {
				deleteItem(selectedItem);
			}
		}
	}
	
	@FXML
	public void hanldeFilterBtn() {
		TodoItem item = (TodoItem) todoListView.getSelectionModel().getSelectedItem();
		if(filterToggleBtn.isSelected()) {
			filteredList.setPredicate(todayItems);
			if(filteredList.isEmpty()) {
				descriptionArea.clear();
				dueLabel.setText("");
			}else if(filteredList.contains(item)) {
				todoListView.getSelectionModel().select(item);
			}else {
				todoListView.getSelectionModel().selectFirst();
			}
		}else {
			//show all
			filteredList.setPredicate(allItems);
			todoListView.getSelectionModel().select(item);
		}
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
	
}

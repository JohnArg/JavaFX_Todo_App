package john.tutorial_projects.javafx.application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import john.tutorial_projects.javafx.application.dataModel.TodoData;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../ui_config/views/launch.fxml"));
			
			Scene scene = new Scene(root,700,500);
			scene.getStylesheets().add(getClass().getResource("../ui_config/styles/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception{
		try {
			TodoData.getInstance().storeTodoItems();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

package org.crysil.instance.jce_receiver_demo;

import org.crysil.instance.jce_receiver_demo.model.Data;
import org.crysil.instance.jce_receiver_demo.wizard.SelectFile;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Crysil Demo CMS Writer/Reader");


		primaryStage.setScene(new Scene(new SelectFile(new Data()), 300, 200));
		primaryStage.getScene().getStylesheets().add(Main.class.getResource("main.css").toExternalForm());
		primaryStage.show();
	}
}

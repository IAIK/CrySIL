package org.crysil.instance.jce_receiver_demo;

import java.security.Security;

import org.crysil.communications.http.HttpJsonTransmitter;
import org.crysil.instance.jce_receiver_demo.model.Data;
import org.crysil.instance.jce_receiver_demo.wizard.SelectFile;
import org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// load anything we need
		CrysilProvider crysilProvider = CrysilProvider.getInstance0();
		crysilProvider.attach(new HttpJsonTransmitter());
		Security.addProvider(crysilProvider);

		// prepare data block
		Data data = new Data();
		data.setProvider(crysilProvider);

		// setup and start gui
		primaryStage.setTitle("Crysil Demo CMS Writer/Reader");
		primaryStage.setScene(new Scene(new SelectFile(data), 300, 200));
		primaryStage.getScene().getStylesheets().add(Main.class.getResource("main.css").toExternalForm());
		primaryStage.show();
	}
}

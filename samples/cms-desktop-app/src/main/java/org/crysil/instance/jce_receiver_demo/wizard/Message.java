package org.crysil.instance.jce_receiver_demo.wizard;

import org.crysil.instance.jce_receiver_demo.model.Data;
import org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Message extends Step {

	public Message(String title, String message) {
		setTitle(title);
		
		Label messageLabel = new Label(message);
		
		HBox hbox = new HBox();
		hbox.getChildren().add(messageLabel);

		setContent(hbox);
		
		Button procedeButton = new Button("start over");
		procedeButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				Data data = new Data();
				data.setProvider(CrysilProvider.getInstance0());
				procedeTo(new SelectFile(data));
			}
		});

		Button quitButton = new Button("quit");
		quitButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				Platform.exit();
			}
		});

		setButtons(procedeButton, quitButton);
	}
}

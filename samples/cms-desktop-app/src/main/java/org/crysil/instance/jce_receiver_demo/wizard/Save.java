package org.crysil.instance.jce_receiver_demo.wizard;

import java.io.FileOutputStream;

import org.crysil.instance.jce_receiver_demo.model.Data;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class Save extends Step {

	private TextField pathText;
	private Data data;

	public Save(Data data) {
		setTitle("Select location to store the result.");

		this.data = data;

		pathText = new TextField();
		
		if(data.getSourceFile().getName().endsWith(".cms"))
			pathText.setText(data.getSourceFile().getAbsolutePath().replace(".cms", ""));
		else
			pathText.setText(data.getSourceFile().getAbsolutePath() + ".cms");
		
		Button browseButton = new Button("Browse...");
		browseButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// select file
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("select a container format or an arbitrary file");
				pathText.setText(fileChooser.showOpenDialog(getScene().getWindow()).getAbsolutePath());
			}
		});
		HBox fileSelectorGroup = new HBox();
		fileSelectorGroup.getChildren().addAll(pathText, browseButton);
		setContent(fileSelectorGroup);

		Button proceedButton = new Button("proceed");
		proceedButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					perform();
				} catch (Exception e) {
					e.printStackTrace();
					procedeTo(new Message("An error has occured.", e.getMessage()));
				}
			}
		});
		setButtons(proceedButton);
	}
	
	private void perform() throws Exception {

		FileOutputStream fos = new FileOutputStream(pathText.getText());
		fos.write(data.getResult());
		fos.close();

		procedeTo(new Message("Finished", "There is nothing left to be done. Start over?"));
	}
}

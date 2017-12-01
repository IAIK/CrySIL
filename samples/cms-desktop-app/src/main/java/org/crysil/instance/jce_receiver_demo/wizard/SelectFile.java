package org.crysil.instance.jce_receiver_demo.wizard;

import java.io.File;
import java.io.FileInputStream;

import org.crysil.instance.jce_receiver_demo.model.Data;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

public class SelectFile extends Step {

	private TextField pathText;
	private Data data;

	public SelectFile(Data data) {
		setTitle("Select a file you want to encrypt/decrypt");

		this.data = data;

		pathText = new TextField();
		HBox.setHgrow(pathText, Priority.ALWAYS);
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
		HBox.setHgrow(fileSelectorGroup, Priority.ALWAYS);
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
		File file = new File(pathText.getText());

		// we got a file
		// - check for overflow
		if (Integer.MAX_VALUE < file.length())
			throw new Exception("file size limit exceeded");

		FileInputStream inputStream = new FileInputStream(file);
		byte[] bytes = new byte[(int) file.length()];

		// read bytes
		int numberOfBytesRead = inputStream.read(bytes);
		inputStream.close();

		// check if anything was read
		if (bytes.length > numberOfBytesRead)
			throw new Exception("could not read all bytes. giving up...");

		data.setFileContent(bytes);
		data.setSourceFile(file);

		procedeTo(new SelectService(data));
	}
}

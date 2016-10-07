package org.crysil.instance.jce_receiver_demo.wizard;

import java.security.KeyStore;
import java.util.ArrayList;

import org.crysil.instance.jce_receiver_demo.model.Data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

public class ChooseKey extends Step {

	private ListView<String> listView;

	public ChooseKey(final Data data, final KeyStore keystore, ArrayList<String> availableKeys) {
		setTitle("Select the key to use below");

		// - display a select box to the user
		listView = new ListView<String>();
		ObservableList<String> items = FXCollections.observableArrayList(availableKeys);
		listView.setItems(items);
		listView.getSelectionModel().select(0);

		HBox hbox = new HBox();
		hbox.getChildren().add(listView);
		setContent(hbox);
		
		Button proceedButton = new Button("proceed");
		proceedButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					data.setKey(keystore.getKey(listView.getSelectionModel().getSelectedItem(), new char[] {}));
					procedeTo(new EncryptDecrypt(data));
				} catch (Exception e) {
					e.printStackTrace();
					procedeTo(new Message("An error has occured.", e.getMessage()));
				}
			}
		});
		
		setButtons(proceedButton);
	}
}

package org.crysil.instance.jce_receiver_demo.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.crysil.instance.jce_receiver_demo.Main;
import org.crysil.instance.jce_receiver_demo.model.Data;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SelectService extends Step {

    private final ListView<String> recentList;
    private final TextField serviceText;

    private static final Preferences preferences = Preferences.userNodeForPackage(Main.class);
    private static final String recent = "recent";
    private static final String recentCount = "recentCount";
    private static final String last = "last";

    public SelectService(final Data data) {
        setTitle("Select the remote service.");

        // prepare the list of past service

        // - display a text box for new services
        Label serviceLabel = new Label("Service:");
        serviceText = new TextField();
		serviceText.setMinHeight(25);
        Label recentLabel = new Label("Recent:");
        recentList = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList(getRecent());
        recentList.setItems(items);
        recentList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                serviceText.setText(recentList.getSelectionModel().getSelectedItem());
            }
        });
        recentList.getSelectionModel().select(0);

		GridPane gridPane = new GridPane();
		
		gridPane.add(serviceLabel, 0, 0);
		gridPane.add(serviceText, 1, 0);
		gridPane.add(recentLabel, 0, 1);
		gridPane.add(recentList, 1, 1);

		gridPane.setVgap(3d);
		GridPane.setValignment(recentLabel, VPos.TOP);

		ColumnConstraints columnConstraints = new ColumnConstraints();
		columnConstraints.setFillWidth(true);
		// set to calculated size. done by default. so just add the constraint
		// as is.
		gridPane.getColumnConstraints().add(columnConstraints);

		ColumnConstraints columnConstraints1 = new ColumnConstraints();
		columnConstraints1.setFillWidth(true);
		columnConstraints1.setHgrow(Priority.ALWAYS);
		gridPane.getColumnConstraints().add(columnConstraints1);

		HBox.setHgrow(gridPane, Priority.ALWAYS);

		setContent(gridPane);

        Button proceedButton = new Button("proceed");
        proceedButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                try {
                    setLast(serviceText.getText());
                    setRecent(serviceText.getText());
                    procedeTo(new FetchKeys(data));
                } catch (Exception e) {
                    e.printStackTrace();
                    procedeTo(new Message("An error has occured.", e.getMessage()));
                }
            }
        });

        setButtons(proceedButton);
	}

    public static void setRecent(String path) {
        if (getRecent().contains(path)) {
            // exchange
            preferences.put(recent + (getRecent().indexOf(path) + 1), preferences.get(recent + "1", ""));
            preferences.put(recent + "1", path);
        } else {
            // add new
            for (int i = Integer.valueOf(preferences.get(recentCount, "5")); i > 1; i--)
                preferences.put(recent + i, preferences.get(recent + (i - 1), ""));

            preferences.put(recent + "1", path);
        }
    }

    public static void setLast(String path) {
        preferences.put(last, path);
    }

    public static String getLast() {
        return preferences.get(last, "");
    }

    public static List<String> getRecent() {
        List<String> result = new ArrayList<>();
        for (int i = 1; i <= Integer.valueOf(preferences.get(recentCount, "5")); i++) {
            String tmp = preferences.get(recent + i, "");
            if (!"".equals(tmp))
                result.add(tmp);
        }
        return result;
    }
}

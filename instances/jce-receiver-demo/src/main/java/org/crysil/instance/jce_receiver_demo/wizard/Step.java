package org.crysil.instance.jce_receiver_demo.wizard;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class Step extends VBox {

	class StepChanger implements Runnable {

		private Step nextStep;

		public StepChanger(Step ns) {
			nextStep = ns;
		}

		@Override
		public void run() {
			getScene().setRoot(nextStep);
		}
	}

	private Label titleLabel;
	private Pane contentGroup;
	private HBox buttonGroup;

	public Step() {
		titleLabel = new Label();
		HBox.setHgrow(titleLabel, Priority.ALWAYS);
		HBox titleGroup = new HBox();
		titleGroup.getChildren().add(titleLabel);
		titleGroup.setId("title");

		contentGroup = new HBox();
		VBox.setVgrow(contentGroup, Priority.ALWAYS);
		contentGroup.setId("content");

		buttonGroup = new HBox(5);
		buttonGroup.setId("buttons");

		this.getChildren().addAll(titleGroup, contentGroup, buttonGroup);

		this.setId("all");
	}

	public void setTitle(String newTitle) {
		titleLabel.setText(newTitle);
	}

	public void setContent(Pane content) {
		contentGroup.getChildren().clear();
		contentGroup.getChildren().add(content);
	}

	public void setButtons(Button... buttons) {
		buttonGroup.getChildren().clear();
		buttonGroup.getChildren().addAll(buttons);
	}

	public void procedeTo(Step step) {
		Platform.runLater(new StepChanger(step));
	}
}

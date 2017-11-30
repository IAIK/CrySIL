package org.crysil.instance.jce_receiver_demo.wizard;

import javax.crypto.Cipher;

import org.crysil.instance.jce_receiver_demo.model.Data;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class EncryptDecrypt extends Step {

	public EncryptDecrypt(final Data data) {
		setTitle("Encrypting/decrypting...");

		final ProgressIndicator pin = new ProgressIndicator();
		final HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		HBox.setHgrow(hb, Priority.ALWAYS);
		hb.getChildren().add(pin);

		setContent(hb);

		Task<Integer> task = new Task<Integer>() {

			@Override
			protected Integer call() throws Exception {
				try {
					Cipher cipher = Cipher.getInstance("CMS", data.getProvider());

					if (data.getSourceFile().getName().endsWith(".cms"))
						cipher.init(Cipher.DECRYPT_MODE, data.getKey());
					else
						cipher.init(Cipher.ENCRYPT_MODE, data.getKey());

					data.setResult(cipher.doFinal(data.getFileContent()));

					procedeTo(new Save(data));
				} catch (Throwable e) {
					e.printStackTrace();
					procedeTo(new Message("An error has occured.", e.getMessage()));
				}

				return null;
			}
		};

		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}
}

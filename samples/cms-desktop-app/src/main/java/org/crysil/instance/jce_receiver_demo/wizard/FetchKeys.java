package org.crysil.instance.jce_receiver_demo.wizard;

import java.security.KeyStore;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.prefs.Preferences;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

import org.crysil.instance.jce_receiver_demo.Main;
import org.crysil.instance.jce_receiver_demo.model.Data;
import org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider;

import crysil.CrysilAPIFactory;

public class FetchKeys extends Step {

	public FetchKeys(final Data data) {
		setTitle("Fetching keys from Crysil Server");

		final ProgressIndicator pin = new ProgressIndicator();
		final StackPane hb = new StackPane();
		hb.getChildren().add(pin);

		setContent(hb);

		Task<Integer> task = new Task<Integer>() {

			@Override
			protected Integer call() throws Exception {
				try {
					// load anything we need
					// - crysil
                    CrysilAPIFactory.initialize(Preferences.userNodeForPackage(Main.class).get("last", ""));
                    Provider crysilProvider = new CrysilProvider();
					data.setProvider(crysilProvider);

					// - fetch keys
                    KeyStore keystore = KeyStore.getInstance("Crysil", crysilProvider);
					keystore.load(null); // isn't nice at all

					ArrayList<String> keyList = Collections.list(keystore.aliases());
					if(keyList.isEmpty())
                        procedeTo(new Message("An unexpected situation occured",
                                "You do not have any keys available\n or the key server is not reachable"));
					else
						procedeTo(new ChooseKey(data, keystore, keyList));
				} catch (final Exception e) {
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

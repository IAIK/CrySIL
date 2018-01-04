package org.crysil.instance.jce_receiver_demo;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.crysil.authentication.AuthHandlerFactory;
import org.crysil.authentication.authplugins.AuthSecret;
import org.crysil.authentication.authplugins.AuthUsernameAndPassword;
import org.crysil.authentication.interceptor.InterceptorAuth;
import org.crysil.authentication.ui.AutomaticAuthSelector;
import org.crysil.authentication.ui.SecretDialog;
import org.crysil.authentication.ui.UsernameAndPasswordDialog;
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

		final List<AuthHandlerFactory<?, ?, ?>> authPluginFactories = new ArrayList<>();
		authPluginFactories.add(new AuthSecret.Factory<>(SecretDialog.class));
		authPluginFactories.add(new AuthUsernameAndPassword.Factory<>(UsernameAndPasswordDialog.class));
		final InterceptorAuth<AutomaticAuthSelector> interceptor = new InterceptorAuth<>(AutomaticAuthSelector.class);
		interceptor.setAuthenticationPlugins(authPluginFactories);

		crysilProvider.attach(interceptor);
		interceptor.attach(new HttpJsonTransmitter());
		Security.addProvider(crysilProvider);

		// prepare data block
		Data data = new Data();
		data.setProvider(crysilProvider);

		// setup and start gui
		primaryStage.setTitle("Crysil Demo CMS Writer/Reader");
		primaryStage.setScene(new Scene(new SelectFile(data), 700, 300));
		primaryStage.getScene().getStylesheets().add(Main.class.getResource("main.css").toExternalForm());
		primaryStage.show();
	}
}

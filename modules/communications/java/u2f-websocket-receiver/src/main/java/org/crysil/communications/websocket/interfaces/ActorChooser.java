package org.crysil.communications.websocket.interfaces;

import java.util.Map;

import org.crysil.commons.Module;

public interface ActorChooser {
	
	void chooseActor(Map<String, Module> list, ActionPerformedCallback callback);

}

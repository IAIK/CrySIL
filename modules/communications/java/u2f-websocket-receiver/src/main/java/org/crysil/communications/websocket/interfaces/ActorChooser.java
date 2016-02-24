package org.crysil.communications.websocket.interfaces;

import java.util.Collection;

import org.crysil.commons.Module;

public interface ActorChooser {
	
	void chooseActor(Collection<Module> list, ActionPerformedCallback callback);

}

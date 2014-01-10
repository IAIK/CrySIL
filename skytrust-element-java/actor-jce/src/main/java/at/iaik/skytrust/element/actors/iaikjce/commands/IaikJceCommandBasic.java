package at.iaik.skytrust.element.actors.iaikjce.commands;

import at.iaik.skytrust.element.actors.common.BasicCommand;
import at.iaik.skytrust.element.actors.gatekeeper.session.SessionManager;
import at.iaik.skytrust.element.actors.iaikjce.IaikJceActor;
import at.iaik.skytrust.element.actors.iaikjce.JCE;
import at.iaik.skytrust.keystorage.rest.client.RestKeyStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IaikJceCommandBasic extends BasicCommand {

    protected JCE cryptoProvider;
    protected IaikJceActor actor;
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    protected SessionManager sessionManager = SessionManager.getInstance();
    protected RestKeyStorage restKeyStorage;

    public IaikJceCommandBasic(IaikJceActor actor) {
        this.actor = actor;
		restKeyStorage = actor.getRestKeyStorage();
        this.cryptoProvider = actor.getCryptoProvider();
    }

    protected String getKeyId(String completeKeyId) {
        //return completeKeyId.substring(completeKeyId.lastIndexOf(",") + 1);
        return completeKeyId;
    }


}

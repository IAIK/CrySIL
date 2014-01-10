package at.iaik.skytrust.element.actors.gatekeeper.session;

import at.iaik.skytrust.element.actors.gatekeeper.authentication.plugins.UserBean;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    protected static SessionManager instance;
    protected List<SessionTokenBean> sessions;

    private SessionManager() {
        sessions = new ArrayList<SessionTokenBean>();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

	public SessionTokenBean createSession(UserBean user) {
        SessionTokenBean session = new SessionTokenBean();
        session.setUser(user);
        sessions.add(session);
		return session;
    }

    public SessionTokenBean getSession(String id) {
        for (SessionTokenBean session : sessions) {
            if (session.getSessionID().equals(id)) {
                return session;
            }
        }
        return null;
    }

    public void removeOutdatedEntries() {
        List<SessionTokenBean> toBeRemoved = new ArrayList<SessionTokenBean>();

        for (SessionTokenBean session : sessions) {
            if (session.isExpired()) {
                toBeRemoved.add(session);
            }
        }

        for (SessionTokenBean session : toBeRemoved) {
            sessions.remove(session);
        }
    }
}
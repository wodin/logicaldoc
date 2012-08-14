package com.logicaldoc.opencmis.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.server.CallContext;

/**
 * Repository map.
 */
public class LDRepositoryMap {

    private final Map<String, LogicaldocRepository> map;
    private final Map<String, String> logins;

    public LDRepositoryMap() {
        map = new HashMap<String, LogicaldocRepository>();
        logins = new HashMap<String, String>();
    }

    /**
     * Adds a repository object.
     */
    public void addRepository(LogicaldocRepository fsr) {
        if ((fsr == null) || (fsr.getRepositoryId() == null)) {
            return;
        }

        map.put(fsr.getRepositoryId(), fsr);
    }

    /**
     * Gets a repository object by id.
     */
    public LogicaldocRepository getRepository(String repositoryId) {
        // get repository object
    	LogicaldocRepository result = map.get(repositoryId);
        if (result == null) {
            throw new CmisObjectNotFoundException("Unknown repository '" + repositoryId + "'!");
        }

        return result;
    }

    /**
     * Takes user and password from the CallContext and checks them.
     */
    public void authenticate(CallContext context) {
        // check user and password first
        if (!authenticate(context.getUsername(), context.getPassword())) {
            throw new CmisPermissionDeniedException();
        }
    }

    /**
     * Returns all repository objects.
     */
    public Collection<LogicaldocRepository> getRepositories() {
        return map.values();
    }

    /**
     * Adds a login.
     */
    public void addLogin(String username, String password) {
        if ((username == null) || (password == null)) {
            return;
        }

        logins.put(username.trim(), password);
    }

    /**
     * Authenticates a user against the configured logins.
     */
    private boolean authenticate(String username, String password) {
        String pwd = logins.get(username);
        if (pwd == null) {
            return false;
        }

        return pwd.equals(password);
    }
}

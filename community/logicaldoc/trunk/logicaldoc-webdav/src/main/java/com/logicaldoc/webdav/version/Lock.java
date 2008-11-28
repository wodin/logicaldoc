package com.logicaldoc.webdav.version;

import org.apache.jackrabbit.uuid.UUID;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.DefaultActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;

public class Lock extends DefaultActiveLock {

    private final String token = DavConstants.OPAQUE_LOCK_TOKEN_PREFIX + UUID.randomUUID().toString();
    private String owner;
    private boolean isDeep = true; // deep by default
    private long expirationTime = DavConstants.INFINITE_TIMEOUT; // never expires by default;

    /**
     * Create a new <code>DefaultActiveLock</code> with default values.
     */
    public Lock() {
    }

    /**
     * Create a new lock
     *
     * @param lockInfo
     * @throws IllegalArgumentException if either scope or type is invalid.
     */
    public Lock(LockInfo lockInfo) {
        if (lockInfo != null) {
            
            owner = lockInfo.getOwner();
            isDeep = lockInfo.isDeep();
            setTimeout(lockInfo.getTimeout());
        }
    }

    /**
     * @see ActiveLock#isLockedByToken(String)
     */
    public boolean isLockedByToken(String lockToken) {
	return (token != null) && token.equals(lockToken);
    }

    /**
     * @see ActiveLock#isExpired()
     */
    public boolean isExpired() {
	return System.currentTimeMillis() > expirationTime;
    }

    /**
     * @see ActiveLock#getToken()
     */
    public String getToken() {
	return token;
    }

    /**
     * @see ActiveLock#getOwner()
     */
    public String getOwner() {
	return "admin";
    }

    /**
     * @see ActiveLock#setOwner(String)
     */
    public void setOwner(String owner) {
	this.owner = owner;
    }

    /**
     * @see ActiveLock#getTimeout()
     */
    public long getTimeout() {
	return 50000000;
    }

    /**
     * @see ActiveLock#setTimeout(long)
     */
    public void setTimeout(long timeout) {
	if (timeout > 0) {
	    expirationTime = System.currentTimeMillis() + timeout;
	}
    }

    /**
     * @see ActiveLock#isDeep()
     */
    public boolean isDeep() {
	return isDeep;
    }

    /**
     * @see ActiveLock#setIsDeep(boolean)
     */
    public void setIsDeep(boolean isDeep) {
	this.isDeep = isDeep;
    }

    /**
     * This is always a write lock.
     *
     * @return the lock type
     * @see Type#WRITE
     */
    public Type getType() {
        return Type.WRITE;
    }

    /**
     * This is always an exclusive lock.
     *
     * @return the lock scope.
     * @see Scope#EXCLUSIVE
     */
    public Scope getScope() {
        return Scope.SHARED;
    }
}


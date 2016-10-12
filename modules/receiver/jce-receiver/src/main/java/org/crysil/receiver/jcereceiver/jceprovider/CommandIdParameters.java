package org.crysil.receiver.jcereceiver.jceprovider;

import java.security.KeyStore;

import iaik.pkcs.pkcs1.PKCS1AlgorithmParameterSpec;

public class CommandIdParameters extends PKCS1AlgorithmParameterSpec implements KeyStore.LoadStoreParameter {
    
    /** The command id. */
    protected String commandId = "";

    /**
     * Instantiates a new command id parameters.
     *
     * @param commandId the command id
     */
    public CommandIdParameters(String commandId) {
        this.commandId = commandId;
    }

    /**
     * Gets the command id.
     *
     * @return the command id
     */
    public String getCommandId() {
        return commandId;
    }

    /**
     * Sets the command id.
     *
     * @param commandID the new command id
     */
    public void setCommandId(String commandID) {
        this.commandId = commandID;
    }

    /* (non-Javadoc)
     * @see java.security.KeyStore.LoadStoreParameter#getProtectionParameter()
     */
    @Override
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return null;
    }
}

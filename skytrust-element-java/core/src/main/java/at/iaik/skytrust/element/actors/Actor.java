package at.iaik.skytrust.element.actors;

import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.skytrustprotocol.SRequest;
import at.iaik.skytrust.element.skytrustprotocol.SResponse;

import java.util.Set;

/**
 * The actor depicts the active part of the Skytrust Element. Actors actually
 * interpret and execute the given command.
 */
public interface Actor {

    /**
     * Takes the command blob, interprets and executes it.
     *
     * @param skyTrustRequest the command
     */
    SResponse take(SRequest skyTrustRequest);

    /**
     * Method to get provided commands from actor
     */
    Set<CmdType> getProvidedCommands();



}

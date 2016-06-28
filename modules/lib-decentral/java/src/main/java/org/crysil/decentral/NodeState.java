package org.crysil.decentral;

public enum NodeState {

    OFFLINE("Offline"),
    CONNECTING_TOR("Connecting to Tor..."),
    DISCONNECTED("Disconnected"),
    SHUTDOWN("Shutting down..."),
    RUNNING_DIRECT("Running (Directly Reachable)"),
    RUNNING_PORTFWD("Running (Using Port Forwarding)"),
    RUNNING_RELAY("Running (Relayed)"),
    BINDINGS("Setting-Up Bindings..."),
    BINDINGS_OK("Bindgins Set-Up Successfully"),
    BINDINGS_FAIL("Bindings Set-Up Failed"),
    SEED("Starting Seed Node"),
    SEED_OK("Seed Node Successfully Started"),
    SEED_FAIL("Seed Node Set-Up Failed"),
    PEER("Starting Peer Node"),
    PEER_OK("Peer Node Successfully Initialised"),
    PEER_FAIL("Peer Node Set-Up Failed"),
    DIRECT("Trying Direct Discovery..."),
    DIRECT_OK("Direct Discovery Successful"),
    DIRECT_FAIL("Direct Discovery Failed"),
    PORTFWD("Trying to Set-Up Port Forwading"),
    PORTFWD_DISCOVER("Trying Discovery using Port Forwading"),
    PORTFWD_OK("Port-Forwarded Connection Successful"),
    PORTFWD_FAIL("Port-Forwarded Connection Failed"),
    RELAY("Trying to Set-Up Relayed Connection"),
    RELAY_OK("Relayed Connection Successful"),
    RELAY_FAIL("Relayed Connection Failed"),
    ADDSELF("Trying to Add Myself to the DHT"),
    ADDSELF_FAIL("Failed Adding Myself to the DHT"),
    ADDSELF_OK("Succesfully Added Myself"),
    TOR_FAIL("Failed to connect to Tor!"),
    TOR_CONNECTED("Connected to Tor!");

  private String strVal;

  private NodeState(final String defaultMessage) {
    strVal = defaultMessage;
  }

  @Override
  public String toString() {
    return strVal;
  }

}

# Webservice for CrySIL on Android

This webservice enables clients to use a CrySIL instance running on an Android device by providing a static URL. U2F support is given, since the webservice simply forwards any incoming requests to the CrySIL instance and does not modify them in any way.

You have to register a [GCM application](https://developers.google.com/cloud-messaging/registration) with Google and enter your server key in the file [`./src/main/resources/application.properties`](./src/main/resources/application.properties).

### Features

* WebSocket endpoint for registration of a new CrySIL server under `wss://localhost/api/register`
* WebSocket endpoint for managing the status of a CrySIL server under `wss://localhost/api/manage` (mandatory client authentication)
* WebSocket endpoint for CrySIL servers for connecting upon a push notification under `wss://localhost/api/crysil/server` (mandatory client authentication)
* REST endpoint for CrySIL clients under `https://localhost/api/crysil`
* Registered CrySIL servers are stored in a database
* Keys for use in TLS are read from a keystore file

### Configuration

The webservice is configured in the file [`./src/main/resources/application.properties`](./src/main/resources/application.properties)
```INI
# GCM communication
gcmServerKey = TODOENTERYOURGCMKEY
# Keystore for signing CrySIL Android server certificates
keyFile = keystore.pkcs12
keyFilePassword = changeit
keyFileAlias = alias
keyFileAliasPassword = changeit
# Logging
logging.level.org.crysil.instance = DEBUG
# Database for storing registered servers
spring.datasource.url = jdbc:hsqldb:file:temporarydb
spring.jpa.hibernate.ddl_auto = update
# TLS settings
server.port = 8443
server.ssl.enabled = true
# Key store for TLS server key
server.ssl.key-store = keystore.pkcs12
server.ssl.key-store-password = changeit
server.ssl.keyStoreType = PKCS12
server.ssl.keyAlias = alias
# TLS client authentication with trusted signing keys
server.ssl.client-auth = want
server.ssl.trust-store = keystore.pkcs12
server.ssl.trust-store-password = changeit
server.ssl.trust-store-type = PKCS12
```

### Running the Webservice

Compile the sources on the command line:
```
mvn clean package
```

This creates a `.jar` file in the folder `./target`, named `android-webservice-1.0-SNAPSHOT.jar` or similar.

Run the file with plain Java using the default configuration:
```
java -jar target/android-webservice-1.0-SNAPSHOT.jar
```

### Alternatives

Execution of the tests can be skipped when creating the package:
```
mvn -Dmaven.test.skip=true clean package
```

Any configuration value can be overwritten when starting the webservice:
```
java -jar target/android-webservice-1.0-SNAPSHOT.jar --keyFile=anotherstore.pkcs12
```

The designated port of the server can be changed when passing an additional parameter on start-up:
```
java -jar android-webservice-1.0.0-SNAPSHOT.jar --server.port=443
```
Beware that starting a service on port 443 usually requires root privileges.

The server can also be started directly from the project directory, without building a `.jar` file first:
```
mvn spring-boot:run
```


## Communication Protocol

The CrySIL Android app and the webservice communicate over secure WebSockets by sending JSON messages.

All JSON messages sent over the WebSocket connection between the Android app and webservice contain two fields, the header and the payload: 
```JSON
{
  "header": "csr",
  "payload": "LS0tL..."
}
```

### Registering

Process when a CrySIL Android app wants to register itself:

1. The Android app connects to `wss://localhost/api/register?gcm=APA91b...`
2. The URI parameter `gcm` contains the (unique) Google Cloud Messaging ID of the Android device
3. The Android app sends the first message `{ header: "register", payload: "" }`
4. The webservice creates a new device registration (mapping of GCM ID to CrySIL ID) in its datastore and answers with the new CrySIL ID: `{ header: "register", payload: "1" }`
5. The Android app creates a new key and certificate with the CrySIL ID as Common Name 
6. The Android app sends a certificate signing request in the payload of the message `{ header: "csr", payload: "LS0tL..." }` (base64-encoded)
7. The webservice signs the certificate, sending it back in the payload of the message `{ header: "csr", payload: "LS0tL..." }` (base64-encoded)
8. The WebSocket connection is closed by the webservice
9. The Android app saves the key and certificate in the Android Keystore to use it for further requests

The certificate signed from the webservice in the register step needs to be provided as the client TLS certificate of the Android app when connecting to the webservice for further requests.

### Unregistering

Process when a CrySIL Android app wants to unregister from the webservice:

1. The Android app connects to `wss://localhost/api/manage?gcm=APA91b...`
2. The URI parameter `gcm` contains the (unique) Google Cloud Messaging ID of the Android device
3. The Android app has to provide the certificate signed preceding as an client TLS certificate
4. The Android app sends the first message `{ header: "unregister", payload: "" }`
5. The webservice verifies the client TLS certificate and the datastore entry, answering with `{ header: "unregister", payload: "1"}` containing the CrySIL ID on success
6. The WebSocket connection is closed by the webservice

### Pausing and Resuming

The CrySIL Android app can also send a request to tell the webservice it should pause forwarding requests from clients to the server. The process is the same as when unregistering, but sending the messages `{ header: "pause", payload: "" }` and `{ header: "resume", payload: "" }` respectively.

### Handling a CrySIL operation

Process when a CrySIL client sends a request to the webservice, designated for a specific CrySIL server:

1. The CrySIL client posts the request to `https://localhost/api/crysil/?id=1` specifying the CrySIL ID of the designated server
2. The webservice looks up the server in the datastore and sends a notification via Google Cloud Messaging containing a random token and the URL of its own WebSocket endpoint to the CrySIL server app
3. The Android app receives the push notification, opens a new WebSocket connection to the specified URL, e.g. `wss://localhost/api/crysil/server?token=abcde...`
4. The webservice verifies the client TLS certificate of the Android app
5. The webservice forwards the request of the CrySIL client to the CrySIL server on the WebSocket connection established
6. The Android app handles the request and sends the response over the WebSocket connection
7. The webservice forwards the response from the WebSocket connection to the HTTPS connection of the CrySIL client

Any existing WebSocket connection between the Android app and the webservice from requests preceding may be reused.


# CrySIL on Android

This project implements a CrySIL server on Android. The app can be installed on any Android device running Android 4.0 or later. It stores cryptographic keys in the [Android Keystore](https://developer.android.com/training/articles/keystore.html) and provides access to them via the CrySIL interface. CrySIL clients connect to a webservice which forwards the request to the registered CrySIL server. See the documentation of [the webservice](./webservice) and of [the Android app](./crysilapp) for details.

Process to start from scratch:
* Install the CrySIL server on an Android device
* The device needs Google Play Services installed to use Google Cloud Messaging
* The server app will show a first-run wizard to create an initial key and certificate
* Within the app, create a user account which can access the key
* Make sure the webservice is running and accessible
* Within the app, register the CrySIL server on the webservice
* The webservice assigns a CrySIL ID to the server
* The CrySIL client sends a request to the webservice, specifying the server ID
* The Webservice sends a push notification to the CrySIL server (with Google Cloud Messaging)
* The CrySIL server app receives the notification and establishes a secure WebSocket connection to the webservice
* The Webservice forwards the request from the CrySIL client to the Android device
* The CrySIL server and CrySIL client will establish an TLS session for end-to-end security
* Requests and responses are handled over this TLS session
* The WebSocket connection from the Android device to the webservice will stay open for further requests

# CrySIL U2F on Android

This project implements a CrySIL server compatible to U2F commands on Android. The app can be installed on any Android device running Android 4.0 or later. Any cryptographic commands are execute on a smart card connected over NFC. CrySIL/U2F clients connect to a webservice which forwards the request to the registered CrySIL server. See the documentation of [the webservice](./webservice/) and of [the Android app](./crysilapp/) for details.

Process to start from scratch:
* Install the CrySIL server on an Android device
* The device needs Google Play Services installed to use Google Cloud Messaging
* Make sure the webservice is running and accessible
* Within the app, register the CrySIL server on the webservice
* Webservice will assign a CrySIL ID to the server
* CrySIL client sends a request to the webservice, specifying the server ID
* Webservice sends a push notification to the CrySIL server (with Google Cloud Messaging)
* CrySIL server app receives the notification and establishes a secure WebSocket connection to the webservice
* Webservice forwards the request from the CrySIL client to the Android device
* Requests and responses are handled over this channel
* WebSocket connection from the Android device to the webservice will stay open for further requests

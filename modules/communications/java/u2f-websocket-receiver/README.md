# U2F WebSocket Receiver

This is a [U2F](https://www.yubico.com/applications/fido/) compliant receiver for CrySIL. It should be used with a matching actor to build a CrySIL U2F element, see [U2F Readme](./../../../../README-U2F.md). It is typically used in a instance running on Android to connect to a webservice when receiving a push notification, see [Android Readme](./../../../../samples/android/README.md).

The receiver can handle both U2F and CrySIL requests. For details on the conversion see the [U2F commons](./../u2f-commons/).

Main class is `WebSocketReceiver`, which implements the interfaces needed for CrySIL and opens a WebSocket connection to the server specified.. Construction of this receiver needs a `ActorChooser` to let the user choose which actor should perform the cryptographic operation. It also needs a `CertificateCallback` to let the user decide whether to accept a certificate provided by the server the receiver connects to. The receiver also supports using a key store to provide Client TLS authentication upon connecting to a secure WebSocket.



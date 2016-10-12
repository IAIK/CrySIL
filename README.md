# CrySIL

This is CrySIL, the cryptographic service interoperability layer.

The following actors are implemented:
* [SMCC actor](./modules/actors/java/smcc/)
* [U2F Android actor](./modules/actors/java/u2f-android/)
* [U2F PKCS#11 actor](./modules/actors/java/u2f-pkcs11/)
* [U2F Spongy Castle actor](./modules/actors/java/u2f-spongycastle/)

The following communication modules are implemented:
* [U2F commons](./modules/communications/java/u2f-commons/)
* [U2F HTTP receiver](./modules/communications/java/u2f-http-json-receiver/)
* [U2F Websocket receiver](./modules/communications/java/u2f-websocket-receiver/)

The following other modules are implemented:
* [U2F Chromium Extension](./modules/others/chromium/)
* [U2F Windows Credential Provider](./modules/others/windows-cp/)

Be sure to check out the samples too:
* [U2F sample](./samples/u2f/)
* [Android U2F sample](./samples/android/)

Webservice demo:
* Resource filter (bootstrap)

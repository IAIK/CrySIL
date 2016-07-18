# Private U2F Server

This is a [U2F](https://www.yubico.com/applications/fido/) compliant CrySIL element. It is configured to use a [WebSocket receiver](./../../../modules/communications/java/u2f-websocket-receiver/) and two actors: The [Android NFC actor](./../../../modules/actors/java/u2f-android/) and the [Spongy Castle actor](./../../../modules/actors/java/u2f-spongycastle/). It is designed to be used through an [Android app](./../crysilapp/). See that readme for details.

# Android U2F Actor

This is a [U2F](https://www.yubico.com/applications/fido/) compliant actor for CrySIL. It should be used with a matching receiver to build a CrySIL U2F element, see the [U2F readme](./../../../../samples/u2f/) and the [Android readme](./../../../../samples/android/) for details.

This actor forwards incoming commands to an NFC token connected to an Android device. It is designed to support only the `generateU2FKey` and `sign` commands from CrySIL. It requires an instance of [`U2FActivityHandler`](./src/main/java/org/crysil/actor/u2f/U2FActivityHandler.java) to enable NFC on the device and return an instance of [`U2FDeviceHandler`](./src/main/java/org/crysil/actor/u2f/U2FDeviceHandler.java). That device handler is then called to execute U2F commands on a compliant NFC token, e.g. a [YubiKey NEO](https://www.yubico.com/products/yubikey-hardware/yubikey-neo/) or a dual interface smart card from [CardContact](http://www.smartcard-hsm.com/). Communication for more cards can be implemented by extending the [strategy pattern](./src/main/java/org/crysil/actor/u2f/nfc/).

When using this actor in a CrySIL U2F instance, the U2F relying party can not detect any differences to using an YubiKey directly.

# Android U2Fy Actor

This is a [U2F](https://www.yubico.com/applications/fido/) compliant actor for CrySIL. It should be used with a matching receiver to build a CrySIL U2F element, see [U2F Readme](./../../../../README-U2F.md).

This actor forwards incoming commands to an NFC token connected to an Android device. It is designed to support only with `generateU2FKey` and `sign` requests from CrySIL. It requires an instance of `U2FActivityHandler` to enable NFC on the device and return an instance of `U2FDeviceHandler`. That device handler is then called to execute U2F commands on a compliant NFC token, e.g. a [YubiKey NEO](https://www.yubico.com/products/yubikey-hardware/yubikey-neo/). The conversion into APDU is not implemented in this receiver, but needs to be implemented in a class implementing the `U2FDeviceHandler` interface.

When using this actor in a CrySIL U2F instance, the U2F relying party can not detect any differences to using an YubiKey directly.

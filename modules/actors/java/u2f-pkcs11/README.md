# PKCS#11 U2F Actor

This is a [U2F](https://www.yubico.com/applications/fido/) compliant actor for CrySIL. It should be used with a matching receiver to build a CrySIL U2F element, see [U2F Readme](./../../../../samples/u2f/).

This actor forwards incoming commands to an smart card connected inserted in a card reader, connected with the PKCS11 bridge. It is designed to support only with `generateU2FKey` and `sign` requests from CrySIL. The smart card does need to provide an ECC key and an RSA key. It is well tested with cards from [CardContact](http://www.smartcard-hsm.com/) and [gemalto readers](http://www.gemalto.com/readers).

You'll need an PKCS#11 library, e.g. [OpenSC](https://github.com/OpenSC/OpenSC) to use this module. Configure the location of the library file and the PIN for your smart card in [`Pkcs11Actor.java`](./src/main/java/org/crysil/actor/pkcs11/Pkcs11Actor.java). Configure the alias of your keys in [`Command.java`](./src/main/java/org/crysil/actor/pkcs11/Command.java) and in [`U2FKeyHandleStrategy.java`](./src/main/java/org/crysil/actor/pkcs11/strategy/U2FKeyHandleStrategy.java).

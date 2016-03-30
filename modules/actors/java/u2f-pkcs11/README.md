# PKCS#11 U2F Actor

This is a [U2F](https://www.yubico.com/applications/fido/) compliant actor for CrySIL. It should be used with a matching receiver to build a CrySIL U2F element, see the [U2F readme](./../../../../samples/u2f/).

This actor forwards incoming commands to a smart card inserted in a card reader. It uses the [PKCS#11 provider](https://jce.iaik.tugraz.at/sic/Products/Core_Crypto_Toolkits/PKCS_11_Provider) and [PKCS#11 wrapper](https://jce.iaik.tugraz.at/sic/Products/Core_Crypto_Toolkits/PKCS_11_Wrapper) from IAIK. It is designed to support only the `generateU2FKey` and `sign` requests from CrySIL. The smart card does need to provide an ECC key and an RSA key. It is well tested with cards from [CardContact](http://www.smartcard-hsm.com/) and readers from [gemalto](http://www.gemalto.com/readers).

You'll need an PKCS#11 library installed on your machine, e.g. [OpenSC](https://github.com/OpenSC/OpenSC) to use this module. Configure the location of the library file and the PIN for your smart card in [`Pkcs11Actor.java`](./src/main/java/org/crysil/actor/pkcs11/Pkcs11Actor.java). Configure the alias of the ECC key on your smart card in [`Command.java`](./src/main/java/org/crysil/actor/pkcs11/Command.java) and the alias of the RSA key in [`U2FKeyHandleStrategy.java`](./src/main/java/org/crysil/actor/pkcs11/strategy/U2FKeyHandleStrategy.java).

Contrary to the U2F standard, the sign operations are always executed with the same ECC key from the smart card. Thus the generated key handle is used to verify authenticity of the handle only, using the RSA key from the smart card.

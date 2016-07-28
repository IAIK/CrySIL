# SMCC Actor

This is a [U2F](https://www.yubico.com/applications/fido/) compliant actor for CrySIL. It should be used with a matching receiver to build a CrySIL U2F element, see the [U2F readme](./../../../../samples/u2f/).

This actor uses SMCC from [MOCCA](https://joinup.ec.europa.eu/site/mocca/) to forward commands to a compatible signature card like the Austrian Citizen Card. It supports the commands necessary for U2F operation, e.g. generating U2F key handles and signing data. In contrast to the U2F standard, the sign operations are always executed with the same ECC key from the card. Thus the generated key handle is used to verify authenticity of it only, by using the RSA key from the card.

PIN verification for the signature card is handled through fixed constants, meaning you need to configure your PIN prior to compilation in [`SmccPinConfiguration`](./src/main/java/org/crysil/actor/smcc/SmccPinConfiguration.java). This needs to be modified once authentication is implemented in CrySIL.

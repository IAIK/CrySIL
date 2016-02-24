# SMCC Actor

This is a [U2F](https://www.yubico.com/applications/fido/) compliant actor for CrySIL. It should be used with a matching receiver to build a CrySIL U2F element, see [U2F Readme](./../../../../samples/u2f/).

This actor uses SMCC from [MOCCA](https://joinup.ec.europa.eu/site/mocca/) to forward commands to a compatible signature card like the Austrian Citizen Card. It supports the commands necessary for U2F handling, e.g. generating wrapped ECC keys and signing data. Nevertheless, the sign operations are always executed with the same ECC key from the smartcard. Thus the generated key handle is only used to verify authenticity of it, using the RSA key from the smartcard. 

PIN verification for the smartcard is handled through a fixed class. This needs to be modified, once authentication is implemented in CrySIL.

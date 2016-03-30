# U2F Support

This sample demonstrates [U2F](https://www.yubico.com/applications/fido/) (Universal Second Factor authentication) support for CrySIL. With this implementation, any U2F client can use any CrySIL instance as a U2F token. So we combine remote crypto with secure second factor authentication.

First, start an instance:
* [SMCC instance](./smcc-server/)
* [PKCS#11 instance](./pkcs11-server/)

Second, run a U2F client:
* [Chromium extension](./../../modules/others/chromium/)
* [Windows credential provider](./../../modules/others/windows-cp/)

## U2F Compatibility

In general, every instance can be used like a U2F token. The receivers are capable to convert incoming U2F messages (registration and authentication) into CrySIL commands to perform the operations. The main use case of U2F is currently the support in the Google Chrome browser. Therefore, an extension for Chrome is needed to forward the requests to a CrySIL instance. See the readme of that project for details.

In general, U2F relies on elliptic curve keys and a simple challenge-response protocol. For registration, the server (or relying party) sends an application identifier and a random challenge to the token. The token answers with a public key and a key handle for identification later on. For authentication, the server sends the same application identifier, a random challenge and the key handle from before to the token. The token answers with a signature over the challenge, including a counter. See the [documentation at yubico](https://developers.yubico.com/U2F/Protocol_details/Overview.html) for details.

CrySIL actors have implemented different ways to perform the cryptographic operations:

* [SMCC actor](./../../modules/actors/java/smcc/) uses the ECC key on a signature card like the Austrian Citizen Card
* [PKCS#11 actor](./../../modules/actors/java/u2f-pkcs11/) uses an ECC key on a smart card
* [U2F Android actor](./../../modules/actors/java/u2f-android/) forwards all operations to an NFC device
* [U2F Spongy Castle actor](./../../modules/actors/java/u2f-spongycastle/) uses the Android key store

The receivers expect one of two forms of requests:

* Proper U2F commands for registration and authentication, e.g. sent directly by a U2F relying party/client
* Messages containing CrySIL commands, e.g. when converted beforehand by a U2F client containing a CrySIL forwarder (e.g. Windows credential provider or Chrome extension)

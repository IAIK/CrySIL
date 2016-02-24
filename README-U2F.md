# U2F Support

This enables [U2F](https://www.yubico.com/applications/fido/) support for CrySIL. With this projects, every CrySIL instance can be used to emulate a U2F token, therefore enabling secure second factor authentication on a variety of services.

Several projects are needed to enable compatibility:

* [smcc actor](./modules/actors/java/smcc/)

## U2F Compatibility

In general, every instance can be used like a U2F token. The receivers are capable to convert incoming U2F messages (registration and authentication) into CrySIL commands to perform the operations. The main use case of U2F is currently the support in the Google Chrome browser. Therefore, an extension for Chrome is needed to forward the requests to a CrySIL instance. See the Readme of that project for details.

In general, U2F relies on elliptic curve keys and a simple challenge-response protocol. For registration, the server (or relying party) sends an application identifier and a random challenge to the token. The token answers with a public key and a key handle for identification later on. For authentication, the server sends the same application identifier, a random challenge and the key handle from before to the token. The token answers with a signature over the challenge, including a counter.

CrySIL actors have implemented different ways to perform the cryptographic operations:

* [smcc actor](./modules/actors/java/smcc/) performs the cryptographic operations on a signature card like the Austrian Citizen Card



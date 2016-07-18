# U2F Receiver

This is the base for a [U2F](https://www.yubico.com/applications/fido/) compliant receiver for CrySIL. It needs to be extended to build an actual receiver for CrySIL.

## U2F Compatibility

This receiver expects one of two forms of requests:

* Messages containing proper CrySIL commands, e.g. when converted beforehand by a U2F client containing a CrySIL bridge (e.g. Windows credential provider or Chrome extension). These requests are simply forwarded to the connected module.
* Proper U2F commands for registration and authentication, e.g. send directly by a U2F relying party. These commands are converted accordingly.

### Registration

A U2F registration commands looks like this:

```JavaScript
{
  "appId": "https://example.com",
  "version": "U2F_V2",
  "challenge": <32-byte-random>
}
```

That single U2F command is converted into two CrySIL commands, one to generate a wrapped U2F/ECC key, and one to calculate the signature:

```JavaScript
payload = {
  "type" : "generateU2FKeyRequest",
  "certificateSubject" : "CN=CrySIL",
  "appParam" : <appParam>,
  "clientParam" : <clientParam>,
  "encodedRandom" : NULL
}
```

```JavaScript
payload = {
  "type" : "signRequest",
  "algorithm" : "SHA256withECDSA",
  "hashesToBeSigned" : [
     <appParam,clientParam,keyHandle,publicKey>
   ],
   "signatureKey" : {
     "type" : "wrappedKey",
     "encodedWrappedKey" : <wrappedU2FKey>
   }
}
```

### Authentication

A U2F authentication command looks like this:

```JavaScript
{
  "appId": "https://example.com",
  "version": "U2F_V2",
  "challenge": <32-byte-random>,
  "keyHandle": <keyhandle>
}
```

That single U2F command is converted into two CrySIL commands, one to generate a wrapped U2F/ECC key, and one to calculate the signature:

```JavaScript
payload = {
  "type" : "generateU2FKeyRequest",
  "certificateSubject" : "CN=CrySIL",
  "appParam" : <appParam>,
  "clientParam" : NULL,
  "encodedRandom" : <keyhandle>
}
```

```JavaScript
payload = {
  "type" : "signRequest",
  "algorithm" : "SHA256withECDSA",
  "hashesToBeSigned" : [
     <appParam,counter,clientParam>
   ],
  "signatureKey" : {
    "type" : "wrappedKey",
    "encodedWrappedKey" : <wrappedU2FKey>
  }
}
```

The counter needed to calculate the signature is managed by the actor (may be stored securely). it is passed in a special header with type `u2fheader` and inserted by the [receiver](./src/main/java/org/crysil/communications/u2f/U2FReceiverHandler.java) into signature input.


# U2F Receiver

This is a [U2F](https://www.yubico.com/applications/fido/) compliant receiver for CrySIL. It should be used with a matching actor to build a CrySIL U2F element, see [U2F Readme](./../../../../README-U2F.md).

## U2F Compatibility

This receiver expects one of two forms of requests:

* Messages containing proper CrySIL commands, e.g. when converted beforehand by an U2F client containing a CrySIL bridge (e.g. Windows credential provider or Chrome extension). These requests are simply forwarded to the router.
* Proper U2F commands for registration and authentication, e.g. send directly by an U2F relying party. These commands are converted accordingly.

### Registration

An U2F registration commands looks like this:

``` JavaScript
{
  "appId": "https://example.com",
  "version": "U2F_V2",
  "challenge": <32ByteRandom>
}
```

That single U2F command is converted into two CrySIL commands, one to generate a wrapped U2F/ECC key, and one to calculate the signature:

``` JavaScript
payload = {
  "type" : "generateU2FKeyRequest",
  "certificateSubject" : "CN=SkyTrust",
  "appParam" : <appParam>,
  "clientParam" : <clientParam>,
  "encodedRandom" : NULL
}
```

``` JavaScript
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

An U2F authentication command looks like this:

``` JavaScript
{
  "appId": "https://example.com",
  "version": "U2F_V2",
  "challenge": <32ByteRandom>,
  "keyHandle": <keyhandle>
}
```

That single U2F command is converted into two CrySIL commands, one to generate a wrapped U2F/ECC key, and one to calculate the signature:

``` JavaScript
payload = {
  "type" : "generateU2FKeyRequest",
  "certificateSubject" : "CN=SkyTrust",
  "appParam" : <appParam>,
  "clientParam" : NULL,
  "encodedRandom" : <keyhandle>
}
```

``` JavaScript
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

The counter needed to calculate the signature is handled by the actor. It is passed in a special header with type `u2fSkyTrustHeader` and inserted by the receiver into signature input.


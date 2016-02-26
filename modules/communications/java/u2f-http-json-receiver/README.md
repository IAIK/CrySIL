# U2F HTTP Receiver

This is a [U2F](https://www.yubico.com/applications/fido/) compliant receiver for CrySIL. It should be used with a matching actor to build a CrySIL U2F element, see the [U2F readme](./../../../../samples/u2f/).

The receiver offers an endpoint under `/json` expecting U2F or CrySIL commands. For details on the conversion see the [U2F commons](./../u2f-commons/) project.


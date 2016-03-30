# U2F Server

This is a [U2F](https://www.yubico.com/applications/fido/) compliant CrySIL element. It is configured to use the [U2F HTTP receiver](./../../../modules/communications/java/u2f-http-json-receiver/) and the [SMCC actor](./../../../modules/actors/java/smcc/). Please connect a card reader to the machine running the instance and insert a compatible card, e.g. an Austrian citizen card.

## Troubleshooting

If you want to run this project from Eclipse, be sure that the file `.settings/org.eclipse.wst.common.component` contains something along the lines of the following snippet:

```xml
<dependent-module deploy-path="/" handle="module:/overlay/prj/crysil-u2f-http-json-receiver?includes=**/**&amp;excludes=META-INF/MANIFEST.MF">
    <dependency-type>consumes</dependency-type>
</dependent-module>
```

This should be created when importing this maven project into Eclipse, but for some reason unknown it is not.
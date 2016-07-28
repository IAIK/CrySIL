using System;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using Newtonsoft.Json.Linq;

namespace ControlU2FKeys
{
    public class RegisterInternalHandler : IHandler
    {
        private readonly CrySilForwarder handler;

        public RegisterInternalHandler(CrySilForwarder handler)
        {
            this.handler = handler;
        }

        private byte[] StripMetaData(byte[] pubKey)
        {
            if (pubKey.Length == 65)
                return pubKey;
            if (pubKey.Length < 4)
                return pubKey;
            int headerLen = pubKey[3];
            if (pubKey.Length < headerLen + 6)
                return pubKey;
            int keyLen = pubKey[headerLen + 5];
            if (pubKey.Length < headerLen + 7 + keyLen - 1)
                return pubKey;
            byte[] key = new byte[keyLen - 1]; // strip first byte of key too
            Array.Copy(pubKey, headerLen + 7, key, 0, keyLen - 1);
            return key;
        }

        static bool BytesContains(byte[] haystack, byte[] needle)
        {
            var len = needle.Length;
            var limit = haystack.Length - len;
            for (var i = 0; i <= limit; i++)
            {
                var k = 0;
                for (; k < len; k++)
                {
                    if (needle[k] != haystack[i + k]) break;
                }
                if (k == len)
                    return true;
            }
            return false;
        }

        public string Handle(string msg)
        {
            var u2fRequest = JObject.Parse(msg);
            var challengeParam = Helpers.Base64UrlDecode(u2fRequest["challengeHash"].ToString());
            var appParam = Helpers.Base64UrlDecode(u2fRequest["appIdHash"].ToString());
            var crysilResponse = this.handler.ExecuteGenerateWrappedKey(u2fRequest["challengeHash"].ToString(), u2fRequest["appIdHash"].ToString(), null);
            var responseGenKey = JObject.Parse(crysilResponse);
            var payloadGenKey = responseGenKey["payload"];
            var certEncoded = Helpers.Base64UrlDecode(payloadGenKey["encodedX509Certificate"].ToString());
            var certificate = new X509Certificate(certEncoded);
            var keyEncoded = StripMetaData(certificate.GetPublicKey());
            var keyHandleBytes = Helpers.Base64UrlDecode(payloadGenKey["encodedRandom"].ToString());
            var signatureBytes = new byte[] { 0x00 }
                .Concat(appParam)
                .Concat(challengeParam)
                .Concat(keyHandleBytes)
                .Concat(keyEncoded)
                .ToArray();
            var responseStr = this.handler.ExecuteSignatureRequest(payloadGenKey["encodedWrappedKey"].ToString(),
                Helpers.Base64UrlEncode(signatureBytes));
            var responseSign = JObject.Parse(responseStr);
            var payloadSign = responseSign["payload"];
            var signature = Helpers.Base64UrlDecode(payloadSign["signedHashes"][0].ToString());
            var signatureData = new byte[] { 0x05 }
                .Concat(keyEncoded)
                .Concat(new byte[] { (byte)keyHandleBytes.Length })
                .Concat(keyHandleBytes)
                .Concat(certEncoded)
                .Concat(signature)
                .ToArray();
            var fromU2FActor = BytesContains(signature, keyHandleBytes) && BytesContains(signature, certEncoded);
            if (fromU2FActor)
            {
                signatureData = signature;
            }
            var response = new JObject
            {
                ["registrationData"] = Helpers.Base64UrlEncode(signatureData)
            };
            return response.ToString();
        }
    }
}
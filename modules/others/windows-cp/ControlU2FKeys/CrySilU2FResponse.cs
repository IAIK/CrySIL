using System;
using System.IO;
using System.Linq;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Text;

namespace ControlU2FKeys
{
    [DataContract]
    public class CrySilU2FResponse
    {
        [DataMember]
        public string clientData;
        [DataMember]
        public string registrationData;

        public string keyHandle;

        public string publicKey;

        internal void ExtractValues(string appParam, byte[] originalChallenge)
        {
            DataContractJsonSerializer jsonSerializerResponse = new DataContractJsonSerializer(typeof(ClientData));
            object objResponse = jsonSerializerResponse.ReadObject(new MemoryStream(Helpers.Base64UrlDecode(clientData)));
            ClientData clientDataObject = objResponse as ClientData;

            if (clientDataObject == null
                || !clientDataObject.origin.Equals(appParam)
                || !clientDataObject.typ.Equals("navigator.id.finishEnrollment")
                || !clientDataObject.challenge.Equals(Helpers.Base64UrlEncode(originalChallenge))
                )
                throw new Exception("clientData does not contain necessary fields");

            byte[] data = Helpers.Base64UrlDecode(registrationData);
            if (data[0] != 0x05)
                throw new Exception("Invalid registration data");

            var keyLen = 65;
            byte[] keyBytes = new byte[keyLen];
            Array.Copy(data, 1, keyBytes, 0, keyLen);
            publicKey = Helpers.Base64UrlEncode(keyBytes);

            int keyHandleLen = data[66];
            byte[] keyHandleBytes = new byte[keyHandleLen];
            Array.Copy(data, 1 + 1 + keyLen, keyHandleBytes, 0, keyHandleLen);
            keyHandle = Helpers.Base64UrlEncode(keyHandleBytes);

            int certLen = data.Length - 1 - 1 - keyLen - keyHandleLen; // temporary!
            byte[] certBytes = new byte[certLen];
            Array.Copy(data, 1 + 1 + keyLen + keyHandleLen, certBytes, 0, certLen);
            X509Certificate certObject = new X509Certificate(certBytes);
            certBytes = certObject.Export(X509ContentType.Cert);
            certLen = certBytes.Length;

            int sigLen = data.Length - 1 - 1 - keyLen - keyHandleLen - certLen;
            byte[] signatureBytes = new byte[sigLen];
            Array.Copy(data, data.Length - sigLen, signatureBytes, 0, sigLen);

            var bytesToVerify = new byte[] { 0x00 }
                .Concat(SHA256.Create().ComputeHash(new UTF8Encoding().GetBytes(appParam)))
                .Concat(SHA256.Create().ComputeHash(Helpers.Base64UrlDecode(clientData)))
                .Concat(keyHandleBytes)
                .Concat(keyBytes)
                .ToArray();

            var ecdsa = new ECDsaCng(CngKey.Import(FixKeyBytes(certObject.GetPublicKey()), CngKeyBlobFormat.EccPublicBlob))
            {
                HashAlgorithm = CngAlgorithm.Sha256
            };
            if (!ecdsa.VerifyData(bytesToVerify, FixSignatureBytes(signatureBytes)))
                throw new Exception("Signature is not valid");
        }

        /// <summary>
        /// Prepends necessary bytes to the U2F key, s.t. <see cref="CngKey"/> accepts it
        /// </summary>
        private static byte[] FixKeyBytes(byte[] keyIn)
        {
            if (keyIn.Length == 65)
            {
                var keyType = new byte[] { 0x45, 0x43, 0x53, 0x31 };
                var keyLength = new byte[] { 0x20, 0x00, 0x00, 0x00 };
                var key = new byte[64];
                Array.Copy(keyIn, 1, key, 0, 64);
                return keyType.Concat(keyLength).Concat(key).ToArray();
            }
            return keyIn;
        }

        /// <summary>
        /// Extract the plain signature from the ASN.1 structure provided by U2F, s.t. <see cref="ECDsaCng"/> accepts it
        /// </summary>
        private static byte[] FixSignatureBytes(byte[] sigIn)
        {
            if (sigIn.Length != 64)
            {
                var sig = new byte[64];
                if (sigIn[3] == 0x20)
                {
                    Array.Copy(sigIn, 4, sig, 0, 32);
                    Array.Copy(sigIn, sigIn[37] == 0x20 ? 38 : 39, sig, 32, 32);
                }
                else if (sigIn[3] == 0x21)
                {
                    Array.Copy(sigIn, 5, sig, 0, 32);
                    Array.Copy(sigIn, sigIn[38] == 0x20 ? 39 : 40, sig, 32, 32);
                }
                return sig;
            }
            return sigIn;
        }
    }


}

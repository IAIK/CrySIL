using System;
using System.IO;
using System.Net;
using System.Text;
using System.Windows.Forms;
using Newtonsoft.Json.Linq;

namespace ControlU2FKeys
{
    public class CrySilForwarder
    {
        private readonly string url;
        private readonly JObject header;
        public CrySilForwarder(string url)
        {
            this.url = url;
            header = JObject.Parse(@"{
                'type': 'standardHeader',
                'commandId' : '',
                'path' : [ ],
                'protocolVersion' : '2.0'
            }");
        }

        public String ExecuteGenerateWrappedKey(string clientParam, string appParam, string encodedRandom)
        {
            var payload = JObject.Parse(@"{
                'type': 'generateU2FKeyRequest',
                'certificateSubject' : 'CN=CrySIL'
            }");
            payload["appParam"] = DeUrlSafe(appParam);
            payload["clientParam"] = DeUrlSafe(clientParam);
            payload["encodedRandom"] = DeUrlSafe(encodedRandom);
            return Send(payload);
        }

        public String ExecuteSignatureRequest(string keyEncoded, string hashToBeSigned)
        {
            var payload = JObject.Parse(@"{
                'type': 'signRequest',
                'algorithm' : 'SHA256withECDSA'
            }");
            payload["hashesToBeSigned"] = new JArray(DeUrlSafe(hashToBeSigned));
            var signatureKey = JObject.Parse(@"{
                'type' : 'wrappedKey'
            }");
            signatureKey["encodedWrappedKey"] = keyEncoded;
            payload["signatureKey"] = signatureKey;
            return Send(payload);
        }

        public String ExecuteAuthentication(string commandId, string secretValue)
        {
            header["commandId"] = commandId;
            var payload = JObject.Parse(@"{
                'type': 'authChallengeResponse'
            }");
            var authInfo = JObject.Parse(@"{
                'type' : 'SecretAuthInfo'
            }");
            authInfo["secret"] = secretValue;
            payload["authInfo"] = authInfo;
            return Send(payload);
        }

        public String ExectueDoubleAuthentication(string commandId, string secretValue1, string secretValue2)
        {
            header["commandId"] = commandId;
            var payload = JObject.Parse(@"{
                'type': 'authChallengeResponse'
            }");
            var authInfo = JObject.Parse(@"{
                'type' : 'SecretDoubleAuthInfo'
            }");
            authInfo["secret1"] = secretValue1;
            authInfo["secret2"] = secretValue2;
            payload["authInfo"] = authInfo;
            return Send(payload);
        }

        private string DeUrlSafe(string s)
        {
            if (s == null)
                return null;
            s = s.Replace("-", "+").Replace("_", "/").Replace("\r", "").Replace("\n", "");
            while (s.Length % 4 != 0)
                s += "=";
            return s;
        }

        private String Send(JObject payload)
        {
            var requestJson = new JObject(
                new JProperty("header", header),
                new JProperty("payload", payload));
            ServicePointManager
                .ServerCertificateValidationCallback +=
                (s, cert, chain, sslPolicyErrors) => true;
            HttpWebRequest request = WebRequest.Create(url) as HttpWebRequest;
            if (request != null)
            {
                request.Method = "POST";
                byte[] byteArray = Encoding.UTF8.GetBytes(requestJson.ToString());
                request.ContentType = "application/json";
                request.ContentLength = byteArray.Length;
                using (Stream dataStream = request.GetRequestStream())
                {
                    dataStream.Write(byteArray, 0, byteArray.Length);
                }
                using (HttpWebResponse response = request.GetResponse() as HttpWebResponse)
                {
                    if (response != null && response.StatusCode != HttpStatusCode.OK)
                        throw new Exception(string.Format("Server error (HTTP {0}: {1}).", response.StatusCode,
                            response.StatusDescription));

                    if (response != null)
                    {
                        JObject responseJson = JObject.Parse(new StreamReader(response.GetResponseStream()).ReadToEnd());
                        return CheckForAuth(responseJson.ToString());
                    }
                }
            }
            return "";
        }

        private String CheckForAuth(string responseStr)
        {
            var response = JObject.Parse(responseStr);
            var payload = response["payload"];
            if ("authChallengeRequest".Equals(payload["type"].ToString(), StringComparison.InvariantCultureIgnoreCase))
            {
                string authTypeFound = null;
                foreach (var authType in payload["authTypes"])
                {
                    if ("SecretAuthType".Equals(authType["type"].ToString(), StringComparison.InvariantCultureIgnoreCase) || "SecretDoubleAuthType".Equals(authType["type"].ToString(), StringComparison.InvariantCultureIgnoreCase))
                    {
                        authTypeFound = authType["type"].ToString();
                        break;
                    }
                }
                if (authTypeFound == null)
                    return responseStr;
                if ("SecretAuthType".Equals(authTypeFound, StringComparison.InvariantCultureIgnoreCase))
                {
                    var pin = ShowDialog("Enter PIN for Authentication on CrySIL", "Enter PIN");
                    return ExecuteAuthentication(response["header"]["commandId"].ToString(), pin);
                }
                if ("SecretDoubleAuthType".Equals(authTypeFound, StringComparison.InvariantCultureIgnoreCase))
                {
                    var pin1 = ShowDialog("Enter Signature PIN for Authentication on CrySIL", "Enter Signature PIN");
                    var pin2 = ShowDialog("Enter Card PIN for Authentication on CrySIL", "Enter Card PIN");
                    return ExectueDoubleAuthentication(response["header"]["commandId"].ToString(), pin1, pin2);
                }
            }
            return responseStr;
        }

        public static string ShowDialog(string text, string caption)
        {
            Form prompt = new Form()
            {
                Width = 400,
                Height = 170,
                FormBorderStyle = FormBorderStyle.FixedDialog,
                Text = caption,
                StartPosition = FormStartPosition.CenterScreen
            };
            Label textLabel = new Label() { Left = 50, Top = 20, Width = 300, Text = text };
            TextBox textBox = new TextBox() { Left = 50, Top = 50, Width = 300, PasswordChar = '*' };
            Button confirmation = new Button() { Text = "OK", Left = 250, Width = 100, Top = 80, DialogResult = DialogResult.OK };
            confirmation.Click += (sender, e) => { prompt.Close(); };
            prompt.Controls.Add(textBox);
            prompt.Controls.Add(confirmation);
            prompt.Controls.Add(textLabel);
            prompt.AcceptButton = confirmation;

            return prompt.ShowDialog() == DialogResult.OK ? textBox.Text : "";
        }
    }
}
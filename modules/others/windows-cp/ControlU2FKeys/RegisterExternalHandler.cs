using System.Security.Cryptography;
using System.Text;
using Newtonsoft.Json.Linq;

namespace ControlU2FKeys
{
    public class RegisterExternalHandler : IHandler
    {
        private readonly IHandler handler;

        public RegisterExternalHandler(IHandler handler)
        {
            this.handler = handler;
        }

        public string Handle(string msg)
        {
            var u2fRequest = JObject.Parse(msg);
            var appId = u2fRequest["appId"].ToString();
            var version = u2fRequest["version"].ToString();
            var challenge = u2fRequest["challenge"].ToString();
            var clientData = new JObject
            {
                ["origin"] = appId,
                ["challenge"] = challenge,
                ["typ"] = "navigator.id.finishEnrollment"
            };
            var clientDataString = clientData.ToString();
            var clientParam = SHA256.Create().ComputeHash(new UTF8Encoding().GetBytes(clientDataString));
            var appParam = SHA256.Create().ComputeHash(new UTF8Encoding().GetBytes(appId));
            var innerRequest = new JObject
            {
                ["appIdHash"] = Helpers.Base64UrlEncode(appParam),
                ["challengeHash"] = Helpers.Base64UrlEncode(clientParam),
                ["version"] = version
            };
            var crysilResponse = handler.Handle(innerRequest.ToString());
            var internalResponse = JObject.Parse(crysilResponse);
            var response = new JObject
            {
                ["clientData"] = Helpers.Base64UrlEncode(new UTF8Encoding().GetBytes(clientDataString)),
                ["registrationData"] = internalResponse["registrationData"]
            };
            return response.ToString();
        }
    }
}
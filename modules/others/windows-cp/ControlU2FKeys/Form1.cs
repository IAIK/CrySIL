using System;
using System.Net;
using System.Text;
using System.Runtime.Serialization.Json;
using System.Windows.Forms;
using System.IO;

namespace ControlU2FKeys
{
    public partial class Form1 : Form
    {
        private const string AppId = "windows10";
        private const string RegistryKeyHandle = "KeyHandle";
        private const string RegistryPublicKey = "PublicKey";
        private const string RegistryHost = "Host";
        private const string RegistryPort = "Port";
        private const string RegistryUrl = "URL";

        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            Init();
        }

        private void Init()
        {
            cbUser.DataSource = new BindingSource(Helpers.GetSubKeys("Software", "Microsoft", "Windows NT", "CurrentVersion", "ProfileList"), null);
            cbUser.DisplayMember = "Value";
            cbUser.ValueMember = "Key";
            cbUser.SelectedIndex = 0;
            cbUser_SelectedIndexChanged(null, null);
        }

        private void btRegister_Click(object sender, EventArgs e)
        {
            string urlHost = tbHost.Text;
            string urlPort = tbPort.Text;
            string urlUri = tbUrl.Text;
            if (String.IsNullOrEmpty(urlHost) || string.IsNullOrEmpty(urlPort) || string.IsNullOrEmpty(urlUri))
            {
                statusStrip1.Items[0].Text = "Not all host information entered";
                return;
            }
            string url = string.Format("https://{0}:{1}/{2}", urlHost, urlPort, urlUri);
            // TODO Production: Do not ignore all TLS errors!
            ServicePointManager.ServerCertificateValidationCallback += (s, cert, chain, sslPolicyErrors) => true;
            byte[] challengeBytes = new byte[32];
            (new Random()).NextBytes(challengeBytes);
            try
            {
                CrySilU2FRequest u2fRequest = new CrySilU2FRequest { appId = AppId, version = "U2F_V2", challenge = Helpers.Base64UrlEncode(challengeBytes) };
                var response = new RegisterExternalHandler(new RegisterInternalHandler(new CrySilForwarder(url))).Handle(u2fRequest.ToJSON());
                DataContractJsonSerializer jsonSerializerResponse = new DataContractJsonSerializer(typeof(CrySilU2FResponse));
                object objResponse = jsonSerializerResponse.ReadObject(new MemoryStream(new UTF8Encoding().GetBytes(response)));
                CrySilU2FResponse jsonResponse = objResponse as CrySilU2FResponse;
                if (jsonResponse != null)
                {
                    jsonResponse.ExtractValues(AppId, challengeBytes);
                    Helpers.WriteRegistryValue(RegistryKeyHandle, jsonResponse.keyHandle, cbUser.SelectedValue.ToString());
                    Helpers.WriteRegistryValue(RegistryPublicKey, jsonResponse.publicKey, cbUser.SelectedValue.ToString());
                    statusStrip1.Items[0].Text = "New key successfully registered";
                }
            }
            catch (Exception ex)
            {
                statusStrip1.Items[0].Text = "Error: " + ex.Message;
            }
            Init();
        }

        private void btDelete_Click(object sender, EventArgs e)
        {
            Helpers.WriteRegistryValue(RegistryKeyHandle, "", cbUser.SelectedValue.ToString());
            Helpers.WriteRegistryValue(RegistryPublicKey, "", cbUser.SelectedValue.ToString());
            Init();
        }

        private void btSave_Click(object sender, EventArgs e)
        {
            statusStrip1.Items[0].Text = "Saved all values";
            if (!Helpers.WriteRegistryValue(RegistryHost, tbHost.Text, cbUser.SelectedValue.ToString()))
                statusStrip1.Items[0].Text = "Could not save host";
            if (!Helpers.WriteRegistryValue(RegistryPort, tbPort.Text, cbUser.SelectedValue.ToString()))
                statusStrip1.Items[0].Text = "Could not save port";
            if (!Helpers.WriteRegistryValue(RegistryUrl, tbUrl.Text, cbUser.SelectedValue.ToString()))
                statusStrip1.Items[0].Text = "Could not save URL";
        }

        private void cbUser_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (cbUser.SelectedValue == null)
            {
                statusStrip1.Items[0].Text = "No user selected";
                return;
            }
            tbHost.Text = Helpers.GetRegistryValue(RegistryHost, cbUser.SelectedValue.ToString());
            tbPort.Text = Helpers.GetRegistryValue(RegistryPort, cbUser.SelectedValue.ToString());
            tbUrl.Text = Helpers.GetRegistryValue(RegistryUrl, cbUser.SelectedValue.ToString());
            tbPublicKey.Text = Helpers.ToHexString(Helpers.GetRegistryValue(RegistryPublicKey, cbUser.SelectedValue.ToString()));
            tbKeyHandle.Text = Helpers.GetRegistryValue(RegistryKeyHandle, cbUser.SelectedValue.ToString());
        }
    }

}

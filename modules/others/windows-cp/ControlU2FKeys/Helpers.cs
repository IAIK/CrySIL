using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.IO;
using System.Runtime.Serialization.Json;
using System.Text;

namespace ControlU2FKeys
{
    public static class Helpers
    {
        public static string ToJSON<T>(this T obj) where T : class
        {
            DataContractJsonSerializer serializer = new DataContractJsonSerializer(typeof(T));
            using (MemoryStream stream = new MemoryStream())
            {
                serializer.WriteObject(stream, obj);
                return Encoding.Default.GetString(stream.ToArray());
            }
        }

        public static string Base64UrlEncode(byte[] arg)
        {
            return Convert.ToBase64String(arg).TrimEnd('=').Replace('+', '-').Replace('/', '_');
        }

        public static byte[] Base64UrlDecode(string s)
        {
            while (s.Length % 4 != 0)
                s += "=";
            return Convert.FromBase64String(s.Replace('-', '+').Replace('_', '/'));
        }

        public static IDictionary<string, string> GetSubKeys(params string[] folders)
        {
            IDictionary<string, string> returnValues = new Dictionary<string, string>();
            RegistryKey k = Registry.LocalMachine;
            foreach (string folder in folders)
            {
                k = k.OpenSubKey(folder);
                if (k == null)
                    return returnValues;
            }
            foreach (string subkey in k.GetSubKeyNames())
            {
                RegistryKey subreg = k.OpenSubKey(subkey);
                if (Array.IndexOf(subreg.GetValueNames(), "ProfileImagePath") == -1)
                    continue;
                string profileImagePath = subreg.GetValue("ProfileImagePath").ToString();
                if (!profileImagePath.Contains("Users"))
                    continue;
                if (profileImagePath.LastIndexOf('\\') > -1)
                    returnValues[subkey] = profileImagePath.Substring(profileImagePath.LastIndexOf('\\') + 1);
                else
                    returnValues[subkey] = profileImagePath;
            }
            return returnValues;
        }

        private static string GetRegistryValue(string valuename, string defaultValue, params string[] folders)
        {
            RegistryKey k = Registry.LocalMachine;
            foreach (string folder in folders)
            {
                k = k.OpenSubKey(folder);
                if (k == null)
                    return defaultValue;
            }
            return (string)k.GetValue(valuename, defaultValue);
        }

        public static string GetRegistryValue(string valuename, string usersid)
        {
            return GetRegistryValue(valuename, "", "Software", "CrySIL", "U2F", usersid);
        }

        private static bool WriteRegistryValue(string valuename, string data, params string[] folders)
        {
            try
            {
                RegistryKey k = Registry.LocalMachine;
                RegistryKey parent = k;
                foreach (string folder in folders)
                {
                    k = parent.OpenSubKey(folder, true);
                    if (k == null)
                        k = parent.CreateSubKey(folder);
                    parent = k;
                }
                k.SetValue(valuename, data);
                return true;
            }
            catch (Exception)
            {
                return false;
            }
        }

        public static bool WriteRegistryValue(string valuename, string data, string usersid)
        {
            return WriteRegistryValue(valuename, data, "Software", "CrySIL", "U2F", usersid);
        }

        public static string ToHexString(string s)
        {
            return BitConverter.ToString(Base64UrlDecode(s)).Replace("-", ":");
        }
    }
}

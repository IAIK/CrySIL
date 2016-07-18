using System.Runtime.Serialization;

namespace ControlU2FKeys
{
    [DataContract]
    public class CrySilU2FRequest
    {
        [DataMember]
        public string challenge;
        [DataMember]
        public string version;
        [DataMember]
        public string appId;
    }
}

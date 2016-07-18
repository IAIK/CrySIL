using System.Runtime.Serialization;

namespace ControlU2FKeys
{
    [DataContract]
    public class ClientData
    {
        [DataMember]
        public string origin;
        [DataMember]
        public string challenge;
        [DataMember]
        public string typ;
    }
}

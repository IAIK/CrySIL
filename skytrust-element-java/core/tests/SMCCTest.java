import at.iaik.skytrust.element.actors.KeyInfo;
import at.iaik.skytrust.element.actors.SMCC.SmartCardProvider;
import iaik.x509.X509Certificate;
import org.junit.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 5/22/13
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class SMCCTest {
    @Test
    public void testCard() {
        SmartCardProvider smartCardProvider = new SmartCardProvider();

        List<KeyInfo> keys = smartCardProvider.getAvailableKeys();
        byte[] encrypted = smartCardProvider.encrypt("hallo".getBytes(), keys.get(0).getID(), "RSAES-PKCS1-v1_5");

        byte[] decrypted = smartCardProvider.decrypt(encrypted, keys.get(0).getID(), "RSAES-PKCS1-v1_5");

        System.out.println(new String(decrypted));
        //X509Certificate certificate = smartCardProvider.getCertificate(keys.get(0));
    }
}

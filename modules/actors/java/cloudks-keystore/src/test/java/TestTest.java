import java.security.PrivateKey;
import java.security.PublicKey;

import org.crysil.actor.softwarecrypto.CloudKSKeyStore;
import org.crysil.protocol.payload.crypto.key.KeyHandle;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTest {
	@Test
	public void gettingStarted() throws Exception {

		CloudKSKeyStore DUT = new CloudKSKeyStore("jdbc:mysql://localhost/cloudks_dev", "cloudks", "cloudkspassword");
		KeyHandle keyhandle = new KeyHandle();
		keyhandle.setId("admin");
		keyhandle.setSubId("a");
		PrivateKey privatekey = DUT.getJCEPrivateKey(keyhandle);
		Assert.assertTrue(privatekey instanceof PrivateKey);
	}

	@Test
	public void getPublicKey() throws Exception {

		CloudKSKeyStore DUT = new CloudKSKeyStore("jdbc:mysql://localhost/cloudks_dev", "cloudks", "cloudkspassword");
		KeyHandle keyhandle = new KeyHandle();
		keyhandle.setId("admin");
		keyhandle.setSubId("a");
		PublicKey privatekey = DUT.getJCEPublicKey(keyhandle);
		Assert.assertTrue(privatekey instanceof PublicKey);
	}

}

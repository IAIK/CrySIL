/*
 * Crysil Core
 * This file is subject to the license defined in the directory “license” at the top level of this package.
 */

package org.crysil.receiver.jcereceiver.jceprovider;

import java.security.Provider;

import org.crysil.commons.Interlink;
import org.crysil.commons.Module;
import org.crysil.receiver.jcereceiver.crysil.CrysilAPI;
import org.crysil.receiver.jcereceiver.crysilhighlevelapi.CrysilHighLevelAPI;
import org.crysil.receiver.jcereceiver.jceprovider.Ciphers.CMS;
import org.crysil.receiver.jcereceiver.jceprovider.Ciphers.RSAES_PKCS1_V1_5;
import org.crysil.receiver.jcereceiver.jceprovider.Ciphers.RSAES_RAW;
import org.crysil.receiver.jcereceiver.jceprovider.Ciphers.RSA_OAEP;
import org.crysil.receiver.jcereceiver.jceprovider.Signatures.NONEwithRSA;
import org.crysil.receiver.jcereceiver.jceprovider.Signatures.SHA1withRSA;
import org.crysil.receiver.jcereceiver.jceprovider.Signatures.SHA224withRSA;
import org.crysil.receiver.jcereceiver.jceprovider.Signatures.SHA256withRSA;
import org.crysil.receiver.jcereceiver.jceprovider.Signatures.SHA512withRSA;

import common.CrySilAlgorithm;

public class CrysilProvider extends Provider
		implements Interlink
{

	private static final long serialVersionUID = -4049655518950675946L;
	private static CrysilProvider instance0;
	private Module module;
	private CrysilHighLevelAPI api;

	/**
	 * Instantiates a new crysil provider.
	 * @param index
	 */
	private CrysilProvider(Integer index) {
		super("Crysil" + index, 1.0, "Crysil Security Provider");

		// Keystore
		put("KeyStore.Crysil", "org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider$CrysilKeyStore" + index);

		// Ciphers
		put("Cipher.RSA", "org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider$RSAES_RAW" + index);
		put("Alg.Alias.Cipher.RSA/RAW", "RSA");
		put("Alg.Alias.Cipher.RSA/ECB/NOPADDING", "RSA");
		put("Alg.Alias.Cipher.RSA//RAW", "RSA");
		put("Alg.Alias.Cipher.RSA//NOPADDING", "RSA");

		put("Cipher.RSA/PKCS1", "org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider$RSAES_PKCS1_V1_5" + index);
		put("Alg.Alias.Cipher.RSA//PKCS1PADDING", "RSA/PKCS1");

		put("Cipher.RSA/OAEP", "org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider$RSA_OAEP" + index);
		put("Alg.Alias.Cipher.RSA//OAEPPADDING", "RSA/OAEP");

		put("Cipher.CMS", "org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider$CMS" + index);

		// Signatures
		put("Alg.Alias.Signature." + CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_1.getAlgorithmName(),
				"SHA1withRSA");
		put("Alg.Alias.Signature." + CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_224.getAlgorithmName(),
				"SHA224withRSA");
		put("Alg.Alias.Signature." + CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_256.getAlgorithmName(),
				"SHA256withRSA");
		put("Alg.Alias.Signature." + CrySilAlgorithm.RSASSA_PKCS1_V1_5_SHA_512.getAlgorithmName(),
				"SHA512withRSA");

		put("Signature.NONEwithRSA", "org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider$NONEwithRSA" + index);
		put("Signature.SHA1withRSA", "org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider$SHA1withRSA" + index);
		put("Signature.SHA224withRSA",
				"org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider$SHA224withRSA" + index);
		put("Signature.SHA256withRSA",
				"org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider$SHA256withRSA" + index);
		put("Signature.SHA512withRSA",
				"org.crysil.receiver.jcereceiver.jceprovider.CrysilProvider$SHA512withRSA" + index);

	}

	public static CrysilProvider getInstance0()
	{

		if (null == instance0)
			instance0 = new CrysilProvider(0);
		return instance0;
	}

	public static class CrysilKeyStore0 extends CrysilKeyStore {
		public CrysilKeyStore0() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static class CMS0 extends CMS {
		public CMS0() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static class RSAES_RAW0 extends RSAES_RAW {
		public RSAES_RAW0() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static class RSAES_PKCS1_V1_50 extends RSAES_PKCS1_V1_5 {
		public RSAES_PKCS1_V1_50() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static class RSA_OAEP0 extends RSA_OAEP {
		public RSA_OAEP0() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static class NONEwithRSA0 extends NONEwithRSA {
		public NONEwithRSA0() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static class SHA1withRSA0 extends SHA1withRSA {
		public SHA1withRSA0() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static class SHA224withRSA0 extends SHA224withRSA {
		public SHA224withRSA0() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static class SHA256withRSA0 extends SHA256withRSA {
		public SHA256withRSA0() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static class SHA512withRSA0 extends SHA512withRSA {
		public SHA512withRSA0() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static CrysilProvider getInstance1() {

		if (null == instance0)
			instance0 = new CrysilProvider(1);
		return instance0;
	}

	public static class CrysilKeyStore1 extends CrysilKeyStore {
		public CrysilKeyStore1() {
			provider = CrysilProvider.getInstance1();
		}
	}

	public static class CMS1 extends CMS {
		public CMS1() {
			provider = CrysilProvider.getInstance0();
		}
	}

	public static class RSAES_RAW1 extends RSAES_RAW {
		public RSAES_RAW1() {
			provider = CrysilProvider.getInstance1();
		}
	}

	public static class RSAES_PKCS1_V1_51 extends RSAES_PKCS1_V1_5 {
		public RSAES_PKCS1_V1_51() {
			provider = CrysilProvider.getInstance1();
		}
	}

	public static class RSA_OAEP1 extends RSA_OAEP {
		public RSA_OAEP1() {
			provider = CrysilProvider.getInstance1();
		}
	}

	public static class NONEwithRSA1 extends NONEwithRSA {
		public NONEwithRSA1() {
			provider = CrysilProvider.getInstance1();
		}
	}

	public static class SHA1withRSA1 extends SHA1withRSA {
		public SHA1withRSA1() {
			provider = CrysilProvider.getInstance1();
		}
	}

	public static class SHA224withRSA1 extends SHA224withRSA {
		public SHA224withRSA1() {
			provider = CrysilProvider.getInstance1();
		}
	}

	public static class SHA256withRSA1 extends SHA256withRSA {
		public SHA256withRSA1() {
			provider = CrysilProvider.getInstance1();
		}
	}

	public static class SHA512withRSA1 extends SHA512withRSA {
		public SHA512withRSA1() {
			provider = CrysilProvider.getInstance1();
		}
	}

	@Override
	public void attach(Module module) {
		this.module = module;

		api = new CrysilHighLevelAPI(new CrysilAPI(module));
	}

	@Override
	public void detach(Module module) {
		this.module = null;
		api = null;
	}

	public Module getAttachedModule() {
		return module;
	}

	public CrysilHighLevelAPI getApi() {
		return api;
	}
}

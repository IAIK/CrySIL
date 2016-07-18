package org.crysil.actor.u2f.strategy;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Mac;

import org.crysil.actor.u2f.U2FDeviceHandler;
import org.crysil.actor.u2f.nfc.NfcU2FDeviceStrategy;

import com.google.common.primitives.Bytes;

/**
 * This emulates the U2F key generation from Yubico: Private key is generated from HMAC over appParam and random bytes.
 * External MAC is HMAC over appParam and private key. The exported key handle is concatenation of external MAC and
 * random bytes. For the authentication requests, the relying party will send the key handle, and we can verify
 * authenticity by performing the same steps as for key generation.
 * 
 * Note: We do not actually use the private key to perform signature operations later on, we only verify authenticity of
 * the key handle provided by the relying party in the U2F protocol.
 */
public class YubicoRandomKeyHandleStrategy extends U2FKeyHandleStrategy {

	private static final int MAC_LEN = 32;
	private static final int RANDOM_LEN = 32;
	private static final int HANDLE_LEN = MAC_LEN + RANDOM_LEN;

	@Override
	public boolean verifyKeyHandle(byte[] keyHandle, byte[] appParam, byte[] clientParam, U2FDeviceHandler device,
			NfcU2FDeviceStrategy strategy) {
		try {
			byte[] calculatedKeyHandle = calculateKeyHandle(keyHandle, appParam, device, strategy);
			return Arrays.equals(keyHandle, calculatedKeyHandle);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	protected byte[] calculateKeyHandle(byte[] encodedRandom, byte[] appParam, U2FDeviceHandler device,
			NfcU2FDeviceStrategy strategy) throws Exception {
		byte[] random = new byte[RANDOM_LEN];
		byte[] keyHandle = new byte[HANDLE_LEN];
		byte[] existingMac = null;

		if (encodedRandom != null && encodedRandom.length > 0) {
			existingMac = new byte[MAC_LEN];
			keyHandle = encodedRandom;
			System.arraycopy(keyHandle, 0, existingMac, 0, MAC_LEN);
			System.arraycopy(keyHandle, MAC_LEN, random, 0, RANDOM_LEN);
		} else {
			SecureRandom.getInstance("SHA1PRNG").nextBytes(random);
		}

		Mac mac = Mac.getInstance("HmacSHA256");
		Key secretKey = strategy.getSecret(device);

		// internal mac = HMAC[key](appParam || random)
		mac.init(secretKey);
		mac.update(appParam);
		mac.update(random);
		byte[] internalMac = mac.doFinal();

		// external mac = HMAC[key](appParam || internalMac)
		mac.init(secretKey);
		mac.update(appParam);
		mac.update(internalMac);
		byte[] externalMac = mac.doFinal();

		// Check whether externally provided MAC matches the calculated MAC
		if (existingMac != null && !Arrays.equals(externalMac, existingMac)) {
			throw new Exception("MACs do not match, invalid keyHandle given");
		}

		// keyHandle = (externalMac || randomNumber)
		keyHandle = Bytes.concat(externalMac, random);

		return keyHandle;
	}

}

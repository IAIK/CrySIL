package org.crysil.actor.softwarecrypto;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.protocol.header.Header;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

/**
 * keystore for the CloudKS (by Attila Földes) database
 */
public class CloudKSKeyStore implements SoftwareCryptoKeyStore {

	private Connection connection;

	public CloudKSKeyStore(Connection connection)
			throws KeyStoreUnavailableException, ClassNotFoundException, SQLException {

		Security.addProvider(new BouncyCastleProvider());

		this.connection = connection;
	}

	/**
	 * returns the private key of the specified key in JCE-readable form
	 *
	 * @param current the CrySIL key representation
	 * @return the private key
	 * @throws KeyNotFoundException
	 */
	@Override
	public java.security.Key getPrivateKey(final KeyHandle key) throws KeyNotFoundException {
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement(
					"SELECT keys.keydata, keys.type FROM `keys` INNER JOIN keyslots ON keyslots.id=keys.keyslot_id INNER JOIN users ON keyslots.user_id=users.id WHERE users.username=? AND keyslots.name=?");
			st.setString(1, key.getId());
			st.setString(2, key.getSubId());

			ResultSet rs = st.executeQuery();

			// iterate through the java resultset
			while (rs.next()) {
				String keydata = rs.getString("keydata");
				String type = rs.getString("type");

				type = type.substring(0, 3);

				keydata = keydata.replaceAll("-----.*KEY-----", "");
				keydata = keydata.replaceAll("\\n", "");

				if("AES".equals(type)) {
					return new SecretKeySpec(Base64.decode(keydata.getBytes()),
							type);
				} else {
					KeyFactory keyFactory = KeyFactory.getInstance(type);
					PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(Base64.decode(keydata));
					return keyFactory.generatePrivate(privKeySpec);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != st)
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return null;
	}

	@Override
	public X509Certificate getX509Certificate(final KeyHandle key) {
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement(
					"SELECT keys.certificate FROM `keys` INNER JOIN keyslots ON keyslots.id=keys.keyslot_id INNER JOIN users ON keyslots.user_id=users.id WHERE users.username=? AND keyslots.name=?");
			st.setString(1, key.getId());
			st.setString(2, key.getSubId());

			ResultSet rs = st.executeQuery();

			// iterate through the java resultset
			while (rs.next()) {
				String certificate = rs.getString("certificate");

				certificate = certificate.replaceAll("-----.*-----", "");
				certificate = certificate.replaceAll("\\n", "");

				CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
				return (X509Certificate) certFactory
						.generateCertificate(new ByteArrayInputStream(Base64.decode(certificate)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != st)
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return null;
	}

	@Override
	public PublicKey getPublicKey(KeyHandle current) throws InvalidCertificateException, KeyNotFoundException {
		try {
			java.security.Key privateKey = getPrivateKey(current);

			// beat JCE to give up the public exponent...
			RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;

			return KeyFactory.getInstance("RSA").generatePublic(
					new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent()));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<KeyHandle> getKeyList() {
		List<KeyHandle> result = new ArrayList<>();

		PreparedStatement st = null;
		try {
			st = connection.prepareStatement(
					"SELECT users.username, keyslots.name FROM keyslots INNER JOIN users ON keyslots.user_id=users.id");

			ResultSet rs = st.executeQuery();

			// iterate through the java resultset
			while (rs.next()) {
				String id = rs.getString("username");
				String subid = rs.getString("name");

				KeyHandle tmp = new KeyHandle();
				tmp.setId(id);
				tmp.setSubId(subid);
				result.add(tmp);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != st)
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return null;
	}

	@Override
	public void addFilter(Header header) {
		// TODO Auto-generated method stub
	}
}

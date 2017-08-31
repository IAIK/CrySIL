package org.crysil.actor.softwarecrypto;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.crysil.errorhandling.InvalidCertificateException;
import org.crysil.errorhandling.KeyNotFoundException;
import org.crysil.errorhandling.KeyStoreUnavailableException;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.KeyHandle;

import com.google.common.io.BaseEncoding;

/**
 * keystore for the CloudKS (by Attila Földes) database
 */
public class CloudKSKeyStore implements SoftwareCryptoKeyStore {

	private Connection connection;

	public CloudKSKeyStore(String connectionString, String username, String password)
			throws KeyStoreUnavailableException, ClassNotFoundException, SQLException {

		Security.addProvider(new BouncyCastleProvider());

		// create database connection
		String myDriver = "com.mysql.jdbc.Driver";
		String myUrl = connectionString;
		Class.forName(myDriver);
		connection = DriverManager.getConnection(myUrl, username, password);
	}

	/**
	 * returns the private key of the specified key in JCE-readable form
	 *
	 * @param current the CrySIL key representation
	 * @return the private key
	 * @throws KeyNotFoundException
	 */
	@Override
	public PrivateKey getJCEPrivateKey(final Key current) throws KeyNotFoundException {
		KeyHandle key = null;
		if (current instanceof KeyHandle)
			key = (KeyHandle) current;
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

				keydata = keydata.replaceAll("-----.*KEY-----", "");
				keydata = keydata.replaceAll("\\n", "");

				System.out.println(keydata);

				// create java representation of the raw key data
				KeyFactory keyFactory = KeyFactory.getInstance(type);
				PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(BaseEncoding.base64().decode(keydata));
				return keyFactory.generatePrivate(privKeySpec);
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
	public X509Certificate getX509Certificate(KeyHandle keyHandle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PublicKey getJCEPublicKey(Key currentKey) throws InvalidCertificateException, KeyNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
}

package pkcs11;

import iaik.asn1.structures.AlgorithmID;
import iaik.pkcs.pkcs1.RSASSAPkcs1v15ParameterSpec;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import objects.MKey;

import at.iaik.skytrust.SkyTrustAPIFactory;
import at.iaik.skytrust.common.SkyTrustAlgorithm;
import at.iaik.skytrust.common.SkyTrustException;
import at.iaik.skytrust.element.receiver.skytrust.SkyTrustAPI;
import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;
import configuration.L;
import configuration.Server;

/**
 * 
 * is the connection to the Skytrust server, does the communication and should
 * be the starting point for authentication.
 * 
 * 
 * */
public class ServerSession implements IServerSession, ActionListener {

	// private String sessionID;
	private Server.ServerInfo server;

	private SkyTrustAPI api = null;
	JFrame frame;
	JTextField field;

	public ServerSession(Server.ServerInfo s) {
		server = s;
	}

	public void init() {

		initAPI(server.getUrl());
		
//		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//		frame = new JFrame();
//
//		frame.setSize(250, 150);
//		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height
//				/ 2 - frame.getSize().height / 2);
//		JLabel label = new JLabel("URL:");
//		field = new JTextField("http://skytrust-dev.iaik.tugraz.at/skytrust-server-with-auth-2.0/rest/json");
//		JButton button = new JButton("OK");
//		button.addActionListener(this);
//		frame.add(label, BorderLayout.NORTH);
//		frame.add(field);
//		frame.add(button, BorderLayout.SOUTH);
//		frame.show();
//		try {
//			synchronized (this) {
//
//				this.wait();
//			}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		synchronized (this) {

			System.out.println("action performed!");
			initAPI(field.getText());
			this.notify();
			frame.hide();
			System.out.println("finished action");
		}

	}

	private void initAPI(String url) {
		SkyTrustAPIFactory.initialize(url);
		api = SkyTrustAPI.getInstance();
	}

	public Server.ServerInfo getInfo() {
		return server;
	}

	@Override
	public List<MKey> getKeyList() {

		try {
			List<MKey> mKeys = new ArrayList<>();
			List<SKey> keys = api.discoverKeys("certificate");

			for (SKey k : keys) {
				mKeys.add(MKey.fromSKey(k));
			}

			return mKeys;
		} catch (SkyTrustException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public byte[] sign(byte[] pData, MKey key, SkyTrustAlgorithm mech)
			throws PKCS11Error {

		ArrayList<byte[]> list = new ArrayList<>();
		list.add(pData);
		List<byte[]> signedData;
		try {
			signedData = api.signHashRequest(mech.getAlgorithmName(), list,
					key.getSKey());
			return signedData.get(0);
		} catch (SkyTrustException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String mapSkytrustToJCE(SkyTrustAlgorithm m) {
		switch (m) {
		case RSAES_RAW:
			return "";
		case RSAES_PKCS1_V1_5:
			return "";
		case RSA_OAEP:
			return "";
		case RSASSA_PKCS1_V1_5_SHA_1:
			return "SHA1withRSA";
		case RSASSA_PKCS1_V1_5_SHA_224:
			return "SHA224withRSA";
		case RSASSA_PKCS1_V1_5_SHA_256:
			return "SHA256withRSA";
		case RSASSA_PKCS1_V1_5_SHA_512:
			return "SHA512withRSA";
		case RSA_PSS:
			return "";
		case CMS_AES_128_CBC:
			break;
		case CMS_AES_128_CCM:
			break;
		case CMS_AES_128_GCM:
			break;
		case CMS_AES_192_CBC:
			break;
		case CMS_AES_192_CCM:
			break;
		case CMS_AES_192_GCM:
			break;
		case CMS_AES_256_CBC:
			break;
		case CMS_AES_256_CCM:
			break;
		case CMS_AES_256_GCM:
			break;
		case SMIME_AES_128:
			break;
		case SMIME_AES_192:
			break;
		case SMIME_AES_256:
			break;
		case SMIME_DECRYPT:
			break;
		default:
			return "";
		}
		return "";
	}

	public AlgorithmParameterSpec mapSkytrustToJCEPara(SkyTrustAlgorithm m) {
		switch (m) {
		case RSAES_RAW:
			return null;
		case RSAES_PKCS1_V1_5:
		case RSA_OAEP:
			return null;
		case RSASSA_PKCS1_V1_5_SHA_1:
			return new RSASSAPkcs1v15ParameterSpec(AlgorithmID.sha1);
		case RSASSA_PKCS1_V1_5_SHA_224:
			return new RSASSAPkcs1v15ParameterSpec(AlgorithmID.sha224);
		case RSASSA_PKCS1_V1_5_SHA_256:
			return new RSASSAPkcs1v15ParameterSpec(AlgorithmID.sha256);
		case RSASSA_PKCS1_V1_5_SHA_512:
			return new RSASSAPkcs1v15ParameterSpec(AlgorithmID.sha512);
		case RSA_PSS:
			return null; // new RSAPssParameterSpec();
		case CMS_AES_128_CBC:
			break;
		case CMS_AES_128_CCM:
			break;
		case CMS_AES_128_GCM:
			break;
		case CMS_AES_192_CBC:
			break;
		case CMS_AES_192_CCM:
			break;
		case CMS_AES_192_GCM:
			break;
		case CMS_AES_256_CBC:
			break;
		case CMS_AES_256_CCM:
			break;
		case CMS_AES_256_GCM:
			break;
		case SMIME_AES_128:
			break;
		case SMIME_AES_192:
			break;
		case SMIME_AES_256:
			break;
		case SMIME_DECRYPT:
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public boolean verify(byte[] data, byte[] signature, MKey key,
			SkyTrustAlgorithm mech) {
		// TODO: implement this!
		return false;
	}

	@Override
	public byte[] encrypt(byte[] plaindata, MKey key, SkyTrustAlgorithm mech)
			throws PKCS11Error {

		ArrayList<byte[]> list = new ArrayList<>();
		list.add(plaindata);
		ArrayList<SKey> keyList = new ArrayList<>();
		keyList.add(key.getSKey());
		List<List<byte[]>> cipher = null;
		try {
			cipher = api.encryptDataRequest(mech.getAlgorithmName(), list,
					keyList);
		} catch (SkyTrustException e) {
			e.printStackTrace();
		}

		return cipher.get(0).get(0);
	}

	@Override
	public byte[] decrypt(byte[] encdata, MKey key, SkyTrustAlgorithm mech)
			throws PKCS11Error {

		ArrayList<byte[]> list = new ArrayList<>();
		list.add(encdata);
		List<byte[]> plain = null;
		try {
			plain = api.decryptDataRequest(mech.getAlgorithmName(), list,
					key.getSKey());
		} catch (SkyTrustException e) {
			e.printStackTrace();
		}

		return plain.get(0);
	}

	@Override
	public boolean isAutheticated() {
		return false;
	}

}

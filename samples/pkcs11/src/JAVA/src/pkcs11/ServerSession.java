package pkcs11;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.crysil.authentication.interceptor.InterceptorAuth;
import org.crysil.authentication.ui.SwingAuthenticationSelector;
import org.crysil.communications.http.HttpJsonTransmitter;
import org.crysil.errorhandling.CrySILException;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.receiver.jcereceiver.crysil.CrysilAPI;

import common.CrySilAlgorithm;
import configuration.Server;
import iaik.asn1.structures.AlgorithmID;
import iaik.pkcs.pkcs1.RSASSAPkcs1v15ParameterSpec;
import objects.MKey;

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

	private CrysilAPI api = null;
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
		InterceptorAuth<SwingAuthenticationSelector> interceptor = new InterceptorAuth<>(
				SwingAuthenticationSelector.class);

		HttpJsonTransmitter uplink = new HttpJsonTransmitter();
		uplink.setTargetURI(url);

		api = new CrysilAPI();
		api.attach(interceptor);
		interceptor.attach(uplink);
	}

	@Override
	public Server.ServerInfo getInfo() {
		return server;
	}

	@Override
	public List<MKey> getKeyList() {

		try {
			List<MKey> mKeys = new ArrayList<>();
			List<Key> keys = api.discoverKeys("certificate");

			for (Key k : keys) {
				mKeys.add(MKey.fromKey(k));
			}

			return mKeys;
		} catch (CrySILException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public byte[] sign(byte[] pData, MKey key, CrySilAlgorithm mech)
			throws PKCS11Error {

		ArrayList<byte[]> list = new ArrayList<>();
		list.add(pData);
		List<byte[]> signedData;
		try {
			signedData = api.signHashRequest(mech.getAlgorithmName(), list,
					key.getKey());
			return signedData.get(0);
		} catch (CrySILException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String mapSkytrustToJCE(CrySilAlgorithm m) {
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

	public AlgorithmParameterSpec mapSkytrustToJCEPara(CrySilAlgorithm m) {
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
			CrySilAlgorithm mech) {
		// TODO: implement this!
		return false;
	}

	@Override
	public byte[] encrypt(byte[] plaindata, MKey key, CrySilAlgorithm mech)
			throws PKCS11Error {

		ArrayList<byte[]> list = new ArrayList<>();
		list.add(plaindata);
		ArrayList<Key> keyList = new ArrayList<>();
		keyList.add(key.getKey());
		List<List<byte[]>> cipher = null;
		try {
			cipher = api.encryptDataRequest(mech.getAlgorithmName(), list,
					keyList);
		} catch (CrySILException e) {
			e.printStackTrace();
		}

		return cipher.get(0).get(0);
	}

	@Override
	public byte[] decrypt(byte[] encdata, MKey key, CrySilAlgorithm mech)
			throws PKCS11Error {

		ArrayList<byte[]> list = new ArrayList<>();
		list.add(encdata);
		List<byte[]> plain = null;
		try {
			plain = api.decryptDataRequest(mech.getAlgorithmName(), list,
					key.getKey());
		} catch (CrySILException e) {
			e.printStackTrace();
		}

		return plain.get(0);
	}

	@Override
	public boolean isAutheticated() {
		return false;
	}

}

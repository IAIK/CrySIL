package org.crysil.communications.http;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.crysil.protocol.PolymorphicStuff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * custom deserializer for GSon to cope with polymorphic stuff.
 */
public class GSonHelper implements JsonDeserializer<PolymorphicStuff> {

	private static GSonHelper instance = null;

	Map<String, Class<?>> lut;

	/**
	 * grep -r "return \"" org/crysil/protocol/ > sepp
	 * vim sepp
	 * %s/\(.*\):\(.*\);$/\2.*\1/g
	 * %s/^.*return /lut.put(/g
	 * %s/.\*\/, /g  <<< remove that one superfluous \ right between * and /. It has been inserted to not end the javadoc block too early
	 * %s/\//\./g
	 * %s/java/class);/g
	 */
	public GSonHelper() {
		lut = new HashMap<>();
		lut.put("OauthAuthInfo", org.crysil.protocol.payload.auth.oauth.OAuthAuthInfo.class);
		lut.put("OauthAuthType", org.crysil.protocol.payload.auth.oauth.OAuthAuthType.class);
		lut.put("authChallengeRequest", org.crysil.protocol.payload.auth.PayloadAuthResponse.class);
		lut.put("authChallengeResponse", org.crysil.protocol.payload.auth.PayloadAuthRequest.class);
		lut.put("UserNamePasswordAuthInfo", org.crysil.protocol.payload.auth.credentials.UserPasswordAuthInfo.class);
		lut.put("UserNamePasswordAuthType", org.crysil.protocol.payload.auth.credentials.UserPasswordAuthType.class);
		lut.put("IdentifierAuthType", org.crysil.protocol.payload.auth.credentials.IdentifierAuthType.class);
		lut.put("IdentifierAuthInfo", org.crysil.protocol.payload.auth.credentials.IdentifierAuthInfo.class);
		lut.put("SecretAuthType", org.crysil.protocol.payload.auth.credentials.SecretAuthType.class);
		lut.put("SecretAuthInfo", org.crysil.protocol.payload.auth.credentials.SecretAuthInfo.class);
		lut.put("status", org.crysil.protocol.payload.status.PayloadStatus.class);
		lut.put("getKeyResponse", org.crysil.protocol.payload.crypto.keydiscovery.PayloadGetKeyResponse.class);
		lut.put("getKeyRequest", org.crysil.protocol.payload.crypto.keydiscovery.PayloadGetKeyRequest.class);
		lut.put("discoverKeysResponse",
				org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysResponse.class);
		lut.put("discoverKeysRequest",
				org.crysil.protocol.payload.crypto.keydiscovery.PayloadDiscoverKeysRequest.class);
		lut.put("encryptRequest", org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptRequest.class);
		lut.put("encryptResponse", org.crysil.protocol.payload.crypto.encrypt.PayloadEncryptResponse.class);
		lut.put("decryptRequest", org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptRequest.class);
		lut.put("decryptResponse", org.crysil.protocol.payload.crypto.decrypt.PayloadDecryptResponse.class);
		lut.put("decryptCMSResponse", org.crysil.protocol.payload.crypto.decryptCMS.PayloadDecryptCMSResponse.class);
		lut.put("decryptCMSRequest", org.crysil.protocol.payload.crypto.decryptCMS.PayloadDecryptCMSRequest.class);
		lut.put("signRequest", org.crysil.protocol.payload.crypto.sign.PayloadSignRequest.class);
		lut.put("signResponse", org.crysil.protocol.payload.crypto.sign.PayloadSignResponse.class);
		lut.put("standardHeader", org.crysil.protocol.header.StandardHeader.class);
		lut.put("wrappedKey", org.crysil.protocol.payload.crypto.key.WrappedKey.class);
		lut.put("externalCertificate", org.crysil.protocol.payload.crypto.key.ExternalCertificate.class);
		lut.put("internalCertificate", org.crysil.protocol.payload.crypto.key.InternalCertificate.class);
		lut.put("keyHandle", org.crysil.protocol.payload.crypto.key.KeyHandle.class);
		lut.put("modifyWrappedKeyRequest",
				org.crysil.protocol.payload.crypto.modifyWrappedKey.PayloadModifyWrappedKeyRequest.class);
		lut.put("modifyWrappedKeyResponse",
				org.crysil.protocol.payload.crypto.modifyWrappedKey.PayloadModifyWrappedKeyResponse.class);
		lut.put("encryptCMSRequest", org.crysil.protocol.payload.crypto.encryptCMS.PayloadEncryptCMSRequest.class);
		lut.put("encryptCMSResponse", org.crysil.protocol.payload.crypto.encryptCMS.PayloadEncryptCMSResponse.class);
		lut.put("generateWrappedKeyRequest",
				org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateWrappedKeyRequest.class);
		lut.put("generateWrappedKeyResponse",
				org.crysil.protocol.payload.crypto.generatekey.PayloadGenerateWrappedKeyResponse.class);
		lut.put("exportWrappedKeyResponse",
				org.crysil.protocol.payload.crypto.exportWrappedKey.PayloadExportWrappedKeyResponse.class);
		lut.put("exportWrappedKeyRequest",
				org.crysil.protocol.payload.crypto.exportWrappedKey.PayloadExportWrappedKeyRequest.class);
	}

	/**
	 * creates an appropriate builder that knows about the CrySIL protocol polymorphic stuff.
	 * 
	 * @return a builder up for the job of deserializing CrySIL JSON protocol packets
	 */
	private GsonBuilder getBuilder() {
		GsonBuilder sepp = new GsonBuilder();

		Set<Class<?>> franz = new HashSet<>();
		for (Class<?> current : lut.values()) {
			franz.add(current.getSuperclass());
		}

		for (Class<?> current : franz)
			if ((current.getModifiers() & Modifier.ABSTRACT) > 0)
				sepp.registerTypeAdapter(current, this);

		return sepp;
	}

	/**
	 * creates object from string
	 * 
	 * @param jsonString
	 * @param objectType
	 * @return
	 */
	public static <T> T fromJson(String jsonString, Class<T> objectType) {
		if (null == instance)
			instance = new GSonHelper();
		return instance.getBuilder().create().fromJson(jsonString, objectType);
	}

	/**
	 * creates a JSON string from the given object
	 * 
	 * @param object
	 * @return
	 */
	public static String toJson(Object object) {
		return new Gson().toJson(object);
	}

	@Override
	public PolymorphicStuff deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		String type = json.getAsJsonObject().get("type").getAsString();
		return context.deserialize(json, lut.get(type));
	}
}
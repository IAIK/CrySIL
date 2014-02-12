package objects;

import java.util.ArrayList;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;

import pkcs11.PKCS11Error;
import proxys.CK_ATTRIBUTE;

public class ObjectManager {

	private ArrayList<PKCS11Object> objects = new ArrayList<>();
	private ArrayList<Long> ids = new ArrayList<>();

	synchronized public ArrayList<PKCS11Object> findObjects(
			CK_ATTRIBUTE[] template) throws PKCS11Error {
		PKCS11Object object = ObjectBuilder.createFromTemplate(template);
		ArrayList<PKCS11Object> result = new ArrayList<>();

		for (PKCS11Object tmp : objects) {
			if (tmp.equals(object)) {
				result.add(tmp);
			}
		}
		return result;
	}

	synchronized public PKCS11Object getObject(long id) {
		for (Long tmp : ids) {
			if (tmp == id) {
				int index = ids.indexOf(tmp);
				return objects.get(index);
			}
		}
		return null;
	}

	synchronized public long createObject(CK_ATTRIBUTE[] template)
			throws PKCS11Error {
		Long id = getNextId();
		PKCS11Object object = ObjectBuilder.createFromTemplate(template);
		objects.add(object);
		ids.add(id);
		return id;
	}

	synchronized public long createObject(SKey key) throws PKCS11Error {
		Long id = getNextId();
		PKCS11Object object = ObjectBuilder.createFromSkyTrust(key);
		objects.add(object);
		ids.add(id);
		return id;
	}

	synchronized public void deleteObject(long id) {
		for (Long tmp : ids) {
			if (tmp == id) {
				int index = ids.indexOf(tmp);
				ids.remove(index);
				objects.remove(index);
				return;
			}
		}
	}

	private Long getNextId() {
		Long id = 0L;
		for (Long tmp : ids) {
			if (tmp > id) {
				id = tmp;
			}
		}
		id++;
		return id;
	}
}

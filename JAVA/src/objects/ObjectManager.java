package objects;

import java.util.ArrayList;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;

import pkcs11.PKCS11Error;
import pkcs11.PKCS11SkyTrustMapper;
import proxys.CK_ATTRIBUTE;
import proxys.RETURN_TYPE;

public class ObjectManager {

	private ArrayList<PKCS11Object> objects = new ArrayList<>();
	private ArrayList<Long> ids = new ArrayList<>();

	synchronized public ArrayList<PKCS11Object> findObjects(Attribute[] template) throws PKCS11Error {
		ArrayList<PKCS11Object> result = new ArrayList<>();
		for (PKCS11Object tmp : objects) {
			if (tmp.query(template)) {
				result.add(tmp);
			}
		}
		return result;
	}

	synchronized public PKCS11Object getObject(long id) throws PKCS11Error {
		for (Long tmp : ids) {
			if (tmp == id) {
				int index = ids.indexOf(tmp);
				return objects.get(index);
			}
		}
		throw new PKCS11Error(RETURN_TYPE.OBJECT_HANDLE_INVALID);
	}

	synchronized public long createObject(CK_ATTRIBUTE[] template) throws PKCS11Error {
		Long id = getNextId();
		PKCS11Object object = ObjectBuilder.createFromTemplate(template);
		objects.add(object);
		ids.add(id);
		System.err.println("created object with handle: "+id);
		return id;
	}

	synchronized public long createObject(SKey key) throws PKCS11Error {
		Long id = getNextId();
		PKCS11Object object = ObjectBuilder.createFromTemplate(PKCS11SkyTrustMapper.mapKey(key));
		objects.add(object);
		ids.add(id);
		return id;
	}

	synchronized public void deleteObject(long id) throws PKCS11Error {
		for (Long tmp : ids) {
			if (tmp == id) {
				int index = ids.indexOf(tmp);
				ids.remove(index);
				objects.remove(index);
				return;
			}
		}
		throw new PKCS11Error(RETURN_TYPE.OBJECT_HANDLE_INVALID);
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

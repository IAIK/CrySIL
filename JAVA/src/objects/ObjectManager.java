package objects;

import java.util.ArrayList;
import java.util.Arrays;

import at.iaik.skytrust.element.skytrustprotocol.payload.crypto.key.SKey;

import pkcs11.PKCS11Error;
import pkcs11.PKCS11SkyTrustMapper;
import proxys.CK_ATTRIBUTE;
import proxys.RETURN_TYPE;

public class ObjectManager {

	private ArrayList<PKCS11Object> objects = new ArrayList<>();
	private ArrayList<Long> ids = new ArrayList<>();

	synchronized public ArrayList<Long> findObjects(ATTRIBUTE[] template) throws PKCS11Error {
		ArrayList<Long> result = new ArrayList<>();
		for (PKCS11Object tmp : objects) {
			if (tmp.query(template)) {
				result.add(ids.get(objects.indexOf(tmp)));
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

	synchronized public long createObject(ArrayList<ATTRIBUTE> template) throws PKCS11Error {
		Long id = getNextId();
		PKCS11Object object = ObjectBuilder.createFromTemplate(new ArrayList<>(Arrays.asList(template)));
		objects.add(object);
		ids.add(id);
		System.err.println("created object with handle: "+id);
		return id;
	}

	synchronized public long addObject(PKCS11Object object) throws PKCS11Error {
		if(object == null){
			throw new PKCS11Error(RETURN_TYPE.OBJECT_HANDLE_INVALID);
		}
		Long id = getNextId();
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

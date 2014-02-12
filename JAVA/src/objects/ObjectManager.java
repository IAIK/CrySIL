package objects;

import java.util.ArrayList;

import proxys.CK_ATTRIBUTE;

public class ObjectManager {

	private ArrayList<PKCS11Object> objects = new ArrayList<>();
	private ArrayList<Long> ids = new ArrayList<>();

	synchronized public ArrayList<PKCS11Object> getObjects(
			CK_ATTRIBUTE[] template) {
		PKCS11Object object = ObjectBuilder.createFromTemplate(template);
		ArrayList<PKCS11Object> result = new ArrayList<>();

		for (PKCS11Object tmp : objects) {
			if (tmp.matches(object)) {
				result.add(tmp);
			}
		}
		return result;
	}

	synchronized public long createObject(CK_ATTRIBUTE[] template) {
		Long id = getNextId();
		PKCS11Object object = ObjectBuilder.createFromTemplate(template);
		objects.add(object);
		ids.add(id);
		return id;
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

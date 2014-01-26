
import java.util.ArrayList;

public class ObjectStorage {

	private ArrayList<Long> ids = new ArrayList<Long>();
	private ArrayList<Object> objects = new ArrayList<Object>();

	/*
	 * 
	 * */

//	private static ObjectStorage _instance;

	public ObjectStorage() {
	}

//	public static ObjectStorage getInstance() {
//
//		if (_instance == null) {
//			_instance = new ObjectStorage();
//		}
//		return _instance;
//	}

	public Object getObjectById(long id) {
		for (Long l : ids) {
			if (id == l) {
				return objects.get(ids.indexOf(l));
			}
		}
		return null;
	}

	public void addNewObject(long id, Object object) {
		if (getObjectById(id) == null && getIdByObject(object) == -1) {
			ids.add(new Long(id));
			objects.add(object);
		} else {

		}

	}

	public void delObject(long id) {

		for (Long l : ids) {
			if (id == l) {
				int index = ids.indexOf(l);
				objects.remove(index);
				ids.remove(index);
			}
		}

	}

	public void delObject(Object object) {
		int index = ids.indexOf(object);
		if (index != -1) {
			objects.remove(index);
			ids.remove(index);
		}
	}

	public long getIdByObject(Object object) {
		int id = objects.indexOf(object);
		if (id == -1) {
			return id;
		} else {
			return ids.get(id);
		}
	}

}

package org.crysil.instance.datastore;

import org.springframework.data.repository.CrudRepository;

/**
 * Spring does automatically implement this interface for database access to all device registration
 * 
 * @see DeviceRegistration
 */
public interface DeviceRepository extends CrudRepository<DeviceRegistration, Long> {

	DeviceRegistration findByDeviceId(String deviceId);

}

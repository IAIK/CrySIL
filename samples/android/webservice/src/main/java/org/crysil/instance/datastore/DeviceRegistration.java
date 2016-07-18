package org.crysil.instance.datastore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Stores a mapping from deviceId to crysilId, together with its status, in the database
 * 
 * @see DeviceRepository
 */
@Entity
public class DeviceRegistration {

	@Id
	@GeneratedValue
	private Long crysilId;

	@Column(unique = true)
	private String deviceId;

	@Column
	private boolean active;

	/**
	 * Parameter-less constructor is needed for JPA
	 */
	protected DeviceRegistration() {
	}

	public DeviceRegistration(String deviceId) {
		this.deviceId = deviceId;
		this.active = false;
	}

	public Long getCrysilId() {
		return crysilId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}

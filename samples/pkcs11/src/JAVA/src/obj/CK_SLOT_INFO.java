package obj;

public class CK_SLOT_INFO {
	
	private String slotDescription; //64
	private String manufacturerID; //32
	private long flags=0L;
	private CK_VERSION hardwareVersion;
	private CK_VERSION firmwareVersion;
	private String empty = "                                                                                                          ";
	
	public CK_SLOT_INFO(String slotDescription, String manufacturerID, long flags, CK_VERSION hardwareVersion, CK_VERSION firmwareVersion) {
		this.slotDescription = slotDescription;
		this.manufacturerID = manufacturerID;
		this.flags=flags;
		this.hardwareVersion = hardwareVersion;
		this.firmwareVersion = firmwareVersion;
	}

	public String getSlotDescription() {
		return slotDescription;
	}

	public void setSlotDescription(String slotDescription) {
		
		this.slotDescription = slotDescription;
		this.slotDescription= (this.slotDescription+empty).substring(0, 64);
	}

	public String getManufacturerID() {
		return manufacturerID;
	}

	public void setManufacturerID(String manufacturerID) {
		this.manufacturerID = manufacturerID;
		this.manufacturerID= (this.manufacturerID+empty).substring(0, 32);
	}

	public long getFlags() {
		return flags;
	}

	public void setCKF_TOKEN_PRESENT(){
		flags=flags|1L;
	}
	public void setCKF_REMOVABLE_DEVICE(){
		flags=flags|2L;
	}
	public void setCKF_HW_SLOT(){
		flags=flags|4L;
	}

	public CK_VERSION getHardwareVersion() {
		return hardwareVersion;
	}

	public void setHardwareVersion(CK_VERSION hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}

	public CK_VERSION getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(CK_VERSION firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}
	
	
	
	
	
}

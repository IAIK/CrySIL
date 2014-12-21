package objects;

import pkcs11.Util;

public class CK_TOKEN_INFO {
	private String label; // [32]
	private String manufacturerID;// [32];
	private String model;// [16];
	private String serialNumber;// [16];
	private long flags = 0;
	private long ulMaxSessionCount;
	private long ulSessionCount;
	private long ulMaxRwSessionCount;
	private long ulRwSessionCount;
	private long ulMaxPinLen;
	private long ulMinPinLen;
	private long ulTotalPublicMemory;
	private long ulFreePublicMemory;
	private long ulTotalPrivateMemory;
	private long ulFreePrivateMemory;
	private CK_VERSION hardwareVersion;
	private CK_VERSION firmwareVersion;
	private String utcTime;// [16];

	
	public CK_TOKEN_INFO(String label, String manufacturerID, String model,
			String serialNumber, long flags, long ulMaxSessionCount,
			long ulSessionCount, long ulMaxRwSessionCount,
			long ulRwSessionCount, long ulMaxPinLen, long ulMinPinLen,
			long ulTotalPublicMemory, long ulFreePublicMemory,
			long ulTotalPrivateMemory, long ulFreePrivateMemory,
			CK_VERSION hardwareVersion, CK_VERSION firmwareVersion,
			String utcTime) {
		this.label = label;
		this.manufacturerID = manufacturerID;
		this.model = model;
		this.serialNumber = serialNumber;
		this.flags = flags;
		this.ulMaxSessionCount = ulMaxSessionCount;
		this.ulSessionCount = ulSessionCount;
		this.ulMaxRwSessionCount = ulMaxRwSessionCount;
		this.ulRwSessionCount = ulRwSessionCount;
		this.ulMaxPinLen = ulMaxPinLen;
		this.ulMinPinLen = ulMinPinLen;
		this.ulTotalPublicMemory = ulTotalPublicMemory;
		this.ulFreePublicMemory = ulFreePublicMemory;
		this.ulTotalPrivateMemory = ulTotalPrivateMemory;
		this.ulFreePrivateMemory = ulFreePrivateMemory;
		this.hardwareVersion = hardwareVersion;
		this.firmwareVersion = firmwareVersion;
		this.utcTime = utcTime;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = Util.fixStringLen(label, 32);
	}

	public String getManufacturerID() {
		return manufacturerID;
	}

	public void setManufacturerID(String manufacturerID) {
		this.manufacturerID = Util.fixStringLen(manufacturerID, 32);
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = Util.fixStringLen(model, 16);
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = Util.fixStringLen(serialNumber, 16);
	}

	public long getFlags() {
		return flags;
	}

	public void setFlags(long flags) {
		this.flags = flags;
	}

	public long getUlMaxSessionCount() {
		return ulMaxSessionCount;
	}

	public void setUlMaxSessionCount(long ulMaxSessionCount) {
		this.ulMaxSessionCount = ulMaxSessionCount;
	}

	public long getUlSessionCount() {
		return ulSessionCount;
	}

	public void setUlSessionCount(long ulSessionCount) {
		this.ulSessionCount = ulSessionCount;
	}

	public long getUlMaxRwSessionCount() {
		return ulMaxRwSessionCount;
	}

	public void setUlMaxRwSessionCount(long ulMaxRwSessionCount) {
		this.ulMaxRwSessionCount = ulMaxRwSessionCount;
	}

	public long getUlRwSessionCount() {
		return ulRwSessionCount;
	}

	public void setUlRwSessionCount(long ulRwSessionCount) {
		this.ulRwSessionCount = ulRwSessionCount;
	}

	public long getUlMaxPinLen() {
		return ulMaxPinLen;
	}

	public void setUlMaxPinLen(long ulMaxPinLen) {
		this.ulMaxPinLen = ulMaxPinLen;
	}

	public long getUlMinPinLen() {
		return ulMinPinLen;
	}

	public void setUlMinPinLen(long ulMinPinLen) {
		this.ulMinPinLen = ulMinPinLen;
	}

	public long getUlTotalPublicMemory() {
		return ulTotalPublicMemory;
	}

	public void setUlTotalPublicMemory(long ulTotalPublicMemory) {
		this.ulTotalPublicMemory = ulTotalPublicMemory;
	}

	public long getUlFreePublicMemory() {
		return ulFreePublicMemory;
	}

	public void setUlFreePublicMemory(long ulFreePublicMemory) {
		this.ulFreePublicMemory = ulFreePublicMemory;
	}

	public long getUlTotalPrivateMemory() {
		return ulTotalPrivateMemory;
	}

	public void setUlTotalPrivateMemory(long ulTotalPrivateMemory) {
		this.ulTotalPrivateMemory = ulTotalPrivateMemory;
	}

	public long getUlFreePrivateMemory() {
		return ulFreePrivateMemory;
	}

	public void setUlFreePrivateMemory(long ulFreePrivateMemory) {
		this.ulFreePrivateMemory = ulFreePrivateMemory;
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

	public String getUtcTime() {
		return utcTime;
	}

	public void setUtcTime(String utcTime) {
		this.utcTime = Util.fixStringLen(utcTime, 16);
	}

	public void setCKF_RNG() {
		flags = flags | 0x00000001L;

	}

	public void setCKF_WRITE_PROTECTED() {
		flags = flags | 0x00000002L;

	}

	public void setCKF_LOGIN_REQUIRED() {
		flags = flags | 0x00000004L;

	}

	public void setCKF_USER_PIN_INITIALIZED() {
		flags = flags | 0x00000008L;

	}

	public void setCKF_RESTORE_KEY_NOT_NEEDED() {
		flags = flags | 0x00000020L;
	}

	public void setCKF_CLOCK_ON_TOKEN() {
		flags = flags | 0x00000040L;
	}

	public void setCKF_PROTECTED_AUTHENTICATION_PATH() {
		flags = flags | 0x00000100L;

	}

	public void setCKF_DUAL_CRYPTO_OPERATIONS() {
		flags = flags | 0x00000200L;
	}

	public void setCKF_TOKEN_INITIALIZED() {
		flags = flags | 0x00000400L;
	}

	public void setCKF_SECONDARY_AUTHENTICATION() {
		flags = flags | 0x00000800L;
	}

	public void setCKF_USER_PIN_COUNT_LOW() {
		flags = flags | 0x00010000L;
	}

	public void setCKF_USER_PIN_FINAL_TRY() {
		flags = flags | 0x00020000L;
	}

	public void setCKF_USER_PIN_LOCKED() {
		flags = flags | 0x00040000L;
	}

	public void setCKF_USER_PIN_TO_BE_CHANGED() {
		flags = flags | 0x00080000L;
	}

	public void setCKF_SO_PIN_COUNT_LOW() {
		flags = flags | 0x00100000L;
	}

	public void setCKF_SO_PIN_FINAL_TRY() {
		flags = flags | 0x00200000L;
	}

	public void setCKF_SO_PIN_LOCKED() {
		flags = flags | 0x00400000L;
	}

	public void setCKF_SO_PIN_TO_BE_CHANGED() {
		flags = flags | 0x00800000L;
	}


}

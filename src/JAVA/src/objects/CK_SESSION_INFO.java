package objects;

public class CK_SESSION_INFO {
	private long slotID;
	private long state;
	private long flags=0;
	private long ulDeviceError;

	public CK_SESSION_INFO(long slotID, long state, long flags,
			long ulDeviceError) {
		this.slotID = slotID;
		this.state = state;
		this.flags = flags;
		this.ulDeviceError = ulDeviceError;
	}

	public long getSlotID() {
		return slotID;
	}

	public void setSlotID(long slotID) {
		this.slotID = slotID;
	}

	public long getState() {
		return state;
	}

	public void setState(long state) {
		this.state = state;
	}

	public long getFlags() {
		return flags;
	}

	public void setFlags(long flags) {
		this.flags = flags;
	}

	public long getUlDeviceError() {
		return ulDeviceError;
	}

	public void setUlDeviceError(long ulDeviceError) {
		this.ulDeviceError = ulDeviceError;
	}
	
	
	public void setCKF_RW_SESSION(){
		flags=flags|0x00000002;
	}
	public void setCKF_SERIAL_SESSION(){
		flags=flags|0x00000004;
	}

}

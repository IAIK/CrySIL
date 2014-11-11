package obj;

import java.lang.Override;

public class CK_VERSION {

	private byte major;
	private byte minor;
	
	public CK_VERSION(byte major, byte minor){
		this.major=major;
		this.minor=minor;
		System.out.println("Created CK_VERSION");
	}

	public byte getMajor() {
		return major;
	}

	public void setMajor(byte major) {
		this.major = major;
	}

	public byte getMinor() {
		return minor;
	}

	public void setMinor(byte minor) {
		this.minor = minor;
	}

    @Override
    public String toString(){
        return "null null wooohoooo buh!   "+ major + " " + minor;
    }
	
	
}

package objects;

public class CK_MECHANISM_INFO {
	private long ulMinKeySize;
	private long ulMaxKeySize;
	private long flags=0;
	
	
	public CK_MECHANISM_INFO(long ulMinKeySize, long ulMaxKeySize, long flags) {
		this.ulMinKeySize = ulMinKeySize;
		this.ulMaxKeySize = ulMaxKeySize;
		this.flags = flags;
	}


	public long getUlMinKeySize() {
		return ulMinKeySize;
	}


	public void setUlMinKeySize(long ulMinKeySize) {
		this.ulMinKeySize = ulMinKeySize;
	}


	public long getUlMaxKeySize() {
		return ulMaxKeySize;
	}


	public void setUlMaxKeySize(long ulMaxKeySize) {
		this.ulMaxKeySize = ulMaxKeySize;
	}


	public long getFlags() {
		return flags;
	}


	public void setFlags(long flags) {
		this.flags = flags;
	}

	public void setCKF_HW(){
		flags=flags|0x00000001;
	}
	public void setCKF_ENCRYPT(){
		flags=flags|0x00000100;
	}
	public void setCKF_DECRYPT(){
		flags=flags|0x00000200;
	}
	public void setCKF_DIGEST(){
		flags=flags|0x00000400;
	}
	public void setCKF_SIGN(){
		flags=flags|0x00000800;
	}
	public void setCKF_SIGN_RECOVER(){
		flags=flags|0x00001000;
	}
	public void setCKF_VERIFY(){
		flags=flags|0x00002000;
	}
	public void setCKF_VERIFY_RECOVER(){
		flags=flags|0x00004000;
	}
	public void setCKF_GENERATE(){
		flags=flags|0x00008000;
	}
	public void setCKF_GENERATE_KEY_PAIR(){
		flags=flags|0x00010000;
	}
	public void setCKF_WRAP(){
		flags=flags|0x00020000;
	}
	public void setCKF_UNWRAP(){
		flags=flags|0x00040000;
	}
	public void setCKF_DERIVE(){
		flags=flags|0x00080000;
	}
	public void setCKF_EXTENSION(){
		flags=flags|0x80000000;
	}

	
	public boolean getCKF_HW(){
		if(0L!= (flags&0x00000001)){
			return true;
		}
		return false;
	}
	public boolean getCKF_ENCRYPT(){
		if(0L!= (flags&0x00000100)){
			return true;
		}
		return false;
	}
	public boolean getCKF_DECRYPT(){
		if(0L!= (flags&0x00000200)){
			return true;
		}
		return false;
	}
	public boolean getCKF_DIGEST(){
		if(0L!= (flags&0x00000400)){
			return true;
		}
		return false;
	}
	public boolean getCKF_SIGN(){
		if(0L!= (flags&0x00000800)){
			return true;
		}
		return false;
	}
	public boolean getCKF_SIGN_RECOVER(){
		if(0L!= (flags&0x00001000)){
			return true;
		}
		return false;
	}
	public boolean getCKF_VERIFY(){
		if(0L!= (flags&0x00002000)){
			return true;
		}
		return false;
	}
	public boolean getCKF_VERIFY_RECOVER(){
		if(0L!= (flags&0x00004000)){
			return true;
		}
		return false;
	}
	public boolean getCKF_GENERATE(){
		if(0L!= (flags&0x00008000)){
			return true;
		}
		return false;
	}
	public boolean getCKF_GENERATE_KEY_PAIR(){
		if(0L!= (flags&0x00010000)){
			return true;
		}
		return false;
	}
	public boolean getCKF_WRAP(){
		if(0L!= (flags&0x00020000)){
			return true;
		}
		return false;
	}
	public boolean getCKF_UNWRAP(){
		if(0L!= (flags&0x00040000)){
			return true;
		}
		return false;
	}
	public boolean getCKF_DERIVE(){
		if(0L!= (flags&0x00080000)){
			return true;
		}
		return false;
	}
	public boolean getCKF_EXTENSION(){
		if(0L!=(flags&0x80000000)){
			return true;
		}
		return false;
	}
	
	
	
	
}

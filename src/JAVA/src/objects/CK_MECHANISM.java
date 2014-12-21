package objects;

public class CK_MECHANISM {
/*
 * typedef struct CK_MECHANISM {
  CK_MECHANISM_TYPE mechanism;
  CK_VOID_PTR       pParameter;
  CK_ULONG          ulParameterLen; 
} CK_MECHANISM;

 */
    private	long mechanism;
	private Object pParameter;
	private long ulParameterLen;

	public CK_MECHANISM(long mechanism, Object pParameter, long ulParameterLen) {
		this.mechanism = mechanism;
		this.pParameter = pParameter;
		this.ulParameterLen = ulParameterLen;
	}
	public long getMechanism() {
		return mechanism;
	}
	public void setMechanism(long mechanism) {
		this.mechanism = mechanism;
	}
	public Object getpParameter() {
		return pParameter;
	}
	public void setpParameter(Object pParameter) {
		this.pParameter = pParameter;
	}
	public long getUlParameterLen() {
		return ulParameterLen;
	}
	public void setUlParameterLen(long ulParameterLen) {
		this.ulParameterLen = ulParameterLen;
	}
	
	
	
	
}

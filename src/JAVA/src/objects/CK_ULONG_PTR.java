package objects;

public class CK_ULONG_PTR {

	private long value=0;
	
	public CK_ULONG_PTR(long value) {
		this.value=value;
	}
	
	public long getValue(){
		return value;
	}
	
	public void setValue(long value){
		this.value=value;
	}
	
}

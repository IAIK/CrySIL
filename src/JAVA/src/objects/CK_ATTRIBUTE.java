package objects;

public class CK_ATTRIBUTE {

	private long type;
	private Object pValue;
	private long ulValueLen;

	public CK_ATTRIBUTE(long type, Object pValue, long ulValueLen) {
		this.type = type;
		this.pValue = pValue;
		this.ulValueLen=ulValueLen;
		
		if(pValue instanceof byte[] || pValue instanceof Byte[]){
			byte[] arr = (byte[]) pValue;
			if(arr.length!= ulValueLen){
			}
			ulValueLen = arr.length;
		}
		if(Boolean.class.isInstance(pValue) || pValue instanceof Boolean){
			ulValueLen = 1L;
		}
	}
	public long getType() {
		return type;
	}
	public void setType(long type) {
		this.type = type;
	}
	public Object getpValue() {
		return pValue;
	}
	public boolean getpValueAsBool() {
		return (Boolean) pValue;
	}
	public long getpValueAsLong(){
		try{
		return new Long((long) pValue);
		}catch(ClassCastException e){
			e.printStackTrace();
		}
		return 0L;
	}
	public void setpValue(Object pValue) {
                this.pValue = pValue;
	}
	
	@Override
	public boolean equals(Object obj){
		

		
		if(!pValue.getClass().equals(((CK_ATTRIBUTE)obj).pValue.getClass())){
			return false;
		}
		if(pValue instanceof Long){
			long val = (long) ((CK_ATTRIBUTE)obj).pValue;
			long val2= (long) pValue;
			boolean ret = false;
			if(val == val2){
				ret =true;
			}
			return  ret;
		}
		if(pValue instanceof Boolean){
			boolean val = (boolean) ((CK_ATTRIBUTE)obj).pValue;
			boolean val2= (boolean) pValue;
			boolean ret = false;
			if((val==true && val2==true)||(val==false && val2==false)|| val == val2){
				ret = true;
			}
			
			return  ret;
		}
		
		if(pValue instanceof byte[]){
			boolean res= true; //Arrays.deepEquals(((Object[])pValue), ((Object[])((CK_ATTRIBUTE)obj).pValue));
			byte[] arr1 = (byte[]) pValue;
			byte[] arr2 = (byte[]) ((CK_ATTRIBUTE)obj).pValue;
			if(arr1.length != arr2.length){
				res = false;
			}else{
				for(int i=0; i< arr1.length; i++){
					if(arr1[i] != arr2[i]){
						res=false;
						break;
					}
				}
			}
            return res;
		}
		if(pValue instanceof CK_DATE){
			boolean res=  (((CK_DATE)pValue).equals(((CK_ATTRIBUTE)obj).pValue));
            return res;
		}
		if(pValue instanceof String){
			boolean res=  (((String)pValue).compareTo((String) ((CK_ATTRIBUTE)obj).pValue))==0;
            return res;
		}
		return false;
	}
	
	
	public static CK_ATTRIBUTE find(CK_ATTRIBUTE[] attributes, long type){
		CK_ATTRIBUTE retVal = null;
		for(CK_ATTRIBUTE tmp : attributes){
			if(tmp.type==type){
				retVal=tmp;
			}
		}
		return retVal;
		
	}
	public CK_ATTRIBUTE createClone() {
		return new CK_ATTRIBUTE(type, pValue, ulValueLen);
	}
	public boolean query(CK_ATTRIBUTE query_attr) {
		if(query_attr==null){
			return true;
		}
		if(query_attr.getpValue()==null){
			return true; //no data --> irrelevant
		}
	
		return query_attr.equals(this);

	}
	

	public long getUlValueLen() {
		return ulValueLen;
	}
	public void setUlValueLen(long ulValueLen) {
		this.ulValueLen = ulValueLen;
	}
	public String toString(){
		String val = "";
		if(pValue instanceof Long){
			val = String.valueOf((Long)pValue);
		}else if(pValue instanceof byte[]){
			val = new String((byte[])pValue);
		}else{
			val= String.valueOf(pValue);
		}
		
		
		
		return new String("type: "+type + " len: "+ ulValueLen + " pValue: _"+val+"_");
		
	}
	 
	
	
}

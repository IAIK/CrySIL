package pkcs11;

public class Util {
	public static boolean isFlagSet(long bitfield, int flag){
		return (bitfield & flag) != 0;
	}
	public static long setFlag(long bitfield, int flag){
		bitfield = (bitfield | flag);
		return bitfield;
	}
	public static final long initFlags = 0x0L;
	public static String fixStringLen(String str,int len){
		if(str.length() < len){
			int d = len - str.length();
			String padd = new String(new byte[d]);
			return str+padd;
		}else{
			return str.substring(0,len);
		}
	}
}

package pkcs11;

import objects.CK_FLAGS;

import java.util.Arrays;


public class Util {
	public static boolean isFlagSet(long bitfield, Long ckfSerialSession){
		return (bitfield & ckfSerialSession) != 0;
	}
	public static long setFlag(long bitfield, int flag){
		bitfield = (bitfield | flag);
		return bitfield;
	}
	public static long setFlag(long bitfield, long flag){
		bitfield = (bitfield | flag);
		return bitfield;
	}
	public static final long initFlags = 0x0L;
	public static String fixStringLen(String str,int len){
		if(str.length() < len){
			int d = len - str.length();
			char[] arr = new char[d];
			Arrays.fill(arr, ' ');
			String padd = new String(arr);
			return str+padd;
		}else{
			return str.substring(0,len);
		}
	}
//	public static byte[] copyCDataToByteArray(long dataCPtr, long len){
//		return copyCDataToByteArray(new CK_BYTE_ARRAY(dataCPtr,false),len);
//	}
//	public static byte[] copyCDataToByteArray(CK_BYTE_ARRAY data, long len){
//		byte[] a = new byte[ (int)len];
//		for(int i =0; i< len; i++){
//			a[i] = (byte) data.getitem(i);
//		}
//		return a;
//	}
//	public static int copyByteArrayToCData(byte[] data, CK_BYTE_ARRAY out_cdata){
//		for(int i=0;i<data.length;i++){
//			out_cdata.setitem(i, data[i]);			
//		}
//		return data.length;
//	}
//	public static void copy(CK_BYTE_ARRAY src_cdata, CK_BYTE_ARRAY dst_cdata, int len){
//		for(int i=0;i<len;i++){
//			dst_cdata.setitem(i, src_cdata.getitem(i));			
//		}
//	}

	public static class Capabilities  {
		protected long flags = Util.initFlags;;
		public Capabilities sign(){
			flags = Util.setFlag(flags, CK_FLAGS.CKF_SIGN);
			return this;
		}
		public Capabilities verify(){
			flags = Util.setFlag(flags, CK_FLAGS.CKF_VERIFY);
			return this;
		}
		public Capabilities encrypt(){
			flags = Util.setFlag(flags, CK_FLAGS.CKF_ENCRYPT);
			return this;
		}
		public Capabilities decrypt(){
			flags = Util.setFlag(flags, CK_FLAGS.CKF_DECRYPT);
			return this;
		}
		public Capabilities wrap(){
			flags = Util.setFlag(flags, CK_FLAGS.CKF_WRAP);
			return this;
		}
		public Capabilities unwrap(){
			flags = Util.setFlag(flags, CK_FLAGS.CKF_UNWRAP);
			return this;
		}
		public boolean isSign(){
			return Util.isFlagSet(flags, CK_FLAGS.CKF_SIGN);
		}
		public boolean isVerify(){
			return Util.isFlagSet(flags, CK_FLAGS.CKF_VERIFY);
		}
		public boolean isEncrypt(){
			return Util.isFlagSet(flags, CK_FLAGS.CKF_ENCRYPT);
		}
		public boolean isDecrypt(){
			return Util.isFlagSet(flags, CK_FLAGS.CKF_DECRYPT);
		}
		public boolean isWrap(){
			return Util.isFlagSet(flags, CK_FLAGS.CKF_WRAP);
		}
		public boolean isUnwrap(){
			return Util.isFlagSet(flags, CK_FLAGS.CKF_UNWRAP);
		}
		public long getAsFlags(){
			return flags;
		}
	}
}

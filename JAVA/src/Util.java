
public class Util {
	public static boolean isFlagSet(long bitfield, int flag){
		return (bitfield & flag) != 0;
	}
	public static long setFlag(long bitfield, int flag){
		bitfield = (bitfield | flag);
		return bitfield;
	}
	public static final long initFlags = 0x0L;
}

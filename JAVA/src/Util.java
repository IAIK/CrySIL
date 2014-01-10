
public class Util {
	public static boolean isFlagSet(long bitfield, int flag){
		return (bitfield & flag) != 0;
	}
	public static void setFlag(long bitfield, int flag){
		bitfield = (bitfield | flag);
	}
	public static final long initFlags = 0x0L;
}

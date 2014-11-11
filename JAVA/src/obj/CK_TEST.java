package obj;

import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.String;

public class CK_TEST {
	private final String empty = "                                                  ";
	private CK_VERSION cryptokiVersion;
	private String manufacturerID=empty; // 32
	private long flags = 0L;
	private String libraryDescription=empty; // 32
	private CK_VERSION libraryVersion;

	public CK_TEST(CK_VERSION first,String string,  long second, long third, CK_VERSION forth) {
        System.out.println("first:   " + first.toString());
        System.out.println("string: "+ string);
        System.out.println("second:   " + second);
        System.out.println("third:   " + third);
        System.out.print("forth: "+forth);
    }
	public CK_VERSION getCryptokiVersion() {
		return cryptokiVersion;
	}

	public void setCryptokiVersion(CK_VERSION cryptokiVersion) {
		this.cryptokiVersion = cryptokiVersion;
	}

	public String getManufacturerID() {
		return manufacturerID;
	}

	public void setManufacturerID(String manufacturerID) {
		this.manufacturerID = (manufacturerID.concat(empty)).substring(0, 31);
	}

	public long getFlags() {
		return flags;
	}

	public void setFlags(Long flags) {
		this.flags = flags;
	}

	public String getLibraryDescription() {
		return libraryDescription.substring(0, 31);
	}

	public void setLibraryDescription(String libraryDescription) {
		this.libraryDescription = (libraryDescription.concat(empty)).substring(
				0, 31);
	}

	public CK_VERSION getLibraryVersion() {
		return libraryVersion;
	}

	public void setLibraryVersion(CK_VERSION libraryVersion) {
		this.libraryVersion = libraryVersion;
	}

}

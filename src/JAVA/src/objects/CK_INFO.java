package objects;

import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.String;

public class CK_INFO {
	private final String empty = "                                                  ";
	private CK_VERSION cryptokiVersion;
	private String manufacturerID=empty; // 32
	private long flags = 0L;
	private String libraryDescription=empty; // 32
	private CK_VERSION libraryVersion;

	public CK_INFO(CK_VERSION cryptokiVersion, String manufacturerID,
			long flags, String libraryDescription, CK_VERSION libraryVersion) {

		this.cryptokiVersion = cryptokiVersion;
		this.libraryVersion = libraryVersion;
//		if (manufacturerID == null) {
//			this.manufacturerID = empty.substring(0, 32);
//		} else {
//			this.manufacturerID = (manufacturerID.concat(empty)).substring(0,
//					31);
//		}
//		this.flags = flags;
//		if (libraryDescription == null) {
//			this.libraryDescription = empty.substring(0, 32);
//		} else {
//			this.libraryDescription = (libraryDescription.concat(empty))
//					.substring(0, 31);
//		}
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
		this.manufacturerID = (manufacturerID.concat(empty)).substring(0, 32);
	}

	public long getFlags() {
		return flags;
	}

	public void setFlags(Long flags) {
		this.flags = flags;
	}

	public String getLibraryDescription() {
		return libraryDescription.substring(0, 32);
	}

	public void setLibraryDescription(String libraryDescription) {
		this.libraryDescription = (libraryDescription.concat(empty)).substring(
				0, 32);
	}

	public CK_VERSION getLibraryVersion() {
		return libraryVersion;
	}

	public void setLibraryVersion(CK_VERSION libraryVersion) {
		this.libraryVersion = libraryVersion;
	}

}

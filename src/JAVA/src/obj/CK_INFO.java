package obj;


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

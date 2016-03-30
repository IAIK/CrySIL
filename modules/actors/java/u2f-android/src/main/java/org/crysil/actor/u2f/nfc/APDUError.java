package org.crysil.actor.u2f.nfc;

/**
 * Thrown when the APDU response from an NFC device contains an error code (last two bytes not equal to 0x9000)
 */
public class APDUError extends Exception {

	private static final long serialVersionUID = 2732899551773813986L;

	private final int code;

	public APDUError(int code) {
		super(String.format("APDU status: %04x", code));
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}

package org.crysil.actor.smcc;

import at.gv.egiz.smcc.CancelledException;
import at.gv.egiz.smcc.PinInfo;
import at.gv.egiz.smcc.pin.gui.PINGUI;

public class SmccPinConfiguration implements PINGUI {

	private static SmccPinConfiguration instance = null;

	private String pin1 = null;
	private String pin2 = null;

	private SmccPinConfiguration() {
		pin1 = "123456";
		pin2 = "1234";
	}

	/**
	 * Singleton pattern to provide access to the PINs the user has entered as the SkyTrust authorization
	 */
	public static SmccPinConfiguration getInstance() {
		if (instance == null) {
			instance = new SmccPinConfiguration();
		}
		return instance;
	}

	/**
	 * We don't get the resource key from {@link PinInfo pinSpec}, so we need to rely on some guessing :/
	 */
	@Override
	public char[] providePIN(PinInfo pinSpec, int retries) throws CancelledException, InterruptedException {
		if (retries <= 2)
			throw new CancelledException("We better not try again, maybe it's the wrong PIN?");
		if (pinSpec.getMinLength() == 6 || pinSpec.getKID() == -127) {
			if (pin1 == null)
				throw new CancelledException("We don't know the PIN");
			return pin1.toCharArray();
		} else if (pinSpec.getMinLength() == 4 || pinSpec.getKID() == 1) {
			if (pin2 == null)
				throw new CancelledException("We don't know the PIN");
			return pin2.toCharArray();
		} else {
			throw new CancelledException("We don't know the PIN");
		}
	}

	@Override
	public void enterPINDirect(PinInfo pinInfo, int retries) throws CancelledException, InterruptedException {
	}

	@Override
	public void enterPIN(PinInfo pinInfo, int retries) throws CancelledException, InterruptedException {
	}

	@Override
	public void validKeyPressed() {
	}

	@Override
	public void correctionButtonPressed() {
	}

	@Override
	public void allKeysCleared() {
	}
}

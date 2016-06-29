package org.crysil.protocol.payload.crypto.stickypolicy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.crysil.protocol.payload.PayloadRequest;
import org.crysil.protocol.payload.crypto.PayloadWithKey;
import org.crysil.protocol.payload.crypto.key.Key;
import org.crysil.protocol.payload.crypto.key.WrappedKey;

public class PayloadExtractStickyPolicyRequest extends PayloadRequest implements PayloadWithKey {

  private final List<Key> keys = new LinkedList<>();

  @Override
  public PayloadRequest getBlankedClone() {
    return new PayloadExtractStickyPolicyRequest();
  }

  @Override
  public String getType() {
    return "extractStickyPolicyRequest";
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {
        getKeys(),
        getType() });
  }

  public void setWrappedKey(final WrappedKey key) {
    keys.add(key);
  }

  @Override
  public List<Key> getKeys() {
    return keys;
  }

}

package org.crysil.gridh;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Provider;
import java.security.Security;
import java.util.Map;

import javax.crypto.Cipher;

import org.crysil.logging.Logger;

public abstract class UnlimitedKeyLengthUnlocker {

  public static boolean tryUnlock() {
    final Provider[] providers = Security.getProviders();
    Logger.info("Available Security Providers:");
    for(final Provider p: providers){
      Logger.info("{}: {}",p.getName(), p.getInfo());
    }
    if (!isRestricted()) {
      Logger.info("Unlimited strength policies present, resuming normal operation");
      return true;
    }
    try {
      Logger.info("JCE strength is limited, trying to work around the limitations...");
      final Class<?> jce = Class.forName("javax.crypto.JceSecurity");

      final Field restricted = getFieldFromClass(jce, "isRestricted");
      final Field defPolicy = getFieldFromClass(jce, "defaultPolicy");
      final Field perms = getFieldFromClass(Class.forName("javax.crypto.CryptoPermissions"), "perms");
      final Field cryptoAllInstance = getFieldFromClass(Class.forName("javax.crypto.CryptoAllPermission"), "INSTANCE");
      final PermissionCollection coll = (PermissionCollection) defPolicy.get(null);

      restricted.set(null, false);
      ((Map<?, ?>) perms.get(coll)).clear();
      coll.add((Permission) cryptoAllInstance.get(null));

    } catch (final Exception e) {
      return false;
    }
    return true;
  }

  private static boolean isRestricted() {
    try {
      return Cipher.getMaxAllowedKeyLength("AES") < 256;
    } catch (final NoSuchAlgorithmException e) {
      return true;
    }
  }

  private static Field getFieldFromClass(final Class<?> clazz, final String fieldName) throws NoSuchFieldException,
      SecurityException {
    final Field declaredField = clazz.getDeclaredField(fieldName);
    declaredField.setAccessible(true);
    return declaredField;
  }
}

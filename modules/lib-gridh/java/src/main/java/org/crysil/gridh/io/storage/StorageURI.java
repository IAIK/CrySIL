package org.crysil.gridh.io.storage;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.crysil.logging.Logger;

public abstract class StorageURI {

  protected static final String        PREFIX   = null;

  protected String                     uri;

  protected final String               prefix_id;

  private final String                 prefix_scheme;

  private static Map<String, Class<?>> registry = new HashMap<String, Class<?>>();

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static <T extends StorageURI> T createFromUri(final String uri) throws NoSuchMethodException, SecurityException,
      InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    final Set<String> keySet = registry.keySet();
    for (final String prefix : keySet) {
      if (uri.startsWith(prefix)) {
        final Class uriClass = registry.get(prefix);
        final Constructor constructor = uriClass.getDeclaredConstructor(new Class[] { String.class });
        return (T) constructor.newInstance(uri);
      }
    }
    throw new InstantiationException("Type info not set!");
  }

  public static <T extends StorageURI> void register(final Class<T> uri) throws IllegalAccessException, NoSuchFieldException {
    String prefix;
    try {
      prefix = (String) uri.getDeclaredField("PREFIX").get(null);
    } catch (final IllegalArgumentException e) {
      throw new IllegalAccessException(e.getLocalizedMessage());
    } catch (final SecurityException e) {
      throw new IllegalAccessException(e.getLocalizedMessage());
    }
    if (prefix == null) {
      throw new IllegalAccessException("Class " + uri.getName() + " does not redefine a public String field PREFIX for URI matching");
    }
    Logger.info("Registered {} URI Support", prefix);
    registry.put(prefix, uri);
  }

  public StorageURI(final String uri) throws InstantiationException {
    throw new InstantiationException("You must override, but never call this constructor!");
  }

  protected StorageURI(final String prefix_id, final String prefix_scheme, final String uri) {
    this.uri = uri;
    this.prefix_id = prefix_id + "/";
    this.prefix_scheme = prefix_scheme;
    if (this.uri.startsWith(this.prefix_scheme)) {
      this.uri = this.prefix_id + uri.substring(this.prefix_scheme.length());
    }
    if (!(this.uri.startsWith(this.prefix_id))) {
      throw new IllegalArgumentException("URI does neither start with " + this.prefix_id + " nor " + this.prefix_scheme);
    }
    Logger.debug(getClass().toString());
  }

  @Override
  public String toString() {
    return uri;
  }

  public String getSchemeURI() {
    return this.prefix_scheme + uri.substring(prefix_id.length());
  }

  public String getFileIdentifier() {
    return uri.substring(prefix_id.length(), uri.length());
  }

  public abstract <V extends StorageURI, T extends StorageInputStream<V>> T createInputStream() throws IOException;

}

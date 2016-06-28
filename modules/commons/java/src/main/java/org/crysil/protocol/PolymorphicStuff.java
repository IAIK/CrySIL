package org.crysil.protocol;

/**
 * Tags classes as polymorphic. Yes, this is a kind of bad workaround to ge
 * GSon to serialize and deserialize into and given JSON.
 */
public abstract class PolymorphicStuff {
  /**
   * This is the all-important type field. Based on this very field, we can
   * recreate the POJO type.
   */
  protected final String type;

  /**
   * enforce setting the type in subclasses
   */
  public PolymorphicStuff() {
    type = getType();
  }

  /**
   * Returns the appropriate type of a certain data block.
   */
  public abstract String getType();

  @Override
  public abstract int hashCode() ;

}

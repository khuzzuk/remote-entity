package pl.javahello.entity.remote.test;

import lombok.Data;

@Data
public class BaseClass {
  private byte byteInheritedField;
//  private short shortInheritedField;
  private int intInheritedField;
  private long longInheritedField;
  private float floatInheritedField;
  private double doubleInheritedField;
  private String stringInheritedField;

  private InternalType internalInheritedField;

}

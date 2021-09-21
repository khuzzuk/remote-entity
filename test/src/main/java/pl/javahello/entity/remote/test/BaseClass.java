package pl.javahello.entity.remote.test;

import lombok.Data;
import pl.javahello.entity.remote.test.MainEntity.InternalType;

import java.util.UUID;

@Data
public class BaseClass {
  private long id;
  private UUID uuid;
  private int version;
  private byte byteInheritedField;
  private short shortInheritedField;
  private int intInheritedField;
  private long longInheritedField;
  private float floatInheritedField;
  private double doubleInheritedField;
  private String stringInheritedField;

  private InternalType internalInheritedField;

}

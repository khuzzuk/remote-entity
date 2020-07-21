package pl.javahello.entity.remote.test;

import lombok.Data;
import pl.javahello.RemoteEntity;

@Data
@RemoteEntity
public class NestedRemoteEntity {

  private byte byteField;
  private short shortField;
  private int intField;
  private long longField;
  private float floatField;
  private double doubleField;
  private String stringField;
}

package pl.javahello.entity.remote.test;

import lombok.Data;
import pl.javahello.DTO;
import pl.javahello.entity.remote.test.MainEntity.InternalType;

@DTO
@Data
public class InternalDataTransferObject {
  private byte byteField;
  private short shortField;
  private int intField;
  private long longField;
  private float floatField;
  private double doubleField;
  private String stringField;

  private InternalType internalTypeField;
}

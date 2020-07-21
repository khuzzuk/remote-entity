package pl.javahello.entity.remote.test;

import javax.validation.constraints.Min;
import lombok.Data;
import pl.javahello.RemoteEntity;
import pl.javahello.RemoteEntity.SecuredService;

@Data
@RemoteEntity(transactional = true, requestMapping = "customMapping")
@SecuredService(allowRead = true)
public class MainEntity extends BaseClass {
  private byte byteField;
  private short shortField;
  private @Min(0) int intField;
  private long longField;
  private float floatField;
  private double doubleField;
  private String stringField;
  private InternalType internalField;
  private InternalDataTransferObject internalDataTransferObjectField;
  private NestedRemoteEntity nestedRemoteEntityField;
  private TypeContainsDefaultName_int typeContainsDefaultNameField;
}

# Remote Entity
Remote Entity is a Java [annotation processor](http://docs.oracle.com/javase/6/docs/technotes/guides/apt/index.html)
for the generation classes based on commonly used patterns.
It is intended for use with Spring Data JPA, Spring MVC/Webflux, Lombok and Mapstruct.

Having entity class:
```java
@Getter
@Setter
@Entity
@RemoteEntity(transactional = true)
public class MyClass {

    @Id
    private long id;
    private String property;
}
```
It generates following classes:
* DTO
```java
public class MyClassDTO {
    private long id;
    private String property;
    
    public long getId() {
      return id;
    }
    public String getProperty() {
      return property;
    }

    public void setId(long id) {
      this.id = id;
    }
    public void setProperty(String property) {
      this.property = property;
    }
}
```
* Adapters for mapstruct
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MyClassAdapter extends Adapter<MyClassDTO, MyClass> {}
```
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MyClassDTOAdapter extends Adapter<MyClass, MyClassDTO> {}
```
* Repository
```java
@Repository
public interface MyClassRepo extends JpaRepository<MyClass, Long> {}
```
* Controller
```java
@RestController
@RequestMapping("myClass")
@Transactional
public class MyClassService implements RemoteService<MyClassDTO> {
  private myClassRepo myClassRepo;
  private Adapter<MyClassDTO, MyClass> myClassAdapter;
  private Adapter<MyClass, MyClassDTO> myClassDTOAdapter;

  public MyClassService(MyClassRepo myClassRepo, 
                        Adapter<myClassDTO, myClass> myClassAdapter, 
                        Adapter<myClass, myClassDTO> myClassDTOAdapter) { 
    this.myClassRepo = myClassRepo; 
    this.myClassAdapter = myClassAdapter; 
    this.myClassDTOAdapter = myClassDTOAdapter; 
  }

  @GetMapping
  @Override
  public List<myClassDTO> findAll() { 
    return myClassDTOAdapter.list(myClassRepo.findAll()); 
  }

  @PostMapping
  @Override
  public myClassDTO save(@Valid @RequestBody myClassDTO myClassDTO) {
    myClass myClass = myClassAdapter.map(myClassDTO);
    myClass persisted = myClassRepo.save(myClass);
    return myClassDTOAdapter.map(persisted);
  }

  @DeleteMapping
  @Override
  public void delete(@Valid @RequestBody myClassDTO myClassDTO) {
    myClass entity = myClassAdapter.map(myClassDTO);
    myClassRepo.delete(entity);
  }
}
```

If Entity has property of some custom class, it should be annotated with @DTO annotation for full support of remote entity generated classes.
The DTO annotation triggers generation of DTO and adapters classes only.
package pl.javahello.processor;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import org.apache.commons.lang3.StringUtils;
import pl.javahello.common.AnnotationTypeUtils;

public class RemoteServiceGenerator extends AbstractFileGenerator {

  private static final String REMOTE_ENTITY_ANNOTATION = "pl.javahello.RemoteEntity";
  private static final String SECURED_ANNOTATION =
      "org.springframework.security.access.annotation.Secured";
  private static final String TRANSACTIONAL_ANNOTATION = "javax.transaction.Transactional";
  private static final String SECURED_SERVICE_ANNOTATION =
      "pl.javahello.RemoteEntity.SecuredService";
  private static final Set<String> BASIC_IMPORTS = Set.of(
      "org.springframework.web.bind.annotation.DeleteMapping",
      "org.springframework.web.bind.annotation.GetMapping",
      "org.springframework.web.bind.annotation.PostMapping",
      "org.springframework.web.bind.annotation.RequestBody",
      "org.springframework.web.bind.annotation.RequestMapping",
      "org.springframework.web.bind.annotation.RestController",
      "pl.javahello.RemoteService",
      "pl.javahello.Adapter",
      "javax.validation.Valid",
      "java.util.List");
  private static final Set<String> SECURITY_IMPORT = Set.of(SECURED_ANNOTATION);

  RemoteServiceGenerator(RoundEnvironment roundEnv,
                         SourceFileDescription sourceFileDescription,
                         ProcessingEnvironment processingEnvironment) {
    super(roundEnv, sourceFileDescription, processingEnvironment);
  }

  @Override
  String getGeneratedClassName() {
    return sourceFileDescription.getPackageElement().getQualifiedName() +
           "." +
           sourceFileDescription.getElement().getSimpleName() +
           "Service";
  }

  @Override
  void generateContent(PrintWriter writer) {
    String entityName = sourceFileDescription.getElement().getSimpleName().toString();
    String beanName = StringUtils.uncapitalize(entityName);
    AnnotationMirror remoteAnnotation =
        AnnotationTypeUtils.getAnnotation(sourceFileDescription.getElement(),
                                          REMOTE_ENTITY_ANNOTATION);
    Optional<Boolean> transactional =
        AnnotationTypeUtils.getBooleanValue(remoteAnnotation, "transactional")
                           .filter(Boolean::booleanValue);

    printPackage(writer, sourceFileDescription.getPackageElement().getQualifiedName().toString());
    printImports(writer, BASIC_IMPORTS);
    transactional.ifPresent(any -> printImports(writer, TRANSACTIONAL_ANNOTATION));

    Optional<AnnotationMirror> securedAnnotation;
    String securedAnnotationText;
    if (AnnotationTypeUtils.hasAnnotation(sourceFileDescription.getElement(),
                                          SECURED_SERVICE_ANNOTATION)) {
      printImports(writer, SECURED_ANNOTATION);

      final AnnotationMirror securedAnnotationMirror =
          AnnotationTypeUtils.getAnnotation(sourceFileDescription.getElement(),
                                            SECURED_SERVICE_ANNOTATION);
      securedAnnotation = Optional.of(securedAnnotationMirror);
      securedAnnotationText = String.format("  @Secured(%s)",
                                            AnnotationTypeUtils.getStringValue(
                                                securedAnnotationMirror,
                                                "role")
                                                               .orElse("\"ROLE_" +
                                                                       beanName.toUpperCase() +
                                                                       "\""));
    } else {
      securedAnnotation = Optional.empty();
      securedAnnotationText = "";
    }

    writer.println("@RestController");
    writer.println(String.format("@RequestMapping(%s)",
                                 AnnotationTypeUtils.getStringValue(remoteAnnotation,
                                                                    "requestMapping")
                                                    .orElse("\"" + beanName + "\"")));
    transactional.ifPresent(any -> writer.println("@Transactional"));

    writer.println(String.format("public class %sService implements RemoteService<%sDTO> {", entityName, entityName));

    writer.println(String.format("  private %sRepo %sRepo;", entityName, beanName));
    writer.println(String.format("  private Adapter<%sDTO, %s> %sAdapter;", entityName, entityName, beanName));
    writer.println(String.format("  private Adapter<%s, %sDTO> %sDTOAdapter;", entityName, entityName, beanName));

    writer.println(String.format(
        "  public %sService(%sRepo %sRepo, Adapter<%sDTO, %s> %sAdapter, Adapter<%s, %sDTO> %sDTOAdapter) { ",
        entityName, entityName, beanName, entityName, entityName, beanName, entityName, entityName, beanName));
    writer.println(String.format("    this.%sRepo = %sRepo; ", beanName, beanName));
    writer.println(String.format("    this.%sAdapter = %sAdapter; ", beanName, beanName));
    writer.println(String.format("    this.%sDTOAdapter = %sDTOAdapter; ", beanName, beanName));
    writer.println(("  }"));
    writer.println();

    securedAnnotation.filter(a -> !AnnotationTypeUtils.getBooleanValue(a, "allowRead").orElse(false))
                     .ifPresent(a -> writer.println(securedAnnotationText));
    writer.println("  @GetMapping");
    writer.println("  @Override");
    writer.println(String.format("  public List<%sDTO> findAll() { ", entityName));
    writer.println(String.format("    return %sDTOAdapter.list(%sRepo.findAll()); ", beanName, beanName));
    writer.println("  }");

    writer.println(securedAnnotationText);
    writer.println("  @PostMapping");
    writer.println("  @Override");
    writer.println(String.format("  public %sDTO save(@Valid @RequestBody %sDTO %sDTO) {", entityName, entityName, beanName));
    writer.println(String.format("    %s %s = %sAdapter.map(%sDTO);", entityName, beanName, beanName, beanName));
    writer.println(String.format("    %s persisted = %sRepo.save(%s);", entityName, beanName, beanName));
    writer.println(String.format("    return %sDTOAdapter.map(persisted);", beanName));
    writer.println("  }");

    writer.println(securedAnnotationText);
    writer.println("  @DeleteMapping");
    writer.println("  @Override");
    writer.println(String.format("  public void delete(@Valid @RequestBody %sDTO %sDTO) {", entityName, beanName));
    writer.println(String.format("    %s entity = %sAdapter.map(%sDTO);", entityName, beanName, beanName));
    writer.println(String.format("    %sRepo.delete(entity);", beanName));
    writer.println("  }");

    writer.println("}");
  }
}

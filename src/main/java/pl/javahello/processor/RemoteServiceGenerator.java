package pl.javahello.processor;

import static java.lang.String.format;
import static pl.javahello.common.AnnotationTypeUtils.getAnnotation;
import static pl.javahello.common.AnnotationTypeUtils.getStringValue;
import static pl.javahello.common.AnnotationTypeUtils.hasAnnotation;
import static pl.javahello.common.AnnotationTypeUtils.isEnabled;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
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
  private static final String STOMP_IMPORT = "org.springframework.messaging.simp.SimpMessagingTemplate";
  private static final Set<String> BASIC_IMPORTS = Set.of(
      "org.springframework.web.bind.annotation.DeleteMapping",
      "org.springframework.web.bind.annotation.GetMapping",
      "org.springframework.web.bind.annotation.PostMapping",
      "org.springframework.web.bind.annotation.RequestBody",
      "org.springframework.web.bind.annotation.RequestMapping",
      "org.springframework.web.bind.annotation.RestController",
      "lombok.RequiredArgsConstructor",
      "pl.javahello.RemoteService",
      "pl.javahello.Adapter",
      "javax.validation.Valid",
      "java.util.List");
  private static final Set<String> SECURITY_IMPORT = Set.of(SECURED_ANNOTATION);
  public static final String STOMP_FEATURE = "stomp";
  public static final String STOMP_TOPIC_FEATURE = "stompTopic";

  RemoteServiceGenerator(SourceFileDescription sourceFileDescription,
                         ProcessingEnvironment processingEnvironment) {
    super(sourceFileDescription, processingEnvironment);
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
        getAnnotation(sourceFileDescription.getElement(),
                                          REMOTE_ENTITY_ANNOTATION);
    Optional<Boolean> transactional =
        AnnotationTypeUtils.getBooleanValue(remoteAnnotation, "transactional")
                           .filter(Boolean::booleanValue);

    printPackage(writer, sourceFileDescription.getPackageElement().getQualifiedName().toString());
    printImports(writer, BASIC_IMPORTS);
    transactional.ifPresent(any -> printImports(writer, TRANSACTIONAL_ANNOTATION));
    if (isEnabled(remoteAnnotation, STOMP_FEATURE)) {
      printImports(writer, STOMP_IMPORT);
    }

    Optional<AnnotationMirror> securedAnnotation;
    String securedAnnotationText;
    if (hasAnnotation(sourceFileDescription.getElement(), SECURED_SERVICE_ANNOTATION)) {
      printImports(writer, SECURED_ANNOTATION);

      AnnotationMirror securedAnnotationMirror = getAnnotation(sourceFileDescription.getElement(), SECURED_SERVICE_ANNOTATION);
      securedAnnotation = Optional.of(securedAnnotationMirror);
      securedAnnotationText = format("  @Secured(%s)",
          getStringValue(securedAnnotationMirror, "role").orElse("\"ROLE_" + beanName.toUpperCase() + "\""));
    } else {
      securedAnnotation = Optional.empty();
      securedAnnotationText = "";
    }

    writer.println("@RequiredArgsConstructor");
    writer.println("@RestController");
    writer.println(format("@RequestMapping(%s)", getStringValue(remoteAnnotation, "requestMapping").orElse("\"" + beanName + "\"")));
    transactional.ifPresent(any -> writer.println("@Transactional"));

    writer.println(format("public class %sService implements RemoteService<%sDTO> {", entityName, entityName));

    writer.println(format("  private final %sRepo %sRepo;", entityName, beanName));
    writer.println(format("  private final Adapter<%1$sDTO, %1$s> %2$sAdapter;", entityName, beanName));
    writer.println(format("  private final Adapter<%1$s, %1$sDTO> %2$sDTOAdapter;", entityName, beanName));
    if (isEnabled(remoteAnnotation, STOMP_FEATURE)) {
      writer.println("  private final SimpMessagingTemplate template;");
    }

    securedAnnotation.filter(a -> !AnnotationTypeUtils.getBooleanValue(a, "allowRead").orElse(false))
                     .ifPresent(a -> writer.println(securedAnnotationText));
    writer.println("  @GetMapping");
    writer.println("  @Override");
    writer.println(format("  public List<%sDTO> findAll() { ", entityName));
    writer.println(format("    return %sDTOAdapter.list(%sRepo.findAll()); ", beanName, beanName));
    writer.println("  }");

    writer.println(securedAnnotationText);
    writer.println("  @PostMapping");
    writer.println("  @Override");
    writer.println(format("  public %1$sDTO save(@Valid @RequestBody %1$sDTO %2$sDTO) {", entityName, beanName));
    writer.println(format("    %1$s %2$s = %2$sAdapter.map(%2$sDTO);", entityName, beanName));
    writer.println(format("    %1$s persisted = %2$sRepo.save(%2$s);", entityName, beanName));
    if (isEnabled(remoteAnnotation, STOMP_FEATURE)) {
      String topic = getStringValue(remoteAnnotation, STOMP_TOPIC_FEATURE).orElse(beanName);
      writer.println(format("    template.convertAndSend(\"%1$s\", %2$sDTOAdapter.map(persisted));", topic, beanName));
    }
    writer.println(format("    return %sDTOAdapter.map(persisted);", beanName));
    writer.println("  }");

    writer.println(securedAnnotationText);
    writer.println("  @DeleteMapping");
    writer.println("  @Override");
    writer.println(format("  public void delete(@Valid @RequestBody %sDTO %sDTO) {", entityName, beanName));
    writer.println(format("    %s entity = %sAdapter.map(%sDTO);", entityName, beanName, beanName));
    writer.println(format("    %sRepo.delete(entity);", beanName));
    if (isEnabled(remoteAnnotation, STOMP_FEATURE)) {
      String topic = getStringValue(remoteAnnotation, STOMP_TOPIC_FEATURE).orElse(beanName) + "/deleted";
      writer.println(format("    template.convertAndSend(\"%1$s\", %2$sDTO);", topic, beanName));
    }
    writer.println("  }");

    writer.println("}");
  }
}

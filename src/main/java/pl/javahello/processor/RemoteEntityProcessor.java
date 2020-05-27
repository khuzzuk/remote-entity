package pl.javahello.processor;

import com.google.auto.service.AutoService;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Processor for {@link pl.javahello.RemoteEntity} annotation.
 */
@SupportedAnnotationTypes({"pl.javahello.RemoteEntity"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class RemoteEntityProcessor extends AbstractProcessor {

  private long recordTime;
  private long totalRecordTime;

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);

      for (Element element : elements) {
        SourceFileDescription sourceFileDescription =
            SourceFileDescription.create(element, processingEnv);

        new JpaRepoGenerator(roundEnv, sourceFileDescription, processingEnv).writeFile();
        new DtoGenerator(roundEnv, sourceFileDescription, processingEnv).writeFile();
        new AdapterToDtoGenerator(roundEnv, sourceFileDescription, processingEnv).writeFile();
        new AdapterToEntityGenerator(roundEnv, sourceFileDescription, processingEnv).writeFile();
        new RemoteServiceGenerator(roundEnv, sourceFileDescription, processingEnv).writeFile();
      }

      return !elements.isEmpty();
    }

    return false;
  }
}

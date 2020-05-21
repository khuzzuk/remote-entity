package pl.javahello.processor;

import com.google.auto.service.AutoService;
import java.time.LocalDateTime;
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
@SupportedAnnotationTypes({"pl.khuzzuk.remote.RemoteEntity"})
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
        System.out.println("Processing RemoteEntity: " + element.getSimpleName());
        startRecord();

        SourceFileDescription sourceFileDescription =
            SourceFileDescription.create(element, processingEnv);

        new JpaRepoGenerator(roundEnv, sourceFileDescription, processingEnv).writeFile();
        reportRecord("JpaRepoGenerator");

        new DtoGenerator(roundEnv, sourceFileDescription, processingEnv).writeFile();
        reportRecord("DtoGenerator");

        new AdapterToDtoGenerator(roundEnv, sourceFileDescription, processingEnv).writeFile();
        new AdapterToEntityGenerator(roundEnv, sourceFileDescription, processingEnv).writeFile();
        reportRecord("AdapterGenerators");

        new RemoteServiceGenerator(roundEnv, sourceFileDescription, processingEnv).writeFile();
        reportRecord("RemoteServiceGenerator");

        finishRecord();
      }

      return !elements.isEmpty();
    }

    return false;
  }

  private void startRecord() {
    recordTime = System.currentTimeMillis();
    totalRecordTime = recordTime;
  }

  private void reportRecord(String name) {
    System.out.println(String.format("%s\tfinished RemoteEntity, %s time: %s ms",
                                     LocalDateTime.now(),
                                     name,
                                     System.currentTimeMillis() - recordTime));
    recordTime = System.currentTimeMillis();
  }

  private void finishRecord() {
    System.out.println(String.format("%s\tfinished RemoteEntity, overall time: %s ms",
                                     LocalDateTime.now(),
                                     System.currentTimeMillis() - totalRecordTime));
  }
}

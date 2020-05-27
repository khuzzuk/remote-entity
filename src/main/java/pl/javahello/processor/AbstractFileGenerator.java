package pl.javahello.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

abstract class AbstractFileGenerator {

  private static final Set<String> DEFAULT_TYPES = Set.of(int.class.getCanonicalName(),
                                                          int[].class.getCanonicalName(),
                                                          Integer.class.getCanonicalName(),
                                                          Integer[].class.getCanonicalName(),
                                                          long.class.getCanonicalName(),
                                                          long[].class.getCanonicalName(),
                                                          Long.class.getCanonicalName(),
                                                          Long[].class.getCanonicalName(),
                                                          float.class.getCanonicalName(),
                                                          float[].class.getCanonicalName(),
                                                          Float.class.getCanonicalName(),
                                                          Float[].class.getCanonicalName(),
                                                          double.class.getCanonicalName(),
                                                          double[].class.getCanonicalName(),
                                                          Double.class.getCanonicalName(),
                                                          Double[].class.getCanonicalName(),
                                                          boolean.class.getCanonicalName(),
                                                          boolean[].class.getCanonicalName(),
                                                          Boolean.class.getCanonicalName(),
                                                          Boolean[].class.getCanonicalName(),
                                                          byte.class.getCanonicalName(),
                                                          byte[].class.getCanonicalName(),
                                                          Byte.class.getCanonicalName(),
                                                          Byte[].class.getCanonicalName(),
                                                          String.class.getCanonicalName(),
                                                          String[].class.getCanonicalName(),
                                                          Date.class.getCanonicalName(),
                                                          java.sql.Date.class.getCanonicalName(),
                                                          Timestamp.class.getCanonicalName(),
                                                          Time.class.getCanonicalName());

  SourceFileDescription sourceFileDescription;
  ProcessingEnvironment processingEnvironment;
  private RoundEnvironment roundEnv;

  AbstractFileGenerator(RoundEnvironment roundEnv,
                        SourceFileDescription sourceFileDescription,
                        ProcessingEnvironment processingEnvironment) {
    this.roundEnv = roundEnv;
    this.sourceFileDescription = sourceFileDescription;
    this.processingEnvironment = processingEnvironment;
  }

  static boolean isEntity(Element field) {
    if (field.asType().getKind().equals(TypeKind.DECLARED)) {
      DeclaredType type = (DeclaredType) field.asType();
      if (type.asElement().getKind().equals(ElementKind.ENUM)) {
        return false;
      }
    }

    String fieldType = field.asType().toString();
    return DEFAULT_TYPES.stream().noneMatch(fieldType::endsWith);
  }

  void writeFile() {
    try (PrintWriter writer = new PrintWriter(processingEnvironment.getFiler()
                                                                   .createSourceFile(
                                                                       getGeneratedClassName())
                                                                   .openWriter())) {
      generateContent(writer);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * returns class name to use as a file name on classpath, i.e. it has to contain full package name
   * separated by dots.
   *
   * @return class name with full package separated with dots.
   */
  abstract String getGeneratedClassName();

  abstract void generateContent(PrintWriter printWriter);

  void printPackage(PrintWriter printWriter, String packageName) {
    printWriter.println(String.format("package %s;", packageName));
    printWriter.println();
  }

  void printImports(PrintWriter printWriter, String... imports) {
    for (String importPackage : imports) {
      printWriter.println(String.format("import %s;", importPackage));
    }
    printWriter.println();
  }

  void printImports(PrintWriter printWriter, Iterable<String> imports) {
    for (String importPackage : imports) {
      printWriter.println(String.format("import %s;", importPackage));
    }
    printWriter.println();
  }

}

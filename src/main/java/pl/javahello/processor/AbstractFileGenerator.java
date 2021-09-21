package pl.javahello.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

abstract class AbstractFileGenerator {

  private static final Set<String> DEFAULT_TYPES = Set.of(int[].class.getCanonicalName(),
                                                          Integer.class.getCanonicalName(),
                                                          Integer[].class.getCanonicalName(),
                                                          long[].class.getCanonicalName(),
                                                          Long.class.getCanonicalName(),
                                                          Long[].class.getCanonicalName(),
                                                          float[].class.getCanonicalName(),
                                                          Float.class.getCanonicalName(),
                                                          Float[].class.getCanonicalName(),
                                                          double[].class.getCanonicalName(),
                                                          Double.class.getCanonicalName(),
                                                          Double[].class.getCanonicalName(),
                                                          boolean[].class.getCanonicalName(),
                                                          Boolean.class.getCanonicalName(),
                                                          Boolean[].class.getCanonicalName(),
                                                          short[].class.getCanonicalName(),
                                                          Short.class.getCanonicalName(),
                                                          Short[].class.getCanonicalName(),
                                                          byte[].class.getCanonicalName(),
                                                          Byte.class.getCanonicalName(),
                                                          Byte[].class.getCanonicalName(),
                                                          String.class.getCanonicalName(),
                                                          String[].class.getCanonicalName(),

                                                          // java.util
                                                          Date.class.getCanonicalName(),
                                                          java.sql.Date.class.getCanonicalName(),
                                                          Timestamp.class.getCanonicalName(),
                                                          Time.class.getCanonicalName(),
                                                          UUID.class.getCanonicalName(),

                                                          // java.time
                                                          DayOfWeek.class.getCanonicalName(),
                                                          Month.class.getCanonicalName(),
                                                          LocalDate.class.getCanonicalName(),
                                                          LocalDateTime.class.getCanonicalName(),
                                                          LocalTime.class.getCanonicalName(),
                                                          ZonedDateTime.class.getCanonicalName(),
                                                          OffsetDateTime.class.getCanonicalName(),
                                                          OffsetTime.class.getCanonicalName(),
                                                          Year.class.getCanonicalName(),
                                                          YearMonth.class.getCanonicalName(),
                                                          Instant.class.getCanonicalName(),
                                                          Period.class.getCanonicalName(),
                                                          Duration.class.getCanonicalName());

  SourceFileDescription sourceFileDescription;
  ProcessingEnvironment processingEnvironment;

  AbstractFileGenerator(SourceFileDescription sourceFileDescription,
                        ProcessingEnvironment processingEnvironment) {
    this.sourceFileDescription = sourceFileDescription;
    this.processingEnvironment = processingEnvironment;
  }

  static boolean isEntity(Element field) {
    TypeKind fieldTypeKind = field.asType().getKind();

    if (fieldTypeKind.isPrimitive()) {
      return false;
    }

    if (fieldTypeKind == TypeKind.DECLARED) {
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

package pl.javahello.common;

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
import javax.lang.model.element.Element;

public class TypeUtils {

  private static Set<String> LANG_TYPES = Set.of(Integer.class.getCanonicalName(),
                                                 Long.class.getCanonicalName(),
                                                 Short.class.getCanonicalName(),
                                                 Byte.class.getCanonicalName(),
                                                 Float.class.getCanonicalName(),
                                                 Double.class.getCanonicalName(),
                                                 String.class.getCanonicalName(),
                                                 Integer[].class.getCanonicalName(),
                                                 int[].class.getCanonicalName(),
                                                 Long[].class.getCanonicalName(),
                                                 long[].class.getCanonicalName(),
                                                 Short[].class.getCanonicalName(),
                                                 short[].class.getCanonicalName(),
                                                 Byte[].class.getCanonicalName(),
                                                 byte[].class.getCanonicalName(),
                                                 Float[].class.getCanonicalName(),
                                                 float[].class.getCanonicalName(),
                                                 Double[].class.getCanonicalName(),
                                                 double[].class.getCanonicalName(),
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

  public static boolean isJavaLangType(Element field) {
    String fieldTypeDeclaration = field.asType().toString();
    return LANG_TYPES.stream().anyMatch(fieldTypeDeclaration::endsWith);
  }

  public static boolean isInternalClass(Element type) {
    return type.getEnclosingElement().getKind().isClass();
  }

  public static String enclosingClassName(Element type) {
    return type.getEnclosingElement().getSimpleName().toString();
  }

  public static String enclosingClassPackage(Element type) {
    return type.getEnclosingElement().getEnclosingElement().toString();
  }
}

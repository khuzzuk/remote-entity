package pl.javahello;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark class to generate DTO with corresponding {@link Adapter}s.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DTO {

  /**
   * Annotated field will be excluded from DTO class.
   */
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.SOURCE)
  @interface Exclude {}
}

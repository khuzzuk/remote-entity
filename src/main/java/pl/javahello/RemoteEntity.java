package pl.javahello;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark class as entity for processor to generate controller, repo, and adapters.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface RemoteEntity {

  /**
   * Defines http path for @RequestMapping annotation, by default it will use bean name (simple
   * class name with lower case first letter).
   */
  String requestMapping() default "";

  /**
   * Adds @javax.Transactional annotation to Service.
   */
  boolean transactional() default false;

  /**
   * Together with {@link RemoteEntity} adds @Secured annotation to Controller.
   */
  @Retention(RetentionPolicy.SOURCE)
  @Target(ElementType.TYPE)
  @interface SecuredService {

    /**
     * Allows defining role for @Secured annotation. By default it will use pattern
     * "ROLE_{CLASSNAME}".
     */
    String role() default "";

    /**
     * If true, @GetMapping controller method won't have @Secured annotation.
     */
    boolean allowRead() default false;
  }
}

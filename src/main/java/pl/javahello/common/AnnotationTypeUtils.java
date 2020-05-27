package pl.javahello.common;

import java.util.Map.Entry;
import java.util.Optional;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public class AnnotationTypeUtils {

  public static boolean hasAnnotation(Element element, String annotationName) {
    for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
      String annotationValue = annotation.toString();
      if (annotationValue.contains(annotationName)) {
        return true;
      }
    }
    return false;
  }

  public static AnnotationMirror getAnnotation(Element element, String annotationName) {
    for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
      String annotationValue = annotation.toString();
      if (annotationValue.contains(annotationName)) {
        return annotation;
      }
    }
    throw new IllegalArgumentException(String.format("Annotation %s not found in %s",
                                                     annotationName,
                                                     element));
  }

  public static Optional<String> getStringValue(AnnotationMirror annotation, String name) {
    return annotation.getElementValues()
                     .entrySet()
                     .stream()
                     .filter(entry -> entry.getKey().getSimpleName().contentEquals(name))
                     .findAny()
                     .map(Entry::getValue)
                     .map(Object::toString);
  }

  public static Optional<Boolean> getBooleanValue(AnnotationMirror annotation, String name) {
    return getStringValue(annotation, name).map(Boolean::valueOf);
  }
}

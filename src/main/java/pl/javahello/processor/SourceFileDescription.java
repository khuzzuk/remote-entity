package pl.javahello.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import pl.javahello.DTO;
import pl.javahello.common.AnnotationTypeUtils;
import pl.javahello.common.CollectionTypeUtils;
import pl.javahello.common.TypeUtils;

class SourceFileDescription {

  private Element element;
  private PackageElement packageElement;
  private List<? extends Element> members = List.of();
  private List<Element> fields = List.of();

  static SourceFileDescription create(Element element,
                                      ProcessingEnvironment processingEnvironment) {
    SourceFileDescription sourceFileDescription = new SourceFileDescription();
    sourceFileDescription.element = element;
    sourceFileDescription.packageElement = processingEnvironment.getElementUtils()
        .getPackageOf(element);
    sourceFileDescription.members = processingEnvironment.getElementUtils()
        .getAllMembers((TypeElement) element);
    sourceFileDescription.fields = new ArrayList<>();

    sourceFileDescription.getFields((Collection<Element>) sourceFileDescription.members)
        .forEach(sourceFileDescription.fields::add);

    TypeElement objectType = processingEnvironment.getElementUtils()
        .getTypeElement("java.lang.Object");

    List<? extends TypeMirror> supertypes = processingEnvironment.getTypeUtils()
        .directSupertypes(element.asType());
    supertypes.forEach(type -> sourceFileDescription.fillSuperTypeFields(type,
                                                                         processingEnvironment,
                                                                         objectType));

    return sourceFileDescription;
  }

  private void fillSuperTypeFields(TypeMirror typeMirror,
                                   ProcessingEnvironment processingEnvironment,
                                   TypeElement objectType) {
    if (!typeMirror.equals(objectType.asType())) {
      DeclaredType declaredType = (DeclaredType) typeMirror;
      Element superElement = processingEnvironment.getTypeUtils().asElement(typeMirror);
      List<Element> superMemebers = (List<Element>) processingEnvironment.getElementUtils()
          .getAllMembers((TypeElement) superElement);
      getFields(superMemebers).forEach(fields::add);

      List<? extends TypeMirror> supertypes = processingEnvironment.getTypeUtils()
          .directSupertypes(declaredType.asElement().asType());
      supertypes.forEach(type -> fillSuperTypeFields(type, processingEnvironment, objectType));
    }
  }

  private Stream<Element> getFields(Collection<Element> elements) {
    return elements.stream().filter(el -> ElementKind.FIELD == el.getKind());
  }

  boolean hasField(String name) {
    return getFields().stream()
        .map(Element::getSimpleName)
        .map(Objects::toString)
        .anyMatch(name::equals);
  }

  List<Element> getFields() {
    return fields;
  }

  public boolean isEntity() {
    return AnnotationTypeUtils.hasAnnotation(element, "javax.persistence.Entity");
  }

  List<Element> getEntityWithOwnMappers(ProcessingEnvironment processingEnvironment) {
    List<Element> entities = new ArrayList<>();

    for (Element field : fields) {
        if (field.asType().getKind().isPrimitive()) continue;
        if (TypeUtils.isJavaLangType(field)) continue;
        if (field.getAnnotation(DTO.Exclude.class) != null) continue;

        Element fieldType = CollectionTypeUtils.isFieldCollection(field)
                            ? CollectionTypeUtils.getTypeFromCollectionField(field,
                                                                             processingEnvironment)
                            : processingEnvironment.getTypeUtils().asElement(field.asType());

        if (AnnotationTypeUtils.hasAnnotation(fieldType, "pl.javahello.RemoteEntity") ||
            AnnotationTypeUtils.hasAnnotation(fieldType, "pl.javahello.DTO")) {
            entities.add(fieldType);
        }
    }

    return entities;
  }

  List<Element> getNullCheckRequiringFields() {
    List<Element> nullCheckFields = new ArrayList<>();

    fields.stream()
        .filter(field -> !field.asType().getKind().isPrimitive())
        .filter(CollectionTypeUtils::isFieldCollection)
        .forEach(nullCheckFields::add);

    fields.stream()
        .filter(field -> "uuid".equals(field.getSimpleName().toString()))
        .findAny()
        .ifPresent(nullCheckFields::add);

    return nullCheckFields;
  }

  public Element getElement() {
    return element;
  }

  public void setElement(Element element) {
    this.element = element;
  }

  public PackageElement getPackageElement() {
    return packageElement;
  }

  public void setPackageElement(PackageElement packageElement) {
    this.packageElement = packageElement;
  }

  public List<? extends Element> getMembers() {
    return members;
  }

  public void setMembers(List<? extends Element> members) {
    this.members = members;
  }

}

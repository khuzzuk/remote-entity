package pl.javahello.processor;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public abstract class AbstractAdapterGenerator extends AbstractFileGenerator {

  public AbstractAdapterGenerator(SourceFileDescription sourceFileDescription,
                                  ProcessingEnvironment processingEnvironment) {
    super(sourceFileDescription, processingEnvironment);
  }

  String getMapperDeclaration(String adapterSuffix) {
    StringBuilder mapperDeclaration = new StringBuilder(
        "@Mapper(componentModel = \"spring\", unmappedTargetPolicy = ReportingPolicy.IGNORE");

    List<Element> entityWithOwnMappers =
        sourceFileDescription.getEntityWithOwnMappers(processingEnvironment);
    if (!entityWithOwnMappers.isEmpty()) {
      mapperDeclaration.append(", uses={\n");

      String mappersToUse = entityWithOwnMappers.stream()
                                                .map(Element::asType)
                                                .map(TypeMirror::toString)
                                                .map(entityTypeName -> String.format(
                                                    "\t\t\t%s%s.class",
                                                    entityTypeName,
                                                    adapterSuffix))
                                                .collect(Collectors.joining(",\n"));
      mapperDeclaration.append(mappersToUse);

      mapperDeclaration.append("\n}");
    }

    mapperDeclaration.append(")");

    return mapperDeclaration.toString();
  }
}

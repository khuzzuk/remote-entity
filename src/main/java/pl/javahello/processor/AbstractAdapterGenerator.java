package pl.javahello.processor;

import static java.lang.String.format;
import static pl.javahello.common.TypeUtils.enclosingClassPackage;
import static pl.javahello.common.TypeUtils.isInternalClass;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

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
                                                .map(element -> isInternalClass(element)
                                                    ? format("\t\t\t%s.%s%s.class", enclosingClassPackage(element), element.getSimpleName(), adapterSuffix)
                                                    : format("\t\t\t%s%s.class", element.asType(), adapterSuffix))
                                                .collect(Collectors.joining(",\n"));
      mapperDeclaration.append(mappersToUse);

      mapperDeclaration.append("\n}");
    }

    mapperDeclaration.append(")");

    return mapperDeclaration.toString();
  }
}

package pl.javahello.processor;

import static pl.javahello.common.TypeUtils.enclosingClassName;
import static pl.javahello.common.TypeUtils.isInternalClass;

import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

class AdapterToEntityGenerator extends AbstractAdapterGenerator {

  AdapterToEntityGenerator(SourceFileDescription sourceFileDescription,
                           ProcessingEnvironment processingEnvironment) {
    super(sourceFileDescription, processingEnvironment);
  }

  @Override
  String getGeneratedClassName() {
    return sourceFileDescription.getPackageElement().getQualifiedName() +
           "." +
           sourceFileDescription.getElement().getSimpleName() +
           "Adapter";
  }

  @Override
  void generateContent(PrintWriter writer) {
    printPackage(writer, sourceFileDescription.getPackageElement().getQualifiedName().toString());

    printImports(writer,
                 "org.mapstruct.Mapper",
                 "org.mapstruct.ReportingPolicy",
                 "org.mapstruct.Mapping",
                 "org.mapstruct.NullValueCheckStrategy",
                 "pl.javahello.Adapter");

    String sourceSimpleName = sourceFileDescription.getElement().getSimpleName().toString();

    writer.println(getMapperDeclaration("Adapter"));
    if (isInternalClass(sourceFileDescription.getElement())) {
      String enclosingClassName = enclosingClassName(sourceFileDescription.getElement());
      writer.println(String.format("public interface %1$sAdapter extends Adapter<%1$sDTO, %2$s.%1$s> {", sourceSimpleName, enclosingClassName));
    } else {
      writer.println(String.format("public interface %1$sAdapter extends Adapter<%1$sDTO, %1$s> {", sourceSimpleName));
    }

    List<String> mappings = sourceFileDescription.getNullCheckRequiringFields()
                                                 .stream()
                                                 .map(Element::getSimpleName)
                                                 .map(Objects::toString)
                                                 .map(fieldName -> String.format(
                                                     "@Mapping(target = \"%s\", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)",
                                                     fieldName))
                                                 .collect(Collectors.toList());

    if (!mappings.isEmpty()) {
      writer.println("@Override");
      mappings.forEach(writer::println);
      writer.println(String.format("%s map(%sDTO source);", sourceSimpleName, sourceSimpleName));
    }

    writer.println("}");
  }
}

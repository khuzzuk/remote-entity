package pl.javahello.processor;

import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

class AdapterToEntityGenerator extends AbstractAdapterGenerator {

  AdapterToEntityGenerator(RoundEnvironment roundEnv,
                           SourceFileDescription sourceFileDescription,
                           ProcessingEnvironment processingEnvironment) {
    super(roundEnv, sourceFileDescription, processingEnvironment);
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
    writer.println(String.format("public interface %sAdapter extends Adapter<%sDTO, %s> {",
                                 sourceSimpleName,
                                 sourceSimpleName,
                                 sourceSimpleName));

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

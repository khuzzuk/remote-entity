package pl.javahello.processor;

import static java.lang.String.format;
import static pl.javahello.common.TypeUtils.enclosingClassName;
import static pl.javahello.common.TypeUtils.isInternalClass;

import java.io.PrintWriter;
import javax.annotation.processing.ProcessingEnvironment;

class AdapterToDtoGenerator extends AbstractAdapterGenerator {

  AdapterToDtoGenerator(SourceFileDescription sourceFileDescription,
                        ProcessingEnvironment processingEnvironment) {
    super(sourceFileDescription, processingEnvironment);
  }

  @Override
  String getGeneratedClassName() {
    return sourceFileDescription.getPackageElement().getQualifiedName() +
           "." +
           sourceFileDescription.getElement().getSimpleName() +
           "DTOAdapter";
  }

  @Override
  void generateContent(PrintWriter writer) {
    printPackage(writer, sourceFileDescription.getPackageElement().getQualifiedName().toString());

    printImports(writer,
                 "org.mapstruct.Mapper",
                 "org.mapstruct.ReportingPolicy",
                 "pl.javahello.Adapter");
    writer.println(getMapperDeclaration("DTOAdapter"));

    String sourceSimpleName = sourceFileDescription.getElement().getSimpleName().toString();

    if (isInternalClass(sourceFileDescription.getElement())) {
      String enclosingClassName = enclosingClassName(sourceFileDescription.getElement());
      writer.println(format("public interface %1$sDTOAdapter extends Adapter<%2$s.%1$s, %1$sDTO> {}", sourceSimpleName, enclosingClassName));
    } else {
      writer.println(format("public interface %1$sDTOAdapter extends Adapter<%1$s, %1$sDTO> {}", sourceSimpleName));
    }
  }
}

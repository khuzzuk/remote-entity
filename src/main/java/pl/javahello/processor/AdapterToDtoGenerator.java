package pl.javahello.processor;

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

    String sourceSimpleName = sourceFileDescription.getElement().getSimpleName().toString();
    writer.println(getMapperDeclaration("DTOAdapter"));
    writer.println(String.format("public interface %sDTOAdapter extends Adapter<%s, %sDTO> {}",
                                 sourceSimpleName,
                                 sourceSimpleName,
                                 sourceSimpleName));
  }
}

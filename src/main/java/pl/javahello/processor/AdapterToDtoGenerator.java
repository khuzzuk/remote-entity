package pl.javahello.processor;

import java.io.PrintWriter;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

class AdapterToDtoGenerator extends AbstractAdapterGenerator {

  AdapterToDtoGenerator(RoundEnvironment roundEnv,
                        SourceFileDescription sourceFileDescription,
                        ProcessingEnvironment processingEnvironment) {
    super(roundEnv, sourceFileDescription, processingEnvironment);
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
                 "pl.khuzzuk.remote.Adapter");

    String sourceSimpleName = sourceFileDescription.getElement().getSimpleName().toString();
    writer.println(getMapperDeclaration("DTOAdapter"));
    writer.println(String.format("public interface %sDTOAdapter extends Adapter<%s, %sDTO> {}",
                                 sourceSimpleName,
                                 sourceSimpleName,
                                 sourceSimpleName));
  }
}

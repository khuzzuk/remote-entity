package pl.javahello.processor;

import java.io.PrintWriter;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

class DtoEntityGenerator extends DtoGenerator {
    DtoEntityGenerator(SourceFileDescription sourceFileDescription, ProcessingEnvironment processingEnvironment) {
        super(sourceFileDescription, processingEnvironment);
    }

    @Override
    void writeClassDeclaration(PrintWriter writer) {
        String extender = "pl.javahello.BaseDTO";
        List<? extends TypeMirror> directSupertypes = processingEnvironment.getTypeUtils().directSupertypes(sourceFileDescription.getElement().asType());
        if (directSupertypes.stream().anyMatch(t -> t.toString().endsWith("ListableEntity"))) {
            extender = "pl.javahello.ListableDTO";
        }

        writer.println(String.format("public class %sDTO extends %s {",
                sourceFileDescription.getElement().getSimpleName(), extender));
        writer.println();
    }
}

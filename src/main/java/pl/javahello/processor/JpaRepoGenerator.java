package pl.javahello.processor;

import java.io.PrintWriter;
import javax.annotation.processing.ProcessingEnvironment;

class JpaRepoGenerator extends AbstractFileGenerator {
    JpaRepoGenerator(SourceFileDescription sourceFileDescription, ProcessingEnvironment processingEnvironment) {
        super(sourceFileDescription, processingEnvironment);
    }

    @Override
    String getGeneratedClassName() {
        return sourceFileDescription.getPackageElement().getQualifiedName()
                + "."
                + sourceFileDescription.getElement().getSimpleName()
                + "Repo";
    }

    @Override
    void generateContent(PrintWriter writer) {
        String repoClassName = sourceFileDescription.getElement().getSimpleName() + "Repo";
        String packageName = sourceFileDescription.getPackageElement().getQualifiedName().toString();

        printPackage(writer, packageName);

        printImports(writer,
                "org.springframework.data.jpa.repository.JpaRepository",
                "org.springframework.stereotype.Repository");

        writer.println("@Repository");
        writer.println(String.format("public interface %s extends JpaRepository<%s, Long> {", repoClassName, sourceFileDescription.getElement()));
        writer.println("}");
    }
}

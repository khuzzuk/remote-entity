package pl.javahello.processor;

import java.io.PrintWriter;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import org.apache.commons.lang3.StringUtils;

public class RemoteServiceGenerator extends AbstractFileGenerator {
    RemoteServiceGenerator(RoundEnvironment roundEnv, SourceFileDescription sourceFileDescription, ProcessingEnvironment processingEnvironment) {
        super(roundEnv, sourceFileDescription, processingEnvironment);
    }

    @Override
    String getGeneratedClassName() {
        return sourceFileDescription.getPackageElement().getQualifiedName()
                + "."
                + sourceFileDescription.getElement().getSimpleName()
                + "Service";
    }

    @Override
    void generateContent(PrintWriter writer) {
        printPackage(writer, sourceFileDescription.getPackageElement().getQualifiedName().toString());

        printImports(writer,
                "org.springframework.web.bind.annotation.DeleteMapping",
                "org.springframework.web.bind.annotation.GetMapping",
                "org.springframework.web.bind.annotation.PostMapping",
                "org.springframework.web.bind.annotation.RequestBody",
                "org.springframework.web.bind.annotation.RequestMapping",
                "org.springframework.web.bind.annotation.RestController",
                "pl.khuzzuk.remote.RemoteService",
                "pl.khuzzuk.remote.Adapter",
                "javax.validation.Valid",
                "java.util.List");

        String entityName = sourceFileDescription.getElement().getSimpleName().toString();
        String beanName = StringUtils.uncapitalize(entityName);

        writer.println("@RestController");
        writer.println(String.format("@RequestMapping(\"%s\")", beanName));

        writer.println(String.format("public class %sService implements RemoteService<%sDTO> {", entityName, entityName));

        writer.println(String.format("private %sRepo %sRepo;", entityName, beanName));
        writer.println(String.format("private Adapter<%sDTO, %s> %sAdapter;", entityName, entityName, beanName));
        writer.println(String.format("private Adapter<%s, %sDTO> %sDTOAdapter;", entityName, entityName, beanName));


        writer.println(String.format("public %sService(%sRepo %sRepo, Adapter<%sDTO, %s> %sAdapter, Adapter<%s, %sDTO> %sDTOAdapter) { ",
                entityName, entityName, beanName, entityName, entityName, beanName, entityName, entityName, beanName));
        writer.println(String.format("this.%sRepo = %sRepo; ", beanName, beanName));
        writer.println(String.format("this.%sAdapter = %sAdapter; ", beanName, beanName));
        writer.println(String.format("this.%sDTOAdapter = %sDTOAdapter; ", beanName, beanName));
        writer.println(("}"));
        writer.println();

        writer.println("@GetMapping");
        writer.println("@Override");
        writer.println(String.format("public List<%sDTO> findAll() { ", entityName));
        writer.println(String.format("return %sDTOAdapter.list(%sRepo.findAll()); ", beanName, beanName));
        writer.println("}");

        writer.println("@PostMapping");
        writer.println("@Override");
        writer.println(String.format("public %sDTO save(@Valid @RequestBody %sDTO %sDTO) {",
                entityName, entityName, beanName));
        writer.println(String.format("%s %s = %sAdapter.map(%sDTO);", entityName, beanName, beanName, beanName));
        writer.println(String.format("%s persisted = %sRepo.save(%s);", entityName, beanName, beanName));
        writer.println(String.format("return %sDTOAdapter.map(persisted);", beanName));
        writer.println("}");

        writer.println("@DeleteMapping");
        writer.println("@Override");
        writer.println(String.format("public void delete(@Valid @RequestBody %sDTO %sDTO) {", entityName, beanName));
        writer.println(String.format("%s entity = %sAdapter.map(%sDTO);", entityName, beanName, beanName));
        writer.println(String.format("%sRepo.delete(entity);", beanName));
        writer.println("}");

        writer.println("}");
    }
}

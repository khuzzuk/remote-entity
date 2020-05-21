package pl.javahello.common;

import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.lang3.tuple.Pair;

public class CollectionTypeUtils {
    private static final Set<String> COLLECTION_TYPE_NAMES = Set.of(
            "java.util.Collection",
            "java.util.List",
            "java.util.Set",
            "java.util.Queue"
    );

    public static boolean isFieldCollection(Element field) {
        String fieldTypeDeclaration = field.asType().toString();
        return COLLECTION_TYPE_NAMES.stream().anyMatch(fieldTypeDeclaration::contains);
    }

    public static Element getTypeFromCollectionField(Element field, ProcessingEnvironment processingEnvironment) {
        DeclaredType declaredType = (DeclaredType) field.asType();
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

        if (typeArguments.size() != 1) {
            throw new IllegalArgumentException(String.format("Collection is not parametrized or field is not Collection type: %s", field.toString()));
        }

        return processingEnvironment.getTypeUtils().asElement(typeArguments.get(0));
    }

    public static boolean isMap(Element field) {
        return field.asType().toString().startsWith("java.util.Map<");
    }

    public static Pair<TypeMirror, TypeMirror> extractTypesFromMap(Element field, ProcessingEnvironment pe) {
        DeclaredType declaredType = (DeclaredType) field.asType();
        return Pair.of(declaredType.getTypeArguments().get(0), declaredType.getTypeArguments().get(1));
    }
}

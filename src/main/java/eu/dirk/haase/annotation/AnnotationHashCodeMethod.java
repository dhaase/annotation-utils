package eu.dirk.haase.annotation;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class AnnotationHashCodeMethod {

    public final static Function<Map<String, Object>, Integer> hashCode = AnnotationHashCodeMethod::hashCode;


    private static int hashCode(final Map<String, Object> propertyValueMap) {
        int hashCode = 0;

        for (Map.Entry<String, Object> entry : propertyValueMap.entrySet()) {
            hashCode += 127 * entry.getKey().hashCode() ^ attributeValueHashCode(entry.getValue());
        }

        return hashCode;
    }


    private static int attributeValueHashCode(final Object attributeValue) {
        final Class<?> attributeClass = attributeValue.getClass();
        if (!attributeClass.isArray()) {
            return attributeValue.hashCode();
        } else if (attributeClass == byte[].class) {
            return Arrays.hashCode((byte[]) attributeValue);
        } else if (attributeClass == char[].class) {
            return Arrays.hashCode((char[]) attributeValue);
        } else if (attributeClass == double[].class) {
            return Arrays.hashCode((double[]) attributeValue);
        } else if (attributeClass == float[].class) {
            return Arrays.hashCode((float[]) attributeValue);
        } else if (attributeClass == int[].class) {
            return Arrays.hashCode((int[]) attributeValue);
        } else if (attributeClass == long[].class) {
            return Arrays.hashCode((long[]) attributeValue);
        } else if (attributeClass == short[].class) {
            return Arrays.hashCode((short[]) attributeValue);
        } else {
            return attributeClass == boolean[].class ? Arrays.hashCode((boolean[]) attributeValue) : Arrays.hashCode((Object[]) attributeValue);
        }
    }


}

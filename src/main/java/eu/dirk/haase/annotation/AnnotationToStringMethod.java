package eu.dirk.haase.annotation;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public final class AnnotationToStringMethod {

    public final static Function<Map<String, Object>, String> toString = AnnotationToStringMethod::toString;

    private AnnotationToStringMethod() {
    }

    private static String attributeValueToString(final Object attributeValue) {
        Class<?> valueClass = attributeValue.getClass();
        if (!valueClass.isArray()) {
            return attributeValue.toString();
        } else if (valueClass == byte[].class) {
            return Arrays.toString((byte[]) attributeValue);
        } else if (valueClass == char[].class) {
            return Arrays.toString((char[]) attributeValue);
        } else if (valueClass == double[].class) {
            return Arrays.toString((double[]) attributeValue);
        } else if (valueClass == float[].class) {
            return Arrays.toString((float[]) attributeValue);
        } else if (valueClass == int[].class) {
            return Arrays.toString((int[]) attributeValue);
        } else if (valueClass == long[].class) {
            return Arrays.toString((long[]) attributeValue);
        } else if (valueClass == short[].class) {
            return Arrays.toString((short[]) attributeValue);
        } else {
            return valueClass == boolean[].class ? Arrays.toString((boolean[]) attributeValue) : Arrays.toString((Object[]) attributeValue);
        }
    }

    private static String toString(final Map<String, Object> propertyValueMap) {
        StringBuilder str = new StringBuilder(128);
        str.append('@');
        str.append(((Class<?>)propertyValueMap.get("annotationType")).getName());
        str.append('(');

        boolean isFirst = true;
        for(Map.Entry<String, Object> entry : propertyValueMap.entrySet()) {
            if (!"annotationType".equals(entry.getKey())) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    str.append(", ");
                }

                str.append(entry.getKey());
                str.append('=');
                str.append(attributeValueToString(entry.getValue()));
            }
        }

        str.append(')');
        return str.toString();
    }

}

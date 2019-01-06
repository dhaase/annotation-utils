package eu.dirk.haase.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;

public class AnnotationEqualsMethod {

    public final static BiFunction<Map<String, Object>, Object, Boolean> equals = AnnotationEqualsMethod::equals;

    private static boolean equals(final Map<String, Object> propertyValueMap, final Object thatObj) {
        if (thatObj instanceof Annotation) {
            Annotation that = (Annotation) thatObj;
            if (propertyValueMap.get("annotationType") == that.annotationType()) {
                Method[] methods = that.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getParameterCount() == 0) {
                        final Object thisValue = propertyValueMap.get(method.getName());
                        if (thisValue != null) {
                            final Object thatValue = extractValue(that, method);
                            if (!memberValueEquals(thisValue, thatValue)) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static Object extractValue(final Annotation that, Method method) {
        try {
            return method.invoke(that);
        } catch (InvocationTargetException | IllegalAccessException e) {
            // ignore
        }
        return null;
    }

    private static boolean memberValueEquals(final Object thisValue, final Object thatValue) {
        Class thisValueClass = thisValue.getClass();
        if (!thisValueClass.isArray()) {
            return thisValue.equals(thatValue);
        } else if (thisValue instanceof Object[] && thatValue instanceof Object[]) {
            return Arrays.equals((Object[]) thisValue, (Object[]) thatValue);
        } else if (thatValue.getClass() != thisValueClass) {
            return false;
        } else if (thisValueClass == byte[].class) {
            return Arrays.equals((byte[]) thisValue, (byte[]) thatValue);
        } else if (thisValueClass == char[].class) {
            return Arrays.equals((char[]) thisValue, (char[]) thatValue);
        } else if (thisValueClass == double[].class) {
            return Arrays.equals((double[]) thisValue, (double[]) thatValue);
        } else if (thisValueClass == float[].class) {
            return Arrays.equals((float[]) thisValue, (float[]) thatValue);
        } else if (thisValueClass == int[].class) {
            return Arrays.equals((int[]) thisValue, (int[]) thatValue);
        } else if (thisValueClass == long[].class) {
            return Arrays.equals((long[]) thisValue, (long[]) thatValue);
        } else if (thisValueClass == short[].class) {
            return Arrays.equals((short[]) thisValue, (short[]) thatValue);
        } else {
            assert thisValueClass == boolean[].class;
            return Arrays.equals((boolean[]) thisValue, (boolean[]) thatValue);
        }
    }

}
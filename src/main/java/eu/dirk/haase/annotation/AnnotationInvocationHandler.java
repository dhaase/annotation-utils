package eu.dirk.haase.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class AnnotationInvocationHandler implements InvocationHandler, Serializable {
    private static final Annotation[] EMPTY_ANNOTATIONS = {};
    private static final long serialVersionUID = 0L;
    private final Map<String, Object> propertyValueMap;

    public AnnotationInvocationHandler(final Class<? extends Annotation> annotationClass, final Map<String, Object> propertyValueMap) {
        this.propertyValueMap = new TreeMap<>();
        init(annotationClass, propertyValueMap);
    }

    public AnnotationInvocationHandler(final Annotation annotationObject) {
        this.propertyValueMap = new TreeMap<>();
        init(annotationObject);
    }

    private static Object extractValue(final Annotation annotationObject, final Method method) {
        try {
            return method.invoke(annotationObject);
        } catch (InvocationTargetException | IllegalAccessException e) {
            // ignore
        }
        return null;
    }

    private Object cloneIfArray(final Object attributeValue) {
        final Class<?> attributeClass = attributeValue.getClass();
        if (attributeClass.isArray() && Array.getLength(attributeValue) != 0) {
            if (attributeClass == byte[].class) {
                return ((byte[]) attributeValue).clone();
            } else if (attributeClass == char[].class) {
                return ((char[]) attributeValue).clone();
            } else if (attributeClass == double[].class) {
                return ((double[]) attributeValue).clone();
            } else if (attributeClass == float[].class) {
                return ((float[]) attributeValue).clone();
            } else if (attributeClass == int[].class) {
                return ((int[]) attributeValue).clone();
            } else if (attributeClass == long[].class) {
                return ((long[]) attributeValue).clone();
            } else if (attributeClass == short[].class) {
                return ((short[]) attributeValue).clone();
            } else if (attributeClass == boolean[].class) {
                return ((boolean[]) attributeValue).clone();
            } else {
                return ((Object[]) attributeValue).clone();
            }
        }
        return attributeValue;
    }

    private Object executeAnnotationMethods(final Method method, final Object[] args) {
        if ((method.getParameterCount() == 1) && "equals".equals(method.getName())) {
            return AnnotationEqualsMethod.equals.apply(this.propertyValueMap, args[0]);
        } else if (method.getParameterCount() == 0) {
            switch (method.getName()) {
                case "hashCode":
                    return AnnotationHashCodeMethod.hashCode.apply(this.propertyValueMap);
                case "toString":
                    return AnnotationToStringMethod.toString.apply(this.propertyValueMap);
            }
            final String attributeName = method.getName();
            final Object attributeValue = propertyValueMap.get(attributeName);
            if (attributeValue == null) {
                throw new IncompleteAnnotationException(getAnnotationType(), attributeName);
            } else {
                return this.cloneIfArray(attributeValue);
            }
        } else {
            throw new IllegalStateException("Too many parameters for an annotation method in @" + getAnnotationType());
        }
    }

    private Object executeIntrospectionMethods(final Method method, final Object[] args) {
        switch (method.getName()) {
            case "getAnnotationsByType":
                final Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) args[0];
                final Annotation[] annotationReflected = getAnnotationType().getAnnotationsByType(annotationClass);
                final int size = annotationReflected.length;
                if (size > 0) {
                    final Object[] metaAnnotations = (Object[]) Array.newInstance(annotationClass, size);
                    for (int i = 0; size > i; i++) {
                        metaAnnotations[i] = AnnotationFactory.newInstance(annotationReflected[i]);
                    }
                    return metaAnnotations;
                } else {
                    return EMPTY_ANNOTATIONS;
                }
        }
        throw new AbstractMethodError(method.getName() + " not implemented in " + getAnnotationType().getName());
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Annotation> getAnnotationType() {
        return (Class<? extends Annotation>) this.propertyValueMap.get("annotationType");
    }

    private void init(final Annotation annotationObject) {
        final Class<? extends Annotation> annotationClass = annotationObject.annotationType();
        for (Method property : annotationClass.getDeclaredMethods()) {
            final Object attributeValue = extractValue(annotationObject, property);
            this.propertyValueMap.put(property.getName(), attributeValue);
        }
        this.propertyValueMap.put("annotationType", annotationClass);
    }

    private void init(final Class<? extends Annotation> annotationClass, final Map<String, Object> propertyValueMap) {
        for (Method property : annotationClass.getDeclaredMethods()) {
            final Object defaultValue = property.getDefaultValue();
            if (defaultValue != null) {
                this.propertyValueMap.put(property.getName(), defaultValue);
            }
        }
        this.propertyValueMap.putAll(propertyValueMap);
        this.propertyValueMap.put("annotationType", annotationClass);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        if (method.getDeclaringClass() == AnnotationIntrospection.class) {
            return executeIntrospectionMethods(method, args);
        } else {
            return executeAnnotationMethods(method, args);
        }
    }

}

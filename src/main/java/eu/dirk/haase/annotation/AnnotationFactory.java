package eu.dirk.haase.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;

public class AnnotationFactory {

    public static  <T extends Annotation> T newInstance(final Class<? extends Annotation> annotationClass, final Map<String, Object> propertyValueMap) {
        final ClassLoader classLoader = AnnotationIntrospection.class.getClassLoader();
        final Class<?>[] interfaces = {annotationClass, AnnotationIntrospection.class};
        return  (T) Proxy.newProxyInstance(classLoader, interfaces, new AnnotationInvocationHandler(annotationClass, propertyValueMap));
    }

    public static  <T extends Annotation> T newInstance(final Annotation annotationObject) {
        final ClassLoader classLoader = AnnotationIntrospection.class.getClassLoader();
        final Class<?>[] interfaces = {annotationObject.annotationType(), AnnotationIntrospection.class};
        return  (T) Proxy.newProxyInstance(classLoader, interfaces, new AnnotationInvocationHandler(annotationObject));
    }

}

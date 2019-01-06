package eu.dirk.haase.annotation;

import java.lang.annotation.Annotation;

public interface AnnotationIntrospection {

    <A1 extends Annotation, A2 extends Annotation & AnnotationIntrospection> A2[] getAnnotationsByType(final Class<A1> annotationClass);

}

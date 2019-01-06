package eu.dirk.haase.annotation;

import java.lang.annotation.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static <T extends MyAnnotation & AnnotationIntrospection> void main(String... args) throws NoSuchMethodException {
        Map<String, Object> propertyValueMap = new HashMap<String, Object>();
        propertyValueMap.put("aaaa", "bbbb");
        propertyValueMap.put("name", "Hallo");
        propertyValueMap.put("myDefault", "myValue");

        Method method = MyAnnotation.class.getMethod("myDefault");
        System.out.println(method.getDefaultValue());

        MyAnnotation anno1 = MyClass.class.getAnnotation(MyAnnotation.class);
        T anno2 = AnnotationFactory.newInstance(MyAnnotation.class, propertyValueMap);
        T anno4 = AnnotationFactory.newInstance(anno1);

        System.out.println(anno1);
        System.out.println(anno2);
        System.out.println(anno4);
        System.out.println(anno2.myDefault());
        System.out.println(anno1.equals(anno2));
        System.out.println(anno1.equals(anno4));
        System.out.println(anno4.equals(anno1));
        System.out.println(anno2.getAnnotationsByType(Documented.class)[0].getAnnotationsByType(Retention.class)[0]);
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface MyAnnotation {
        String aaaa() default "bbbb";
        String introspect() default "cccc";
        String name();
        String myDefault() default "myValue";
    }


    @MyAnnotation(name = "Hallo")
    static class MyClass {

    }


    static class AnnotationInvocationHandler implements InvocationHandler {

        private final Class<? extends Annotation> annotationClass;
        private final Map<String, Class<?>> propertyTypeMap;
        private final Map<String, Object> propertyValueMap;

        AnnotationInvocationHandler(final Class<? extends Annotation> annotationClass, final Map<String, Object> propertyValueMap) {
            this.annotationClass = annotationClass;
            this.propertyTypeMap = new HashMap<String, Class<?>>();
            this.propertyValueMap = new HashMap<String, Object>(propertyValueMap);
            propertyValueMap.put("annotationType", annotationClass);
            propertyValueMap.put("name", "Hallo");
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (this.propertyTypeMap.isEmpty()) {
                for (Method property : annotationClass.getDeclaredMethods()) {
                    this.propertyTypeMap.put(property.getName(), property.getReturnType());
                }
            }
            return propertyValueMap.get(method.getName());
        }
    }
}

package reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Reflect {

    private final Class<?> clazz = this.getClass();

    protected Object instance() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<?> constructor = clazz.getConstructor();
        return constructor.newInstance();
    }

    protected boolean isStatic(String methodName) throws NoSuchMethodException, SecurityException {
        Method method = clazz.getMethod(methodName);

        return Modifier.isStatic(method.getModifiers());
    }

    protected Method getMethod(String methodName, Class<?>... parameterType) throws NoSuchMethodException, SecurityException {
        if (parameterType != null) {
            return clazz.getMethod(methodName,parameterType);
        } else {
            return clazz.getMethod(methodName);
        }
    }

    protected Field[] fetchFields() {
        return clazz.getDeclaredFields();
    }

    protected List<Method> fetchAllGetters() throws NoSuchMethodException, SecurityException, NullOrEmptyException {
        List<Method> methods = new ArrayList<>();

        for (Field field : this.fetchFields()) {
            methods.add(getMethod("get" + firstCharacterToUpperCase(field.getName())));
        }

        return methods;
    }

    protected List<Method> fetchAllSetters() throws NoSuchMethodException, SecurityException, NullOrEmptyException {
        List<Method> methods = new ArrayList<>();

        for (Field field : this.fetchFields()) {
            Method method = getMethod("set" + firstCharacterToUpperCase(field.getName()),field.getType());
            methods.add(method);
        }

        return methods;
    }

    protected Object cast(Object value, Class<?> desiredClass) throws Exception {
        try {
            Method method = desiredClass.getMethod("valueOf", String.class);

            return method.invoke(null, value.toString());
        } catch (Exception e) {
            throw e;
        }
    }
    
    public String firstCharacterToUpperCase(String attr) throws NullOrEmptyException {
        if (attr == null || attr.isEmpty()) {
            throw new NullOrEmptyException();
        }

        return Character.toUpperCase(attr.charAt(0)) + attr.substring(1);
    }

}

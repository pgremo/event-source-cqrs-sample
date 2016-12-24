package io.pillopl.eventsource.domain.shopitem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.springframework.integration.util.ClassUtils.findClosestMatch;

public class MethodInvoker {
  public <V> V invoke(Object target, Object parameter) throws NoSuchMethodException {
    Map<Class<?>, Method> methods = Stream.of(target.getClass().getMethods())
      .filter(x -> x.getParameterCount() == 1)
      .filter(x -> x.getParameterTypes()[0].isAssignableFrom(parameter.getClass()))
      .collect(toMap(x -> x.getParameterTypes()[0], identity()));

    Class<?> match = findClosestMatch(parameter.getClass(), methods.keySet(), true);

    try {
      return (V) methods.get(match).invoke(target, parameter);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}

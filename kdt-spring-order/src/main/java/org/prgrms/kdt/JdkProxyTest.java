package org.prgrms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class CalculatorImpl implements Calculator{

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}

interface Calculator {
    int add(int a, int b);
}

class LogingInvocationHandler implements InvocationHandler{
    private final Logger log = LoggerFactory.getLogger(LogingInvocationHandler.class);
    private final Object target;

    LogingInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("{} executed", method.getName());
        return method.invoke(target, args);
    }
}

public class JdkProxyTest {
    private static final Logger log = LoggerFactory.getLogger(JdkProxyTest.class);

    public static void main(String[] args) {
        var calculator= new CalculatorImpl();
        Calculator proxyInstance = (Calculator) Proxy.newProxyInstance(
                LogingInvocationHandler.class.getClassLoader(),
                new Class[] {Calculator.class},
                new LogingInvocationHandler(calculator));
        var result = proxyInstance.add(1,2);
        log.info("Add -> {}", result);
    }
}

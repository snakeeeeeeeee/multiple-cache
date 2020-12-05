package com.zy.github.multiple.cache.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationInvoker;

import java.lang.reflect.Method;
import java.util.Stack;

public class CacheInterceptorCustom extends CacheInterceptor {
    static ThreadLocal<Stack<MethodInvocation>> methodHolder = ThreadLocal.withInitial(Stack::new);

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        methodHolder.get().push(invocation);
        Method method = invocation.getMethod();
        CacheOperationInvoker aopAllianceInvoker = () -> {
            try {
                return invocation.proceed();
            } catch (Throwable e) {
                throw new CacheOperationInvoker.ThrowableWrapper(e);
            }
        };

        try {
            Object target = invocation.getThis();
            return this.execute(aopAllianceInvoker, target, method, invocation.getArguments());
        } catch (CacheOperationInvoker.ThrowableWrapper e) {
            throw e.getOriginal();
        } finally {
            methodHolder.get().pop();
        }
    }

    public static MethodInvocation getMethodInvocation() {
        return methodHolder.get().peek();
    }
}

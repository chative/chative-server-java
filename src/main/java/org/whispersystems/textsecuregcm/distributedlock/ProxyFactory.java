package org.whispersystems.textsecuregcm.distributedlock;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

public class ProxyFactory implements MethodInterceptor {

    private final Object target;//维护一个目标对象

    public ProxyFactory(Object target) {
        this.target = target;
    }

    //为目标对象生成代理对象
    public Object getProxyInstance() {
        //工具类
        Enhancer en = new Enhancer();
        //设置父类
        en.setSuperclass(target.getClass());
        //设置回调函数
        en.setCallback(this);
        //创建子类对象代理
        return en.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 查看是否有锁的注解
        FairLock lockAnn = method.getAnnotation(FairLock.class);
        if (lockAnn == null) {
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        Object returnValue;
        // 获取锁对象
        Lock locker = DistributedLock.getLocker(lockAnn.names(), lockAnn.time(), lockAnn.unit());
        try {
            locker.lock(); // 加锁
            // 执行目标对象的方法
            returnValue = method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } finally {
            locker.unlock();
        }

        return returnValue;
    }
}

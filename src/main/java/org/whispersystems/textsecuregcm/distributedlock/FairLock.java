package org.whispersystems.textsecuregcm.distributedlock;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FairLock {
    // 锁名,应为全局唯一
    String[] names();

    // 最长锁定时间
    long time() default 10;
    TimeUnit unit() default TimeUnit.SECONDS;
}

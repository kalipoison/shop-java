package com.gohb.anoo;

import java.lang.annotation.*;

/**
 * 做切面
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Log {

    // 操作的名称
    String operation() default "";

}

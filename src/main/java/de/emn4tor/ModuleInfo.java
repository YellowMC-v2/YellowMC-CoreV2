package de.emn4tor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    String name();
    String server() default ""; //Empty -> No specific server requirement, will be loaded on all servers
    int priority() default 100; //Lower number = Higher priority, Modules with higher priority will be loaded first. Modules with the same priority will be loaded in random order bc idk lol.
}
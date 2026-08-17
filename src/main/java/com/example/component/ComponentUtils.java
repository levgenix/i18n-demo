package com.example.component;

import java.util.function.Function;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ComponentUtils {

    public static void printHello(Function<String, String> localize) {
        System.out.println("hello from ComponentUtils: " + localize.apply("hello"));
    }

}

package com.smartlinguo;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class SmartLinguoApp {

    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
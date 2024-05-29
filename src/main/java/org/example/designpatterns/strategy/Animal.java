package org.example.designpatterns.strategy;

import lombok.ToString;

@ToString
public class Animal {

    /**
     * # Strategy interface will have more than one implementation.
     * 1. Avoid if-else and switch cases
     * 2. Reducing code duplication
     *
     * ex. payment app, internal sorting libraries,
     */


    private Boolean isDomestic;
    private String sound;

    public Animal(Boolean isDomestic, String sound) {
        this.isDomestic = isDomestic;
        this.sound = sound;
    }
}


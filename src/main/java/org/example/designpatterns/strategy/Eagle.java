package org.example.designpatterns.strategy;

import lombok.ToString;

@ToString
public class Eagle extends Animal {
    private FlyStrategy flyingType;

    public Eagle(Boolean isDomestic, String sound) {
        super(isDomestic, sound);
        this.flyingType = new EagleFlyingType();
    }
}

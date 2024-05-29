package org.example.designpatterns.strategy;

import lombok.ToString;

@ToString
public class EagleFlyingType implements FlyStrategy {
    @Override
    public String getFlyingType() {
        return "High Altitude";
    }
}

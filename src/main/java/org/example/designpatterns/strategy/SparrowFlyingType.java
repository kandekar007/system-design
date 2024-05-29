package org.example.designpatterns.strategy;

import lombok.ToString;

@ToString
public class SparrowFlyingType implements FlyStrategy {
    @Override
    public String getFlyingType() {
        return "Low Altitude";
    }
}

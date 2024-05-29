package org.example.designpatterns.strategy;


import lombok.ToString;

@ToString
public class Tiger extends Animal {
    private Long stripeCount;

    public Tiger(Boolean isDomestic, String sound, Long stripeCount) {
        super(isDomestic, sound);
        this.stripeCount = stripeCount;
    }
}

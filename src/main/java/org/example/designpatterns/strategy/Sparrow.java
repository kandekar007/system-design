package org.example.designpatterns.strategy;

import lombok.ToString;

@ToString
public class Sparrow extends Animal {
  private FlyStrategy flyStrategy;

  public Sparrow(Boolean isDomestic, String sound) {
    super(isDomestic, sound);
    this.flyStrategy = new SparrowFlyingType();
  }
}

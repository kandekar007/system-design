package org.example.designpatterns.factory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EnemyShip {
    private String name;
    private Double damage;

    public void followHeroShip() {
        System.out.println(getName() + " is following hero");
    }

    public void displayEnemyShip() {
        System.out.println(getName() + " is on screen");
    }

    public void enemyShipShoots() {
        System.out.println(getName() + " attacks and does damage: " +
                getDamage());

    }
}

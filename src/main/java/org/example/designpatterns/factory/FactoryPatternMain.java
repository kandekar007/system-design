package org.example.designpatterns.factory;

import java.util.Scanner;

public class FactoryPatternMain {
    public static void main(String[] args) {
        // Check this class
        EnemyShipFactory shipFactory = new EnemyShipFactory();

        EnemyShip enemyShip = null;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a letter (U/R/B)");

        if (input.hasNextLine()) {
            String shipType = input.nextLine();
            enemyShip = shipFactory.makeEnemyShip(shipType);
        }
        if (null != enemyShip) {
            doStuff(enemyShip);
        } else {
            System.out.println("Enter a valid letter (U/R/B)");
        }
    }

    private static void doStuff(EnemyShip enemyShip) {
        enemyShip.displayEnemyShip();
        enemyShip.followHeroShip();
        enemyShip.enemyShipShoots();
    }
}

package org.example.designpatterns.factory;

// Design making about enemy ship construction is in here
public class EnemyShipFactory {
    public EnemyShip makeEnemyShip(String str) {
        EnemyShip enemyShip = null;

        if ("U".equals(str)) {
            enemyShip = new UfoEnemyShip();
        }
        else if ("R".equals(str)) {
            enemyShip = new RocketEnemyShip();
        }
        else if ("B".equals(str)) {
            enemyShip = new BigUfoEnemyShip();
        }
        return enemyShip;
    }
}

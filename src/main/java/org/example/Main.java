package org.example;

import org.example.designpatterns.strategy.Eagle;
import org.example.designpatterns.strategy.Sparrow;
import org.example.designpatterns.strategy.Tiger;


public class Main {

    public static void main(String[] args){
        Tiger tiger = new Tiger(false, "roar", 100L);
        Eagle eagle = new Eagle(false, "cry");
        Sparrow sparrow = new Sparrow(true, "twit");

        System.out.println(tiger.toString());
        System.out.println(eagle.toString());
        System.out.println(sparrow.toString());
    }
}
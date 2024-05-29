package org.example;

import org.example.designpatterns.observer.NewsAgency;
import org.example.designpatterns.observer.NewsChannel1;
import org.example.designpatterns.observer.NewsChannel2;
import org.example.designpatterns.strategy.Eagle;
import org.example.designpatterns.strategy.Sparrow;
import org.example.designpatterns.strategy.Tiger;

public class Main {

  public static void strategy() {
    Tiger tiger = new Tiger(false, "roar", 100L);
    Eagle eagle = new Eagle(false, "cry");
    Sparrow sparrow = new Sparrow(true, "twit");

    System.out.println(tiger);
    System.out.println(eagle);
    System.out.println(sparrow);
  }

  public static void observer() {
    Object oldNews = "News old";
    Object newNews = "News new";

    NewsAgency observable = new NewsAgency();
    NewsChannel1 observer1 = new NewsChannel1();
    NewsChannel2 observer2 = new NewsChannel2();

    observable.addObserver(observer1);
    observable.addObserver(observer2);

    observable.setNews((String) oldNews);
    System.out.println(observer1);
    System.out.println(observer2);

    observable.removeObserver(observer2);
    observable.setNews((String) newNews);
    System.out.println(observer1);
    System.out.println(observer2);
  }

  public static void main(String[] args) {
    System.out.println("\tStrategy Design Pattern");
    strategy();
    System.out.println("\tObserver Design Pattern");
    observer();
  }
}

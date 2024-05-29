package org.example.designpatterns.observer;

import lombok.ToString;

@ToString
public class NewsChannel1 implements Channel {
  private String news;

  @Override
  public void update(Object s) {
    this.news = (String) s;
  }
}

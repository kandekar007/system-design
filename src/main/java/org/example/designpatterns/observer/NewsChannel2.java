package org.example.designpatterns.observer;

import lombok.ToString;

@ToString
public class NewsChannel2 implements Channel {
  private String news;
  private String debate;

  @Override
  public void update(Object o) {
    this.news = (String) o;
    this.debate = "Ch2 Debate";
  }
}

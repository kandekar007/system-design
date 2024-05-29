package org.example.designpatterns.observer;

import java.util.ArrayList;
import java.util.List;
import lombok.ToString;

@ToString
public class NewsAgency {
  private Object news;
  private List<Channel> channels = new ArrayList<>();

  public void addObserver(Channel channel) {
    channels.add(channel);
  }

  public void removeObserver(Channel channel) {
    channels.remove(channel);
  }

  /** Update all channels subscribed to news agency. */
  public void notifyChannels() {
    for (Channel ch : channels) {
      ch.update(news);
    }
  }

  public void setNews(String news) {
    this.news = news;
    notifyChannels();
  }
}

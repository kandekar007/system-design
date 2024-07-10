package org.example.designpatterns.observer;

public interface Channel {
  /**
   * An observable is an object which notifies observers about the changes in its state.
   * <li>Observer will subscribe to Observables using a list or map
   * <li>Any update in observable will be propagated from the observer list
   */
  void update(Object o);

  // NewsAgency will be observable here and Channels will be observer.
}

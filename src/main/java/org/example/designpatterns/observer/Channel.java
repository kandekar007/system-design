package org.example.designpatterns.observer;

public interface Channel {
  /**
   * # An observable is an object which notifies observers about the changes in its state. 1.
   * Observer will subscribe to Observables using a list or map 2. Any update
   */
  void update(Object o);

  /** NewsAgency will be observable here and Channels will be observer. */
}

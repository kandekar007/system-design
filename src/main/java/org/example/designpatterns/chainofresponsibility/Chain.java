package org.example.designpatterns.chainofresponsibility;

public interface Chain {

  void setNextChain(Chain nextChain);

  void calculate();
}

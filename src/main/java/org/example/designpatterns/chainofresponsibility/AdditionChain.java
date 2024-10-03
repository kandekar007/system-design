package org.example.designpatterns.chainofresponsibility;

public class AdditionChain implements Chain {
  private Chain nextInChain;

  @Override
  public void setNextChain(Chain nextChain) {
    this.nextInChain = nextChain;
  }

  @Override
  public void calculate() {}
}

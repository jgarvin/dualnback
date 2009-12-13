package org.dualnback.android;

public class Pair<T, S> {
  public Pair(T f, S s){
    first = f;
    second = s;
  }

  public String toString()  {
    return "(" + first.toString() + ", " + second.toString() + ")";
  }

  public T first;
  public S second;
}


package com.mysterionnh.tinker.primegen;

public class Primes {

  public static void main(String[] args) {
    boolean isPrime = true;
    for (int i = 0; i < Integer.valueOf(args[0]); i++) {
      for (int j = 2; j < 10 && isPrime; j++) {
        isPrime = i % j != 0;
      }
      if (isPrime || i == 2 || i == 3 || i == 5 || i == 7) {
        System.out.println(i);
      } else {
        isPrime = true;
      }
    }
  }
}

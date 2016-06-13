package com.mysterionnh.tinker;

import com.mysterionnh.util.Logger;

public class Primes {
  
  private Logger log;

  public Primes(Logger _log, String[] args) {
    log = _log;
    
    boolean isPrime = true;
    for (int i = 2; i < Integer.valueOf(args[1]); i++) {
      for (int j = 2; j < 10 && isPrime; j++) {
        isPrime = i % j != 0;
      }
      if (isPrime || i == 2 || i == 3 || i == 5 || i == 7) {
        log.logString(String.valueOf(i) + "\n");
      } else {
        isPrime = true;
      }
    }
  }
}

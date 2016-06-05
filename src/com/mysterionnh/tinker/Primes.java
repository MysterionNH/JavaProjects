package com.mysterionnh.tinker;

import com.mysterionnh.util.Logger;

public class Primes {
  
  private Logger log;

  // FIXME: last test didn't work
  public Primes(Logger _log, String[] args) {
    log = _log;
    
    boolean isPrime = true;
    for (int i = 0; i < Integer.valueOf(args[0]); i++) {
      for (int j = 2; j < 10 && isPrime; j++) {
        isPrime = i % j == 0;
      }
      if (isPrime || i == 2 || i == 3 || i == 5 || i == 7) {
        log.logString(String.valueOf(i));
      } else {
        isPrime = true;
      }
    }
  }
}

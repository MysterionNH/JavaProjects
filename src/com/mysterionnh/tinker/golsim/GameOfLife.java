package com.mysterionnh.tinker.golsim;

import java.util.Random;

public class GameOfLife {
  
  private String deadSign = ". ";
  private String aliveSign = "O ";

  /**
   * @param args: arg0 - int, field size
   *              arg1 - int, startCellCount
   *              arg2 - int, genCount
   *              arg3 - int, sleepTime in millis
   */
  public static void main(String[] args) {
    GameOfLife gol = new GameOfLife();
    
    // Defaults
    int fieldSize = 50;
    int maxStartCellCount = 500;
    int generationCount = 25;
    int sleepTime = 500;
    int temp;
    
    if (args != null & args.length == 4) {
      try {
        temp = Integer.valueOf(args[0]);
        fieldSize = (temp > 0) ? temp: fieldSize;
        
        temp = Integer.valueOf(args[1]);
        maxStartCellCount = (temp > 0 && temp < (fieldSize*fieldSize)) ? temp : maxStartCellCount;

        temp = Integer.valueOf(args[2]);
        generationCount = (temp >= 0) ? temp : generationCount;
        
        temp = Integer.valueOf(args[3]);
        sleepTime = (temp >= 0) ? temp : sleepTime;
      } catch (NumberFormatException e) {
        gol.logMsg("\n\nInvalid argument type used! Valid arguments are *fieldsize* *maxStartCellCount* *generationCount* *waitTimeInMillisBetweenGenerations*, all of them as Integers.\n\n", true);
        e.printStackTrace();
        System.exit(-1);
      }
    }
    gol.simulateGameOfLife(fieldSize, maxStartCellCount, generationCount, sleepTime);
  }

  public void simulateGameOfLife(int fieldSize, int maxStartCellCount, int generationCount, int sleepTime) {

    boolean[][] currentGeneration = new boolean[fieldSize][fieldSize];
    boolean[][] nextGeneration = new boolean[fieldSize][fieldSize];

    int aliveCount = 0;
    
    /**   keeping track of the cell count, this first assingment MAY be wrong, 
     *    because the random placement could places multiple alive ones in the same loction,
     *    resulting in less alives then stated, but from genreation 1 upwards they will be correct
     */
    int dead = 0, alive = 0;
    
    Random rand = new Random(System.currentTimeMillis());
    
    // this isn't very accurate with the numbers, but at leats it doesn't take ages to generate
    for (int cells = 0; cells < maxStartCellCount; cells++) {
      currentGeneration[rand.nextInt(fieldSize)][rand.nextInt(fieldSize)] = true;
    }
    /* this version is accurate, but it takes forever :(
      for (int cells = 0; cells < startCellCount; cells++) {
        int x, y;
        do {
          x = rand.nextInt(fieldSize);
          y = rand.nextInt(fieldSize);
        } while (!currentGeneration[x][y]);
        currentGeneration[x][y] = true;
      }
    */
    
    /**print out the first generation and a general statement
    logMsg("Field Size:\t " + fieldSize + " by " + fieldSize + ", with a maximum of " + alive + " alive cells in the beginning.\n"
        + "The simulation will run for " + generationCount + " generations or till nothing changes anymore.\n"
        + "Between the generations there will be a break of " + sleepTime + " milli-seconds for you to look at the current one.\n", false);
    
    wait(5000);
    */
    
    for (int b = 0; b < fieldSize; b++) {
      logMsg("\n", false);
      for (int c = 0; c < fieldSize; c++) {
        logMsg(currentGeneration[b][c] ? aliveSign : deadSign, false);
        if (currentGeneration[b][c]) {
          alive++;
        } else {
          dead++;
        }
      }
    }
    
    logMsg("\n\nGeneration 0 - " + alive + " alive and " + dead + " dead cells\n", false);
    
    for (int gens = 0; gens < generationCount; gens++) {
      dead = 0;
      alive = 0;
      for (int i = 0; i < fieldSize; i++) {
        logMsg("\n", false);
        for (int j = 0; j < fieldSize; j++) {
          for (int k = -1; k < 2; k++) {
            for (int l = -1; l < 2; l++) {
              if (!(l == k && k == 0)) {
                aliveCount += (currentGeneration[generateValidCoordinate(i, (k == -1) ? 2 : ((k == 0) ? 0 : 1), fieldSize)][generateValidCoordinate(j, (l == -1) ? 2 : ((l == 0) ? 0 : 1), fieldSize)]) ? 1 : 0;
              } else {
                // currently at [i][j], so no check here
              }
            }
          }
               
          if (aliveCount < 2 || aliveCount > 3) {
            nextGeneration[i][j] = false; //dead, under-/overpopulation
            dead++;
          } else if (aliveCount == 3) {
            nextGeneration[i][j] = true; //reprodruction
            alive++;
          } else {
            nextGeneration[i][j] = currentGeneration[i][j]; // just leave it as it is
            if (nextGeneration[i][j]) {
              alive++;
            } else {
              dead++;
            }
          }
          logMsg(nextGeneration[i][j] ? aliveSign : deadSign, false);
          aliveCount = 0;
        }
      }
      logMsg("\n\nGeneration " + (gens + 1) + " - " + alive + " alive and " + dead + " dead cells\n", false);
      
      // every 5 generations we take a look whether the simulation died
      if (!((gens + 1) % 5 == 0 && gens != 0)) {
        currentGeneration = nextGeneration;
        wait(sleepTime);
      } else if (generationIsDead(currentGeneration)) {
        logMsg("\n\nIndifferent Generations - Terminating Simulation\n", true);
        System.exit(-1);
      } else {
        currentGeneration = nextGeneration;
        wait(sleepTime);
      }
    }
  }

  /**
    * @param num:     The entered number, possibly invalid coordinate that will be used to calculate the valid one
    *        modus:   0 - leave it as is, 1 - add 1, 2 - substract 1
    */
  private int generateValidCoordinate(int num, int modus, int fieldSize) {
    if (modus > -1 && modus < 3) {
      if (modus == 0) {
        return num;
          } else {
            return ((modus == 1) ? ((num == (fieldSize - 1)) ? 0 : (num + 1)) : ((num == 0) ? (fieldSize - 1) : (num - 1)));
          }
    } else {
      logMsg("Invalid modus! (generateValidCoordinate(int, int))", true);
      return -1;
    }
  }
  
  private boolean generationIsDead(boolean[][] gen) {
    boolean dead = true;
    
    for (int i = 0; i < gen.length && dead; i++) {
      for (int j = 0; j < gen.length && dead; j++) {
        if (gen[i][j]) {
          dead = false;
        } else {
          dead = true;
        }
      }
    }
    return dead;
  }
  
  private void logMsg(String msg, boolean error) {
    if (error) {
      System.err.print(msg);
    } else {
      System.out.print(msg);
    }
  }
  
  private void wait(int millis) {
    try {
      Thread.sleep(millis);
    } catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }
}
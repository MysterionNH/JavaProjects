package com.mysterionnh.tinker;

import com.mysterionnh.util.Logger;

import java.util.Random;

public class GameOfLife {

    private final Logger log;

    /**
     * @param args: arg0 - int, field size
     *              arg1 - int, startCellCount
     *              arg2 - int, genCount
     *              arg3 - int, sleepTime in millis
     */
    public GameOfLife(Logger _log, String[] args) {
        log = _log;

        // Defaults
        int fieldSize = 50;
        int maxStartCellCount = 500;
        int generationCount = 25;
        int sleepTime = 500;
        int temp;

        if (args != null && args.length == 5) {
            try {
                temp = Integer.valueOf(args[1]);
                fieldSize = (temp > 0) ? temp : fieldSize;

                temp = Integer.valueOf(args[2]);
                maxStartCellCount = (temp > 0 && temp < (fieldSize * fieldSize)) ? temp : maxStartCellCount;

                temp = Integer.valueOf(args[3]);
                generationCount = (temp >= 0) ? temp : generationCount;

                temp = Integer.valueOf(args[4]);
                sleepTime = (temp >= 0) ? temp : sleepTime;
            } catch (NumberFormatException e) {
                log.logError(this, "\n\nInvalid argument type used! Valid arguments are *fieldSize* *maxStartCellCount* *generationCount* *waitTimeInMillisBetweenGenerations*, all of them as Integers.\n\n", true, e);
            }
        }
        simulateGameOfLife(fieldSize, maxStartCellCount, generationCount, sleepTime);
    }

    private void simulateGameOfLife(int fieldSize, int maxStartCellCount, int generationCount, int sleepTime) {

        boolean[][] currentGeneration = new boolean[fieldSize][fieldSize];
        boolean[][] nextGeneration = new boolean[fieldSize][fieldSize];

        int aliveCount = 0;
    
    /*   keeping track of the cell count, this first assignment MAY be wrong,
     *    because the random placement could places multiple alive ones in the same location,
     *    resulting in less alive then stated, but from generation 1 upwards they will be correct
     */
        int dead = 0, alive = 0;

        Random rand = new Random(System.currentTimeMillis());

        // this isn't very accurate with the numbers, but at least it doesn't take ages to generate
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
    
    /*print out the first generation and a general statement
    logMsg("Field Size:\t " + fieldSize + " by " + fieldSize + ", with a maximum of " + alive + " alive cells in the beginning.\n"
        + "The simulation will run for " + generationCount + " generations or till nothing changes anymore.\n"
        + "Between the generations there will be a break of " + sleepTime + " milli-seconds for you to look at the current one.\n", false);
    
    wait(5000);
    */

        String deadSign = ". ";
        String aliveSign = "O ";
        for (int b = 0; b < fieldSize; b++) {
            log.logString("\n");
            for (int c = 0; c < fieldSize; c++) {
                log.logString(currentGeneration[b][c] ? aliveSign : deadSign);
                if (currentGeneration[b][c]) {
                    alive++;
                } else {
                    dead++;
                }
            }
        }

        log.logString("\n\nGeneration 0 - " + alive + " alive and " + dead + " dead cells\n");

        for (int gens = 0; gens < generationCount; gens++) {
            dead = 0;
            alive = 0;
            for (int i = 0; i < fieldSize; i++) {
                log.logString("\n");
                for (int j = 0; j < fieldSize; j++) {
                    for (int k = -1; k < 2; k++) {
                        for (int l = -1; l < 2; l++) {
                            if (!(l == k && k == 0))
                                aliveCount += (currentGeneration[generateValidCoordinate(i, k == -1 ? 2 : k == 0 ? 0 : 1, fieldSize)][generateValidCoordinate(j, l == -1 ? 2 : l == 0 ? 0 : 1, fieldSize)]) ? 1 : 0;
                        }
                    }

                    if (aliveCount < 2 || aliveCount > 3) {
                        nextGeneration[i][j] = false; //dead, under-/overpopulation
                        dead++;
                    } else if (aliveCount == 3) {
                        nextGeneration[i][j] = true; //reproduction
                        alive++;
                    } else {
                        nextGeneration[i][j] = currentGeneration[i][j]; // just leave it as it is
                        if (nextGeneration[i][j]) {
                            alive++;
                        } else {
                            dead++;
                        }
                    }
                    log.logString(nextGeneration[i][j] ? aliveSign : deadSign);
                    aliveCount = 0;
                }
            }
            log.logString("\n\nGeneration " + (gens + 1) + " - " + alive + " alive and " + dead + " dead cells\n");

            // every 5 generations we take a look whether the simulation died
            if (!((gens + 1) % 5 == 0 && gens != 0)) {
                currentGeneration = nextGeneration;
                wait(sleepTime);
            } else if (generationIsDead(currentGeneration)) {
                log.logError(this, "\n\nIndifferent Generations - Terminating Simulation\n", true);
            } else {
                currentGeneration = nextGeneration;
                wait(sleepTime);
            }
        }
    }

    /**
     * @param num: The entered number, possibly invalid coordinate that will be used to calculate the valid one
     *             mode:   0 - leave it as is, 1 - add 1, 2 - subtract 1
     */
    private int generateValidCoordinate(int num, int mode, int fieldSize) {
        if (mode > -1 && mode < 3) {
            if (mode == 0) {
                return num;
            } else {
                return ((mode == 1) ? ((num == (fieldSize - 1)) ? 0 : (num + 1)) : ((num == 0) ? (fieldSize - 1) : (num - 1)));
            }
        } else {
            log.logError(this, "Invalid mode! (generateValidCoordinate(int, int))", true);
            return -1;
        }
    }

    private boolean generationIsDead(boolean[][] gen) {
        boolean dead = true;

        for (int i = 0; i < gen.length && dead; i++) {
            for (int j = 0; j < gen.length && dead; j++) {
                dead = !gen[i][j];
            }
        }
        return dead;
    }

    private void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
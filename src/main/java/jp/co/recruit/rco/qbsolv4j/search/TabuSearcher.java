package jp.co.recruit.rco.qbsolv4j.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import jp.co.recruit.rco.qbsolv4j.solver.Solver;

public class TabuSearcher {

  private final boolean findMax;
  private final Random random = new Random();

  public TabuSearcher(boolean findMax) {
    this.findMax = findMax;
  }

  public double search(int[] solution, int quboSize, double[][] qubo, double[] flipCost,
      AtomicLong bitFlips, long maxFlips, double target, boolean targetSet, int nTabu) {
    final double sign = findMax ? 1.0 : -1.0;

    int lastChangedBit = 0;
    int maxIncrementCount = 900;

    double bestEnergy = Solver.localMaximumSearch(solution, quboSize, qubo, bitFlips);
    int[] index = valueIndexSort(flipCost, quboSize);
    long flipsOfIteration = maxFlips - bitFlips.get();
    long iterationIncrement = flipsOfIteration / 2;
    double lastEnergy = bestEnergy;

    int[] best = Arrays.copyOf(solution, quboSize);
    int[] tabuCount = new int[quboSize];

    searchIteration:
    while (bitFlips.get() < maxFlips) {
      int neighborBestBit = 0;
      double neighborBestEnergy = BIGNEGFP;
      boolean newBestEnergyFound = false;

      for (int bit : index) {
        if (tabuCount[bit] != 0) {
          continue;
        }

        bitFlips.incrementAndGet();
        double newEnergy = lastEnergy + flipCost[bit];
        if (newEnergy > bestEnergy) {
          newBestEnergyFound = true;
          lastChangedBit = bit;
          newEnergy = Solver.flipOneBit(lastEnergy, bit, solution, quboSize, qubo, flipCost);
          lastEnergy = Solver
              .localSearchOneBit(newEnergy, solution, quboSize, qubo, flipCost, bitFlips);
          index = valueIndexSort(flipCost, quboSize);
          bestEnergy = lastEnergy;

          System.arraycopy(solution, 0, best, 0, quboSize);
          if (targetSet && lastEnergy >= sign * target) {
            break searchIteration;
          }

          double done = 1.0 - (maxFlips - bitFlips.get()) / (double) flipsOfIteration;
          if (done >= 0.20 && maxIncrementCount > 0) {
            maxFlips += iterationIncrement;
            flipsOfIteration += iterationIncrement;
            maxIncrementCount--;
          }

          break;
        } else if (newEnergy > neighborBestEnergy) {
          neighborBestBit = bit;
          neighborBestEnergy = newEnergy;
        }
      }

      if (!newBestEnergyFound) {
        lastEnergy = Solver
            .flipOneBit(lastEnergy, neighborBestBit, solution, quboSize, qubo, flipCost);
        lastChangedBit = neighborBestBit;
      }

      // relax tabu
      for (int i = 0; i < quboSize; i++) {
        tabuCount[i] = Math.max(0, tabuCount[i] - 1);
      }

      // set tabu
      tabuCount[lastChangedBit] = nTabu;

      // add some asymmetry
      if (random.nextBoolean()) {
        tabuCount[lastChangedBit]++;
      } else {
        tabuCount[lastChangedBit]--;
      }
    }

    System.arraycopy(best, 0, solution, 0, quboSize);
    return Solver.evaluateEnergy(solution, quboSize, qubo);
  }

  private static final double BIGNEGFP = -1.225E308;

  public static int[] valueIndexSort(double[] values, int n) {
    ArrayList<double[]> list = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      list.add(new double[]{i, values[i]});
    }

    list.sort(Comparator.comparing(x -> x[1]));
    Collections.reverse(list);
    int[] result = new int[n];
    for (int i = 0; i < n; i++) {
      result[i] = (int) list.get(i)[0];
    }
    return result;
  }

  public static int setupTabuCount(int quboSize) {
    if (quboSize < 20) {
      return 5;
    } else if (quboSize < 100) {
      return 10;
    } else if (quboSize < 250) {
      return 12;
    } else if (quboSize < 500) {
      return 13;
    } else if (quboSize < 1000) {
      return 21;
    } else if (quboSize < 2500) {
      return 29;
    } else if (quboSize < 8000) {
      return 34;
    } else {
      return 35;
    }
  }
}

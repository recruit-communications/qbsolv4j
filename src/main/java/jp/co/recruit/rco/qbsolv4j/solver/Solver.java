/**
 * Copyright 2018 Recruit Communications
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.recruit.rco.qbsolv4j.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Solver {

  /**
   * This function evaluates the changes of objective function from flipping a bit for a given
   * solution.
   *
   * @param solution current solution vector
   * @param quboSize the number of variables in the QUBO matrix
   * @param qubo the QUBO matrix being solved
   * @return energy changes from flipping a bit
   */
  static double[] evaluateFlipCost(int[] solution, int quboSize, double[][] qubo) {
    double[] flipCost = new double[quboSize];
    for (int i = 0; i < quboSize; i++) {
      double rowSum = 0;
      double colSum = 0;

      for (int j = i + 1; j < quboSize; j++) {
        rowSum += qubo[i][j] * solution[j];
      }
      for (int j = 0; j < i; j++) {
        colSum += qubo[j][i] * solution[j];
      }

      if (solution[i] == 1) {
        flipCost[i] = -(rowSum + colSum + qubo[i][i]);
      } else {
        flipCost[i] = rowSum + colSum + qubo[i][i];
      }
    }

    return flipCost;
  }

  /**
   * This function evaluates the objective function for a given solution.
   *
   * @param solution current solution vector
   * @param quboSize the number of variables in the QUBO matrix
   * @param qubo the QUBO matrix being solved
   * @return energy of solution
   */
  public static double evaluateEnergy(int[] solution, int quboSize, double[][] qubo) {
    double result = 0.0;
    for (int i = 0; i < quboSize; i++) {
      for (int j = i; j < quboSize; j++) {
        result += qubo[i][j] * solution[i] * solution[j];
      }
    }

    return result;
  }

  /**
   * Flips a given bit in the solution, and calculates the new energy.
   *
   * @param oldEnergy current objective function value
   * @param bit the bit to be flipped
   * @param solution current solution vector
   * @param quboSize the size of the QUBO
   * @param qubo the QUBO matrix being solved
   * @param flipCost changes in energy from flipping a bit
   * @return new energy of the flipped solution
   */
  public static double flipOneBit(double oldEnergy, int bit, int[] solution, int quboSize,
      double[][] qubo, double[] flipCost) {
    double newEnergy = oldEnergy + flipCost[bit];

    // flip
    solution[bit] ^= 1;
    flipCost[bit] *= -1;

    // update flip costs
    int x = solution[bit] == 0 ? 1 : -1;
    for (int i = 0; i < bit; i++) {
      flipCost[i] += x * qubo[i][bit] * (solution[i] - 1 ^ solution[i]);
    }
    for (int i = bit + 1; i < quboSize; i++) {
      flipCost[i] += x * qubo[bit][i] * (solution[i] - 1 ^ solution[i]);
    }

    return newEnergy;
  }

  /**
   * Try to improve the current solution by flipping single bit.
   *
   * @param energy current energy
   * @param solution current solution vector
   * @param quboSize the size of the QUBO matrix
   * @param qubo the QUBO matrix being solved
   * @param flipCost energy changes
   * @param bitFlips count of bit flips
   * @return improved energy
   */
  public static double localSearchOneBit(double energy, int[] solution, int quboSize,
      double[][] qubo, double[] flipCost, AtomicLong bitFlips) {

    List<Integer> indices = new ArrayList<>();
    for (int k = 0; k < quboSize; k++) {
      indices.add(k);
    }

    boolean improved = true;
    while (improved) {
      improved = false;
      Collections.shuffle(indices);
      for (int bit : indices) {
        bitFlips.incrementAndGet();
        if (flipCost[bit] > 0) {
          energy = flipOneBit(energy, bit, solution, quboSize, qubo, flipCost);
          improved = true;
        }
      }
    }

    return energy;
  }

  /**
   * Performs a local maximum search improving the solution
   *
   * @param solution current solution
   * @param quboSize the size of the QUBO matrix
   * @param qubo the QUBO matrix being solved
   * @param bitFlips the number of bit flips in the entire algorithm
   * @return new energy of the modified solution
   */
  public static double localMaximumSearch(int[] solution, int quboSize, double[][] qubo,
      AtomicLong bitFlips) {
    double energy = evaluateEnergy(solution, quboSize, qubo);
    double[] flipCost = evaluateFlipCost(solution, quboSize, qubo);
    return localSearchOneBit(energy, solution, quboSize, qubo, flipCost, bitFlips);
  }

  static void reduce(int[] Icompress, double[][] qubo, int subQuboSize, int quboSize,
      double[][] subQubo, int[] solution, int[] subSolution) {
    for (int i = 0; i < subQuboSize; i++) {
      for (int j = 0; j < subQuboSize; j++) {
        subQubo[i][j] = 0;
      }
    }

    for (int i = 0; i < subQuboSize; i++) {
      for (int j = 0; j < subQuboSize; j++) {
        subQubo[i][j] = qubo[Icompress[i]][Icompress[j]];
      }
    }
    for (int i = 0; i < subQuboSize; i++) {
      subSolution[i] = solution[Icompress[i]];
    }

    for (int subVariable = 0; subVariable < subQuboSize; subVariable++) {
      int variable = Icompress[subVariable];
      double clamp = 0;

      int ji = subQuboSize - 1;

      for (int j = quboSize - 1; j > variable; j--) {
        if (j == Icompress[ji]) {
          ji--;
        } else {
          clamp += qubo[variable][j] * solution[j];
        }
      }

      ji = 0;
      for (int j = 0; j < variable + 1; j++) {
        if (j == Icompress[ji]) {
          ji++;
        } else {
          clamp += qubo[j][variable] * solution[j];
        }
      }

      subQubo[subVariable][subVariable] += clamp;
    }
  }
}

package jp.co.recruit.rco.qbsolv4j.solver;

import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

public class SolverTest {

  @Test
  public void flipOneBit() {
    Random random = new Random();
    int quboSize = 3;

    double[][] qubo = new double[quboSize][quboSize];
    for (int i = 0; i < quboSize; i++) {
      for (int j = 0; j < quboSize; j++) {
        qubo[i][j] = random.nextDouble() * random.nextInt();
      }
    }

    int[] solution = new int[quboSize];
    for (int i = 0; i < quboSize; i++) {
      solution[i] = random.nextInt(2);
    }

    double energy = Solver.evaluateEnergy(solution, quboSize, qubo);
    double[] flipCost = Solver.evaluateFlipCost(solution, quboSize, qubo);

    for (int bit = 0; bit < quboSize; bit++) {
      int[] copySolution = Arrays.copyOf(solution, quboSize);
      double[] copyFlipCost = Arrays.copyOf(flipCost, quboSize);

      double nextEnergy = Solver
          .flipOneBit(energy, bit, copySolution, quboSize, qubo, copyFlipCost);

      double expectedEnergy = Solver
          .evaluateEnergy(copySolution, quboSize, qubo);
      double[] expectedFlipCost = Solver
          .evaluateFlipCost(copySolution, quboSize, qubo);
      Assert.assertEquals(expectedEnergy, nextEnergy, 0.0001);
      Assert.assertArrayEquals(expectedFlipCost, copyFlipCost, 0.0001);
    }
  }
}
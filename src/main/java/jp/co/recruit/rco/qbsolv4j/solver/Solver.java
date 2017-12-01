package jp.co.recruit.rco.qbsolv4j.solver;

public class Solver {

  /**
   * This function evaluates the changes of objective function from flipping a
   * bit for a given solution.
   *
   * @param solution current solution vector
   * @param quboSize the number of variables in the QUBO matrix
   * @param qubo the QUBO matrix being solved
   * @return energy changes from flipping a bit
   */
  static double[] evaluateFlipCost(int[] solution, int quboSize,
      double[][] qubo) {
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
  static double evaluateEnergy(int[] solution, int quboSize, double[][] qubo) {
    double result = 0.0;
    for (int i = 0; i < quboSize; i++) {
      for (int j = i; j < quboSize; j++) {
        result += qubo[i][j] * solution[i] * solution[j];
      }
    }

    return result;
  }


}

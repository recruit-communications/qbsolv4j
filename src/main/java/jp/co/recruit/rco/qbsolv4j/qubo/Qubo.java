package jp.co.recruit.rco.qbsolv4j.qubo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Qubo {

  public static double[][] readFile(Path path) throws IOException {
    double[][] qubo = null;
    int coupleNum = 0;
    int nodeNum = 0;
    int maxNodes;
    for (String line : Files.readAllLines(path)) {
      String[] params = line.split(" ");
      if (params[0].equals("c")) {
        // comment line
        continue;
      }
      if (params[0].equals("p")) {
        maxNodes = Integer.parseInt(params[3]);
        nodeNum = Integer.parseInt(params[4]);
        coupleNum = Integer.parseInt(params[5]);
        qubo = new double[maxNodes][maxNodes];
      } else {
        if (qubo == null) {
          throw new NullPointerException("p line must come before input lines");
        }
        int i = Integer.parseInt(params[0]);
        int j = Integer.parseInt(params[1]);
        double value = Double.parseDouble(params[2]);

        if (params[0].equals(params[1])) {
          // node
          qubo[i][i] = value;
          nodeNum--;
        } else {
          // coupler
          if (i > j) {
            throw new IOException("couplers first value must be > second value");
          }
          qubo[i][j] = value;
          coupleNum--;
        }
      }
    }
    if (nodeNum != 0) {
      throw new IOException("Number of nodes is invalid.");
    }
    if (coupleNum != 0) {
      throw new IOException("Number of couplers is invalid.");
    }
    if (qubo == null) {
      throw new IOException("QUBO is not defined in the given file.");
    }

    return qubo;
  }
}

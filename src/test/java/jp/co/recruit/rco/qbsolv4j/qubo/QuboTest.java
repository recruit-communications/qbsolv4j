package jp.co.recruit.rco.qbsolv4j.qubo;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

public class QuboTest {

  @Test
  public void readFile() throws Exception {
    Path path = Paths.get(getClass().getResource("/bqp50_1.qubo").toURI());
    double[][] qubo = Qubo.readFile(path);
    Assert.assertEquals(qubo.length, 50);
    Assert.assertEquals(qubo[0].length, 50);
  }
}
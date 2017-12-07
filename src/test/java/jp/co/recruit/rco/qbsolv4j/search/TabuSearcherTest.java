package jp.co.recruit.rco.qbsolv4j.search;

import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

public class TabuSearcherTest {

  @Test
  public void valueIndexSort() throws Exception {
    Random random = new Random();
    int n = 100;
    double[] values = new double[n];
    for (int i = 0; i < n; i++) {
      values[i] = random.nextDouble() * random.nextInt();
    }

    int[] index = TabuSearcher.valueIndexSort(values, n);
    for (int i = 0; i < n - 1; i++) {
      Assert.assertTrue(values[index[i]] >= values[index[i]]);
    }

  }

}
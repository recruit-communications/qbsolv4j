package jp.co.recruit.rco.qbsolv4j.args;

public class Arguments {


  final String inputFilepath;
  final String outputFilepath;
  final boolean findMaximum;
  final int subMatrixSize;
  final int repeats;
  final long timeoutSeconds;
  final double targetValue;
  final int tabuListLength;

  public Arguments(String inputFilepath, String outputFilepath, boolean findMaximum,
      int subMatrixSize, int repeats, long timeoutSeconds, double targetValue, int tabuListLength) {
    this.inputFilepath = inputFilepath;
    this.outputFilepath = outputFilepath;
    this.findMaximum = findMaximum;
    this.subMatrixSize = subMatrixSize;
    this.repeats = repeats;
    this.timeoutSeconds = timeoutSeconds;
    this.targetValue = targetValue;
    this.tabuListLength = tabuListLength;
  }
}

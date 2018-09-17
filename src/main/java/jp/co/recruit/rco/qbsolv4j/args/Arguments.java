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

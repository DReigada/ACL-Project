package helpers;

import java.util.stream.IntStream;
import java.util.stream.Stream;

final public class StreamHelpers {

  public static Stream<Integer> range(int i, int f) {
    return IntStream.range(i, f).boxed();
  }

  public static Stream<Integer> rangeClosed(int i, int f) {
    return IntStream.rangeClosed(i, f).boxed();
  }

}

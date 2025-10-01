package extension;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TestResult {
  String name;
  String status;
  long duration;
  String error;
  String screenshot;
}

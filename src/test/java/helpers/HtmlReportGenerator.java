package helpers;

import extension.TestResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HtmlReportGenerator {
  public static void generateReport(List<TestResult> results, String path) {

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
      writer.write("<html><head><title>Test Report</title><style>");
      writer.write("table { border-collapse: collapse; width: 100%; }");
      writer.write("th, td { padding: 8px; text-align: left; border: 1px solid #ddd; }");
      writer.write("tr:hover { background-color: #f5f5f5; }");
      writer.write("</style></head><body>");
      writer.write("<h1>Test Results</h1>");
      writer.write("<table><tr><th>Test</th><th>Status</th><th>Duration (ms)</th><th>Error</th><th>Screenshot</th></tr>");

      for (TestResult result : results) {
        writer.write(String.format(
            "<tr><td>%s</td><td style='color: %s;'>%s</td><td>%d</td><td>%s</td><td>%s</td></tr>",
            result.getName(),
            result.getStatus().equals("Passed") ? "green" : "red",
            result.getStatus(),
            result.getDuration(),
            result.getError() != null ? result.getError() : "-",
            result.getScreenshot() != null ? "<a href='" + result.getScreenshot() + "'>View</a>" : "-"
        ));
      }

      writer.write("</table></body></html>");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

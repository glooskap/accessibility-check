import com.deque.html.axecore.results.CheckedNode;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.results.Results;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class AccessibilityTest {
    public static void main(String[] args) {
        // Set up WebDriver
        System.setProperty("webdriver.chrome.driver", "chromedriver-win64\\chromedriver.exe"); // to download the chrome driver and add it as a resource in the project
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        // HTML content as a string
        String htmlContent;
/*        htmlContent =
                //" <html>   " +
                "<table class=\"TblBottom\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"99%\"> \n" +
                "     <tbody>\n" +
                "      <tr> \n" +
                "       <td colspan=\"2\" class=\"Cl-Level1\"> <a name=\"ref_period\"></a><a name=\"ref_period1730299226957\"></a>\n" +
                "        <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"> \n" +
                "         <tbody>\n" +
                "          <tr> \n" +
                "           <td class=\"Cl-Level1-NoBorder\">5. Reference Period</td>\n" +
                "           <td align=\"right\"><a href=\"#\" class=\"DocCommon\">Top</a></td> \n" +
                "          </tr> \n" +
                "         </tbody>\n" +
                "        </table> </td> \n" +
                "      </tr> \n" +
                "      <tr> \n" +
                "       <td class=\"Cl-Content\">\n" +
                "        <table border=\"1\" cellspacing=\"0\" cellpadding=\"3\"> \n" +
                "         <thead> \n" +
                "          <tr> \n" +
                "           <td class=\"notConcatenable\">aaaaaa</td> \n" +
                "           <td class=\"notConcatenable\">bbbbbbb</td> \n" +
                "          </tr> \n" +
                "         </thead> \n" +
                "         <tbody> \n" +
                "          <tr> \n" +
                "           <td> </td> \n" +
                "           <td> </td> \n" +
                "          </tr> \n" +
                "         </tbody> \n" +
                "        </table></td> \n" +
                "      </tr> \n" +
                "     </tbody>\n" +
                "    </table> "; // +
                //" </html>";
                */

        try {
            htmlContent = new String(
                    Files.readAllBytes(Paths.get("src\\main\\resources\\preview.htm")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Load the HTML content directly
        driver.get("data:text/html;charset=utf-8," + htmlContent);

        // Set the HTML content using JavaScript
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("document.write(arguments[0])", htmlContent);

        // Run accessibility checks
        AxeBuilder builder = new AxeBuilder();
        // Example tags for WCAG A and AA levels
//        builder.withTags(new ArrayList<>(
//                Arrays.asList("wcag2a", "wcag2aa", "wcag21a", "wcag21aa")));
        builder.disableRules(Arrays.asList("region", "landmark-one-main", "landmark-no-duplicate-banner"
                , "landmark-no-duplicate-contentinfo", "html-has-lang", "html-lang-valid", "page-has-heading-one"
                , "bypass", "color-contrast", "frame-title", "frame-tested", "document-title"));
        // Example rules
        //builder.withRules(new ArrayList<>(
        //        Arrays.asList("color-contrast", "image-alt")));
        Results result = builder.analyze(driver);

        // Print the number of accessibility issues found
        System.out.println();
        System.out.println("Accessibility issues found: " + result.getViolations().size());
        System.out.println("Accessibility issues passed: " + result.getPasses().size());
        System.out.println("Accessibility issues incomplete: " + result.getIncomplete().size());
        System.out.println("Accessibility issues inapplicable: " + result.getInapplicable().size());
        System.out.println();

        System.out.println("--- Passes ---");
        if (result.getPasses().size() > 0) {
            result.getPasses().stream()
                    .forEach(rule -> System.out.println(rule.getId() + " | " + rule.getDescription()));
        } else {
            System.out.println("no passes");
        }
        System.out.println();

        System.out.println("--- Incomplete ---");
        if (result.getIncomplete().size() > 0) {
            result.getIncomplete().stream()
                .forEach(rule -> System.out.println(rule.getId() + " | " + rule.getDescription()));
        } else {
            System.out.println("no incomplete");
        }
        System.out.println();

        System.out.println("--- Violations ---");
        if (result.getViolations().size() > 0) {
            result.getViolations().stream()
                .flatMap(rule -> rule.getNodes().stream())
                .forEach(node -> System.out.println(node.getImpact() + " | " + node.getFailureSummary() + "\n" + node.getHtml()));
        } else {
            System.out.println("no violations");
        }

        // Clean up
        driver.quit();
    }
}
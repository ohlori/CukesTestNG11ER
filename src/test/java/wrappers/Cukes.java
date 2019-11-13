package wrappers;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.TestNGCucumberRunner;
import cucumber.api.testng.AbstractTestNGCucumberTests;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;

import driver.util;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


@CucumberOptions(
        features = "src/test/resources/features/LoginProfile.feature",
        glue = {"stepdefs"},
        tags = {"@LoginProfile"},
        monochrome=true,
        plugin = {
        		//"html:target/cucumber-reports/cucumber-pretty",
                //"json:target/cucumber-reports/json-reports/CucumberTestReport.json",
                //"com.vimalselvam.cucumber.listener.ExtentCucumberFormatter:path/report.html"
                //"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
                //"rerun:target/cucumber-reports/rerun-reports/rerun.txt"
        })

public class Cukes extends AbstractTestNGCucumberTests{
    private TestNGCucumberRunner testNGCucumberRunner;

    
    WebDriver driver = null;
   

    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws Exception {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
        util.driver = util.getDriver();
    }

    @Test(groups = "cucumber", description = "Runs Cucumber Feature", dataProvider = "features")
    public void scenario(PickleEventWrapper pickleEvent, CucumberFeatureWrapper cucumberFeature) throws Throwable {
        testNGCucumberRunner.runScenario(pickleEvent.getPickleEvent());
        util.featureName = cucumberFeature.toString();
    }

    @DataProvider
    public Object[][] features() {
        return testNGCucumberRunner.provideScenarios();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        testNGCucumberRunner.finish();
        util.closeBrowser(util.driver);
    }
}

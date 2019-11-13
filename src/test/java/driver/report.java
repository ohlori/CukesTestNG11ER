package driver;

import java.util.HashMap;
import java.util.Map;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentBDDReporter;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.*;
import com.aventstack.extentreports.reporter.configuration.Theme;

        /* OB: extentTestMap holds the information of thread ids and ExtentTest instances.
                ExtentReports instance created by calling getReporter() method.
                At getTest() method, return ExtentTest instance in extentTestMap by using current thread id.
                At endTest() method, test ends and ExtentTest instance got from extentTestMap via current thread id.
                At startTest() method, an instance of ExtentTest created and put into extentTestMap with current thread id.
         */

public class report {
    static Map<Integer, ExtentTest> extentTestMap = new HashMap<Integer, ExtentTest>();
    static Map<Integer, ExtentTest> extentNodeMap = new HashMap<Integer, ExtentTest>();
    private static ExtentReports extent = getReporter();
    private static ExtentTest test;
    
    public static synchronized ExtentTest getTest() {
        return (ExtentTest)extentTestMap.get((int) (long) (Thread.currentThread().getId()));
    }
    
    public static synchronized ExtentTest getStep() {
        return (ExtentTest)extentNodeMap.get((int) (long) (Thread.currentThread().getId()));
    }

    public static synchronized void endTest() {
    	extent.flush();
    	
    }

    public static synchronized ExtentTest startTest(String testName, String desc) {
    	extent.setAnalysisStrategy(AnalysisStrategy.TEST);
        test = extent.createTest(testName, desc);
        extentTestMap.put((int) (long) (Thread.currentThread().getId()), test);
        return test;
    }
    
    
    public static synchronized ExtentTest createNode(ExtentTest test, String scenarioName, String stepName) {
    	ExtentTest node = test.createNode(stepName);
    	node.assignCategory(scenarioName);
        extentNodeMap.put((int) (long) (Thread.currentThread().getId()), node);
        return node;
    }
   
    public synchronized static ExtentReports getReporter() {
    	ExtentHtmlReporter reporter = new ExtentHtmlReporter(util.reportPath);
    	reporter.loadXMLConfig(util.XMLconfigs);
    	extent = new ExtentReports();
    	extent.attachReporter(reporter);
    	extent.setAnalysisStrategy(AnalysisStrategy.TEST);
		return extent;
    }
}
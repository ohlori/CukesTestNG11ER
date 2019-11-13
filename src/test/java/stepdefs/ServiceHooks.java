package stepdefs;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import cucumber.api.PickleStepTestStep;
import cucumber.api.Scenario;
import cucumber.api.TestCase;
import cucumber.api.java.After;
import cucumber.api.java.AfterStep;
import cucumber.api.java.Before;
import cucumber.api.java.BeforeStep;
import driver.report;
import driver.util;


public class ServiceHooks {
	PickleStepTestStep currentStepDef;
	private int stepIndex = 0;
	String currentStepText;
	int counter;
	
	public static volatile int scenarioCount = 0;
	
	@Before
	public void setUpScenario(Scenario scenario){
		counter = (scenarioCount += 1);
		util.scenario = scenario.getName();
	    report.startTest("Scenario No. " + counter + " : " + util.scenario, "XXXXX ---- SERVICE HOOK-@BEFORE");
	}

	@After
	public void afterScenario(Scenario scenario){
		report.endTest();
	 }

	@BeforeStep
	public void getStepName(Scenario scenario) throws Exception {
		Field f = scenario.getClass().getDeclaredField("testCase");
		f.setAccessible(true);
		TestCase r = (TestCase) f.get(scenario);
		
		// need to filter out before/after hooks
		List<PickleStepTestStep> stepDefs = r.getTestSteps()
			.stream()
			.filter(x -> x instanceof PickleStepTestStep)
			.map(x -> (PickleStepTestStep) x)
			.collect(Collectors.toList());
		    
			currentStepDef = stepDefs.get(stepIndex);
			currentStepText = currentStepDef.getStepText();
		    util.currentStep = currentStepText;
		    util.stepForReport = currentStepText;
		    //System.out.println(currentStep);
		    
		    
		    //LOG STEP IN EXTENT REPORTS
			String category = "[SCENARIO "+counter+"]  "+ util.scenario;
		    category=category.replaceAll(" ", "&nbsp;");
		    report.createNode(report.getTest(), category, util.stepForReport);
	}

	@AfterStep
	public void afterStep(Scenario scenario) {
		stepIndex += 1;
		
        if (scenario.isFailed()) {
            try {
                // Code to capture and embed images in test reports (if scenario fails)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
}

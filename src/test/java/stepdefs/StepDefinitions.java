package stepdefs;


import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import driver.report;
import driver.util;

import java.util.ArrayList;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class StepDefinitions {
	ExtentReports reports;
	ExtentTest logger;

	@Given("^Sheetname for datasheet is \"([^\"]*)\"$")
    public void sheetname_for_datasheet_is(String sheetName) {
		util.sheetName = sheetName;
    }
	
	 @When("^I fill in the username field with \"([^\"]*)\"$")
	 public void i_fill_in_the_username_field_with(String arg1) throws Throwable {
		 util.logPass(util.driver, "SAMPLE SCREENSHOT TEST 2", report.getStep(), true);
		 ArrayList<String> testData = util.stepDescFull(arg1);
		 System.out.println(testData);
		 
	 }
	    
	 @And("^I fill in the password field with \"([^\"]*)\"$")
	 public void i_fill_in_the_password_field_with(String arg1) throws Throwable {
		 util.logPass(util.driver, "SAMPLE SCREENSHOT TEST 3", report.getStep(), true);
		 ArrayList<String> testData = util.stepDescFull(arg1);
		 System.out.println(testData);
	 }
	 
	 
    @Given("^I am on the \"([^\"]*)\" page on URL \"([^\"]*)\"$")
    public void i_am_on_the_page_on_URL(String arg1, String arg2) throws Throwable {
    	
    }

    @When("^I fill in \"([^\"]*)\" with \"([^\"]*)\"$")
    public void i_fill_in_with(String arg1, String arg2) throws Throwable {
    	//System.out.println(util.get(util.sheetName, util.scenario, arg1));
    	ArrayList<String> testData = util.stepDescFull(arg1, arg2);
    	System.out.println(testData);
    }
    

    @When("^I click on the \"([^\"]*)\" button$")
    public void i_click_on_the_button(String arg1) throws Throwable {
    	//System.out.println(util.get(util.sheetName, util.scenario, arg1));
    	ArrayList<String> testData = util.stepDescFull(arg1);
		 System.out.println(testData);
    }

    @Then("^I should see \"([^\"]*)\" message$")
    public void i_should_see_message(String arg1) throws Throwable {
    	//System.out.println(util.get(util.sheetName, util.scenario, arg1));
    	ArrayList<String> testData = util.stepDescFull(arg1);
		System.out.println(testData);
    }

    @Then("^I should see the \"([^\"]*)\" button$")
    public void i_should_see_the_button(String arg1) throws Throwable {
    	//System.out.println(util.get(util.sheetName, util.scenario, arg1));
    	ArrayList<String> testData = util.stepDescFull(arg1);
		 System.out.println(testData);
       
    }

}

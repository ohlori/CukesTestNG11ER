package driver;

import java.awt.AWTException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

//import org.openqa.selenium.interactions.internal.Coordinates;
//import org.openqa.selenium.internal.Locatable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;


public class base {
	/**
	 *  Enters the text in the text field
	 * @param driver - Webdriver
	 * @param locator - Object locator
	 * @param text - String that needs to be entered
	 * @param objName - Object Name
	 * @throws IOException 
	 */
	public static boolean enterText(WebDriver driver, String objName, By locator, String text,  ExtentTest logger, 
			boolean wholeScreen) throws IOException{
		
		try {
			scrollObj(driver, locator, logger);
			WebElement elm = driver.findElement(locator);
			if (!(elm==null)){
				elm.clear();
				elm.click();
				elm.sendKeys(text);
				
				String textvalue = elm.getAttribute("value");
				
				if (text.equals(textvalue)){
					util.logInfo("Successfully entered the text: " + text+ " in the field: "+ objName, logger);
					return true;
				}
			} else{
				util.logFail(driver, "Element not found: "+ objName, logger, wholeScreen);	
				return false;
			}
			
		}catch(NoSuchElementException e){
			util.logFail(driver, "Element not found: "+ objName, logger, true);	
			return false;
		} catch(InvalidElementStateException e) {
			util.logFail(driver, "Element is not proper state:" + objName, logger, true);
			return false;
		} catch(Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger); 
			return false;
		}
		return false;
	}
	
	/**
	 * Select a string value on the list box. It will wait for 30 seconds for the list box to be loaded
	 * @param driver
	 * @param locator
	 * @param lstValue
	 * @param dropdownName
	 * @param logger
	 * @throws IOException 
	 */
	public static String selectValueFromListByText(WebDriver driver, final By locator, String lstValue, String dropdownName, ExtentTest logger,
			boolean wholeScreen) throws IOException{
		
		String textvalue="";
		try{		
		    waitListLoad(driver, locator);
			WebElement elm = driver.findElement(locator);
			if (elementExist(driver, locator)){
				Select s = new Select(elm);
				s.selectByVisibleText(lstValue);
				util.logInfo("Selected the list value : "+ lstValue, logger);
				
				textvalue= s.getFirstSelectedOption().getAttribute("innerText").trim();
			}
			else				
				util.logFail(driver, "Element not found : "+ dropdownName, logger, wholeScreen);

		}catch(Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
		}
		return textvalue;
	}
	
	/**
	 * To randomly selecting value from the list drop down
	 * This is useful for dynamic test data
	 * @param driver - Webdriver
	 * @param locator - Object locator
	 * @param lstIndex - Index of the list value to be select
	 * @param dropdownName - Dropdown Name
	 * @throws IOException 
	 */
	public static String selectValueFromListRandomly(WebDriver driver, By obj, String dropdownName, ExtentTest logger, boolean wholeScreen) throws IOException {
		String textvalue="";
		
		try {
			waitListLoad(driver, obj);
			if (elementExist(driver, obj)){
				WebElement elm = driver.findElement(obj);
				Select s = new Select(elm);
				int r = randomNumInt(s.getOptions().size());
				
				if(s.getOptions().size()==1){
					s.selectByIndex(0);
				} else {
					s.selectByIndex(r);
				}
		
				textvalue= s.getFirstSelectedOption().getAttribute("innerText").trim();
				if (textvalue.equals("") || textvalue.equals("undefined")) while(true){
						int o = randomNumInt(s.getOptions().size());
						s.selectByIndex(o);
						WebElement option = s.getFirstSelectedOption();
						textvalue= option.getText().trim();
						if (!textvalue.equals("") && !textvalue.equals("undefined")){
							s.selectByIndex(r);
							util.logInfo("Selected the list value: "+ textvalue, logger);
							break;
						}
				} else {
					WebElement option = s.getFirstSelectedOption();
					textvalue= option.getText().trim();
					util.logInfo("Selected the list value : "+ textvalue, logger);
				}
			}else{
				util.logFail(driver, "Element not found: "+ dropdownName,logger, wholeScreen);
			}
		} catch(Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
		}
		return textvalue;
	}
	
	
	/**
	 *  To select the list value from the dropdown using the list value
	 * @param driver - Webdriver
	 * @param locator - Object locator
	 * @param lstIndex - Index of the list value to be select
	 * @param dropdownName - Dropdown Name
	 * @throws IOException 
	 */
	public static void selectValueFromListByIndex(WebDriver driver, By locator, int lstIndex, String dropdownName, ExtentTest logger, 
			boolean wholeScreen) throws IOException{
		
		try{
			WebElement elm = driver.findElement(locator);
			
			if (!(elm==null)){
				Select s = new Select(elm);
				s.selectByIndex(lstIndex);
				String textvalue= s.getFirstSelectedOption().getText().trim();
				util.logInfo("Selected [INDEX:"+lstIndex+" | VALUE: "+textvalue+"]", logger);
			} else{
				util.logFail(driver, "Element not found: "+ dropdownName, logger,  wholeScreen);
			}
		}
		catch(Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
		}
	}
	
	
	/**
	 * Click object and catch errors if there are
	 * @param driver
	 * @param objName
	 * @param locatorToClick
	 * @param objNameToWait
	 * @param locatorToWait
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean click(WebDriver driver, String objName, By objToClick, ExtentTest logger) throws IOException {
		try{
			//to scroll in view
			scrollObj(driver, objToClick, logger);
			
			//Wait for the element before clicking
			if (base.waitForElBoolean(driver, objToClick)){
				driver.findElement(objToClick).click();
				base.waitUntilPageLoad(driver);
				if(!objName.equals("")) {
					util.logPass("Element Clicked: "+ objName, logger);
				}
				return true;
			}  else {
				util.logFail(driver, "Element not found: "+ objName, logger, true);
			}
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not found: "+ objName, logger, true);	
		} catch(InvalidElementStateException e){			
			util.logFail(driver, "Element is not proper state:" + objName, logger, true);
		} catch(Exception e){
			util.logFail(driver, "Thrown Exceptions: "+ e.getMessage(), logger, true);
		}
		return false; 
	}
	
	/**
	 * Click WebElement and catch errors if there are
	 * @param driver
	 * @param objName
	 * @param locatorToClick
	 * @param objNameToWait
	 * @param locatorToWait
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean click(WebDriver driver, String objName, WebElement objToClick, ExtentTest logger) throws IOException {
		
		try{
			//to scroll in view
			scrollObj(driver, objToClick, logger);
			
			//Wait for the element before clicking
			if (base.waitForElBoolean(driver, objToClick)){
				objToClick.click();
				base.waitUntilPageLoad(driver);
				util.logPass("Element Clicked: "+ objName, logger);
				return true;
			} else {
				util.logFail(driver, "Element not found: "+ objName, logger, true);	
				return false;
			}
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not found: "+ objName, logger, true);	
		} catch(InvalidElementStateException e){			
			util.logFail(driver, "Element is not proper state:" + objName, logger, true);
		} catch(Exception e){
			util.logFail(driver, "Thrown Exceptions: "+ e.getMessage(), logger, true);
		}
		return false; 
		
	}
	
	/**
	 * Click object and wait until another element is visible (can be nothing to be waited)
	 * @param driver
	 * @param objName
	 * @param locatorToClick
	 * @param objNameToWait
	 * @param locatorToWait
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean clickWait(WebDriver driver, String objName, By objToClick, String objNameToWait,
			By objToWait, ExtentTest logger) throws IOException {
		
		boolean found = false;
		
		try{
			//to scroll in view
			scrollObj(driver, objToClick, logger);
			
			//Wait for the element before clicking
			if (base.waitForElBoolean(driver, false, objName, objToClick, logger)){
				driver.findElement(objToClick).click();
				base.waitUntilPageLoad(driver);
				//validate if the locator to wait exist
				
				if(!objToWait.equals(null)){
					if (waitForElBoolean(driver, objToWait)){
						//Reporting clicked element and wait element
						if(!objName.equals("") && !objNameToWait.equals("")){
							util.logPass("Clicked Element: "+ objName+ ". Element Found: "+objNameToWait, logger);
							
						//Reporting clicked element
						} else if(!objName.equals("") && objNameToWait.equals("")){
							util.logPass("Clicked Element: "+ objName, logger);
						}
						found=true;
					}
				} else {
					if(!objName.equals("")){
						util.logPass("Clicked Element: "+ objName, logger);
					}
				}
			}
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not found: "+ objName, logger, true);	
			found=false;
		} catch(InvalidElementStateException e){			
			util.logFail(driver, "Element is not proper state:" + objName, logger, true);
			found=false;
		} catch(Exception e){
			util.logFail(driver, "Thrown Exceptions: "+ e.getMessage(), logger, true);
			found=false;
		} 
		return found;
	}
	
	/**
	 * Click object and wait until another element is visible (can be nothing to be waited)
	 * @param driver
	 * @param objName
	 * @param locatorToClick
	 * @param objNameToWait
	 * @param locatorToWait
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean clickWait(WebDriver driver, String objName, WebElement el, String objNameToWait,
			By objToWait, ExtentTest logger) throws IOException {
		
		boolean found = false;
		
		try{
			//to scroll in view
			scrollObj(driver, el, logger);
			
			//Wait for the element before clicking
			if (base.waitForElBoolean(driver, false, objName, el, logger)){
				el.click();
				base.waitUntilPageLoad(driver);
				//validate if the locator to wait exist
				
				if(!objToWait.equals(null)){
					if (waitForElBoolean(driver, objToWait)){
						//Reporting clicked element and wait element
						if(!objName.equals("") && !objNameToWait.equals("")){
							util.logPass("Clicked Element: "+ objName+ ". Element Found: "+objNameToWait, logger);
							
						//Reporting clicked element
						} else if(!objName.equals("") && objNameToWait.equals("")){
							util.logPass("Clicked Element: "+ objName, logger);
						}
						found=true;
					}
				} else {
					if(!objName.equals("")){
						util.logPass("Clicked Element: "+ objName, logger);
					}
				}
			}
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not found: "+ objName, logger, true);	
			found=false;
		} catch(InvalidElementStateException e){			
			util.logFail(driver, "Element is not proper state:" + objName, logger, true);
			found=false;
		} catch(Exception e){
			util.logFail(driver, "Thrown Exceptions: "+ e.getMessage(), logger, true);
			found=false;
		} 
		return found;
	}
	
	/**
	 * Click object and wait until another element is visible (can be nothing to be waited)
	 * @param driver
	 * @param objName
	 * @param locatorToClick
	 * @param objNameToWait
	 * @param locatorToWait
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean clickWait(WebDriver driver, String objName, WebElement el, String objNameToWait,
			WebElement objToWait, ExtentTest logger) throws IOException {
		
		boolean found = false;
		
		try{
			//to scroll in view
			scrollObj(driver, el, logger);
			
			//Wait for the element before clicking
			if (base.waitForElBoolean(driver, false, objName, el, logger)){
				el.click();
				base.waitUntilPageLoad(driver);
				//validate if the locator to wait exist
				
				if(!objToWait.equals(null)){
					if (waitForElBoolean(driver, objToWait)){
						//Reporting clicked element and wait element
						if(!objName.equals("") && !objNameToWait.equals("")){
							util.logPass("Clicked Element: "+ objName+ ". Element Found: "+objNameToWait, logger);
							
						//Reporting clicked element
						} else if(!objName.equals("") && objNameToWait.equals("")){
							util.logPass("Clicked Element: "+ objName, logger);
						}
						found=true;
					}
				} else {
					if(!objName.equals("")){
						util.logPass("Clicked Element: "+ objName, logger);
					}
				}
			}
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not found: "+ objName, logger, true);	
			found=false;
		} catch(InvalidElementStateException e){			
			util.logFail(driver, "Element is not proper state:" + objName, logger, true);
			found=false;
		} catch(Exception e){
			util.logFail(driver, "Thrown Exceptions: "+ e.getMessage(), logger, true);
			found=false;
		} 
		return found;
	}
	
	
	
	/**
	 * Click object and wait until another element is visible (can be nothing to be waited)
	 * @param driver
	 * @param objName
	 * @param locatorToClick
	 * @param objNameToWait
	 * @param locatorToWait
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean clickOpenTabWait(WebDriver driver, String objName, WebElement el, String objNameToWait,
			By objToWait, ExtentTest logger) throws IOException {
		
		boolean found = false;
		
		try{
			//to scroll in view
			scrollObj(driver, el, logger);
			
			//Wait the new tab opened
			String currentWindow = getTheCurrentWindowHandle(driver, logger);
			
			//Click the element
			if (base.waitForElBoolean(driver, objName, el, logger)){
				el.click();
					
				if(!objName.equals("")){
					util.logPass("Clicked Element: "+ objName, logger);
				}
				
				
				//Closing the newly opened tab
				Set<String> totalwindows = driver.getWindowHandles();
				if(totalwindows.size()>1) {
					switchToNewlyOpenedWindow(driver, currentWindow, logger);
					
					//validate if the locator to wait exist
					if(!objToWait.equals(null)){
						if (waitForElBoolean(driver, objNameToWait, objToWait, logger)){
							if (!objNameToWait.equals("")){
								util.logPass("Element Found: "+ objNameToWait, logger);
							}
							found=true;
						}
					}
				}
			}
		} catch(NoSuchElementException e){
			util.logFail(driver, "Element not found: "+ objName, logger, true);	
			found=false;
		} catch(InvalidElementStateException e){			
			util.logFail(driver, "Element is not proper state:" + objName, logger, true);
			found=false;
		} catch(Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
			found=false;
		} 
		return found;
	}
	
	
	/**
	 * Click element and wait until another element is visible (can be nothing to be waited)
	 * @param driver
	 * @param objName
	 * @param locatorToClick
	 * @param objNameToWait
	 * @param locatorToWait
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean clickWaitUntilGone(WebDriver driver, String objName, By objToClick, String objNameToWait,
			By objToWait, ExtentTest logger) throws InterruptedException, IOException{
		
		boolean found = false;
		try{
			
			//to scroll in view
			scrollObj(driver, objToClick, logger);
			if (base.waitForElBoolean(driver, objName, objToClick, logger)){
				driver.findElement(objToClick).click();
				
				Thread.sleep(500);
				if(!objName.equals("")){
					util.logPass("Clicked Element: "+ objName, logger);
				}
				
				found = waitUntilNotVisible(driver, objToWait);
				
				//validate if the locator to wait exist
				if (driver.findElements(objToWait).size()>0){
					found = false;
				}
				
			}
		} catch (Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
		}
		return found;
	}
	
	
	/**
	 * Click element and wait until another element is visible (can be nothing to be waited)
	 * @param driver
	 * @param objName
	 * @param locatorToClick
	 * @param objNameToWait
	 * @param locatorToWait
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean clickWaitUntilGone(WebDriver driver, String objName, WebElement objToClick, String objNameToWait,
			By objToWait, ExtentTest logger) throws InterruptedException, IOException{
		
		boolean found = false;
		try{
			
			//to scroll in view
			scrollObj(driver, objToClick, logger);
			if (base.waitForElBoolean(driver, false, objName, objToClick, logger)){
				objToClick.click();
				
				base.waitUntilPageLoad(driver);
				if(!objName.equals("")){
					util.logPass("Clicked Element: "+ objName, logger);
				}
				
				found = waitUntilNotVisible(driver, objToWait);
				
				//validate if the locator to wait exist
				if (driver.findElements(objToWait).size()>0){
					found = false;
				}
				
			}
		} catch (Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
		}
		return found;
	}
	
	
	
	/**
	 * Validate the list options found in web element
	 * @param driver
	 * @param objName - Listbox name
	 * @param element - listbox element
	 * @param text - expected listbox values seperated by comma (,)
	 * @param logger
	 * @throws IOException 
	 */
	public static void validate_list_option(WebDriver driver, String objName, By element,  String text, ExtentTest logger) throws IOException	{
		try {
			waitListLoad(driver, element);
			
			scrollObj(driver, element, logger);
			WebElement dropdown = driver.findElement(element);
			dropdown.click();
			
			Select select = new Select(dropdown);
			List <WebElement> options = select.getOptions();
			String [] expected_dropdownOptions = text.split(",");
			
			ArrayList <String> listOptions = new ArrayList<String>();
			ArrayList <String> not_found = new ArrayList<String>();
			ArrayList <String> excess = new ArrayList<String>();
			
			//FINDING OPTIONS FOUND IN LISTBOX vs. EXPECTED OPTIONS (Data Sheet)
			for(WebElement we: options){
				String temp = we.getAttribute("innerText").trim();
				String temp2 = we.getAttribute("value").trim();
				listOptions.add(temp);
			
				if(!findStringInArray(temp, expected_dropdownOptions) && !temp2.equalsIgnoreCase("undefined") && !temp2.equalsIgnoreCase("")){
					excess.add(temp);
				}
			}
			
			//FINDING EXPECTED OPTIONS (Data Sheet) vs. LISTBOX FOUND
			for(int x=0; x<expected_dropdownOptions.length;x++){
				String temp = expected_dropdownOptions[x].toString();
				
				if(!findStringInArrayList(temp, listOptions)){
					not_found.add(temp);
				}
			}
			
			if((not_found.size()==0) && (excess.size()==0)){
				util.logPass(objName+" options are validated! [EXPECTED: "+text+"][FOUND: "+listOptions+"]" , logger);
			} 
			
			if(not_found.size()>0){
				util.logFail("There are MISSING options: "+not_found, logger);
			} 
			
			if(excess.size()>0){
				util.logFail("There are EXCESS options: "+excess, logger);
			}
		} catch(Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
		}
	}
	
	//=================================================================================================================//
	//**********************************************VISIBILITY OF ELEMENT**********************************************//
	
	/**
	 *  Verifies that element is present or not 
	 * @param driver - WebDriver 
	 * @param obj - Locator
	 * @param ObjName - Object Name 
	 * @param logger - ExtentTest Logger
	 * @return
	 * @throws IOException 
	 */
	public boolean verifyObjectExist(WebDriver driver, String ObjName, By obj, ExtentTest logger, boolean wholeScreen) throws IOException{		
		try{
			WebElement el = driver.findElement(obj);
			if (el.isDisplayed()){
				util.logPass(ObjName + " is displayed",  logger);
				return true;
			} else {
				util.logFail(driver, ObjName+" is not displayed", logger, wholeScreen);
				return false;
			}			
		} catch(NoSuchElementException e){
			util.logFail(driver, "Element not found", logger, true);
			return false;
		} catch(Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
			return false;
		}
	}
	
	
	/**
	 * Wait for 60 seconds the page is fully loaded without waiting for any element
	 * Log and return false if not
	 * @param driver
	 * @param obj - Locator
	 * @throws IOException 
	 */
	public static boolean waitUntilPageLoad(WebDriver driver) throws IOException{
		try {
			new WebDriverWait(driver, 120).until((ExpectedCondition<Boolean>) wd ->
	        ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("loaded") ||
	        ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
			
			return true;
		} catch (Exception e) {
			return false;
		}	
	}
	
	
	
	
	/**
	 * Wait for 60 seconds until the element is visible in the DOM and return true if found else false
	 * No reports / logs
	 * @param driver
	 * @param el - Web element
	 * @param logger - ExtentTest
	 */
	public static boolean waitForElBoolean(WebDriver driver, WebElement el){
		try {
			//Wait for the page to successfully load without waiting for any element
			waitUntilPageLoad(driver);
			
			WebDriverWait wait = new WebDriverWait(driver, 60, 2);
			wait.until(ExpectedConditions.visibilityOf(el));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	
	/**
	 * Wait for 60 seconds until the object is visible in the DOM and return true if found else false
	 * With reports / logs and return false if not
	 * @param driver
	 * @param obj - Locator
	 * @throws IOException 
	 */
	public static boolean waitForElBoolean(WebDriver driver, boolean show, String objName, By obj, ExtentTest logger) throws IOException{
		try {
			//Wait for the page to successfully load without waiting for any element
			waitUntilPageLoad(driver);
			WebDriverWait wait = new WebDriverWait(driver,60,2);
			wait.until(ExpectedConditions.visibilityOfElementLocated(obj));
			if(show==true) {
				util.logPass("Element found: "+objName, logger);
			}
			return true;
		}  catch(TimeoutException e) {
			util.logFail(driver, "Session timed out without element ["+objName+"] being present.", logger, true);
			return false;
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not visible: "+objName, logger, true);
			return false;
		
		} catch (Exception e) {
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
			return false;
		}
	}
	
	
	/**
	 * Wait for 60 seconds until the object is visible in the DOM and return true if found else false
	 * With reports / logs and return false if not
	 * @param driver
	 * @param obj - Locator
	 * @throws IOException 
	 */
	public static boolean waitForElBoolean(WebDriver driver,boolean show, String objName, WebElement el, ExtentTest logger) throws IOException{
		try {
			//Wait for the page to successfully load without waiting for any element
			waitUntilPageLoad(driver);
			
			WebDriverWait wait = new WebDriverWait(driver,60,2);
			wait.until(ExpectedConditions.visibilityOf(el));
			if(show==true) {
				util.logPass("Element found: "+objName, logger);
			}
			return true;
		} catch(TimeoutException e) {
			util.logFail(driver, "Session timed out without element ["+objName+"] being present.", logger, true);
			return false;
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not visible: "+objName, logger, true);
			return false;
		
		} catch (Exception e) {
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
			return false;
		}	
	}
	
	/**
	 * Wait for 60 seconds until the object is visible in the DOM and return true if found else false
	 * Log and return false if not
	 * @param driver
	 * @param obj - Locator
	 * @throws IOException 
	 */
	public static boolean waitForElBoolean(WebDriver driver, String objName, By obj, ExtentTest logger) throws IOException{
		try {
			//Wait for the page to successfully load without waiting for any element
			waitUntilPageLoad(driver);
			
			WebDriverWait wait = new WebDriverWait(driver,60,2);
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(obj));
			return true;
		} catch(TimeoutException e) {
			util.logFail(driver, "Session timed out without element ["+objName+"] being present.", logger, true);
			return false;
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not visible: "+objName, logger, true);
			return false;
		} catch(NoSuchElementException e){
			util.logFail(driver, "Element not found: "+objName, logger, true);
			return false;
		} catch (Exception e) {
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
			return false;
		}	
	}
	
	/**
	 * Wait for 60 seconds until the object is visible in the DOM and return true if found else false
	 * Log and return false if not
	 * @param driver
	 * @param obj - Locator
	 * @throws IOException 
	 */
	public static boolean waitForElBoolean(WebDriver driver, String objName, WebElement el, ExtentTest logger) throws IOException{
		try {
			//Wait for the page to successfully load without waiting for any element
			waitUntilPageLoad(driver);
			
			WebDriverWait wait = new WebDriverWait(driver,60,2);
			wait.until(ExpectedConditions.visibilityOf(el));
			return true;
		} catch(TimeoutException e) {
			util.logFail(driver, "Session timed out without element ["+objName+"] being present.", logger, true);
			return false;
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not visible: "+objName, logger, true);
			return false;
		} catch(NoSuchElementException e){
			util.logFail(driver, "Element not found: "+objName, logger, true);
			return false;
		} catch (Exception e) {
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
			return false;
		}	
	}
	
	
	/**
	 * Wait for 60 seconds until the object is visible in the DOM and return true if found else false
	 * @param driver
	 * @param objName - Object Name
	 * @param obj - Locator
	 * @throws IOException 
	 */
	public static boolean waitForElBoolean(WebDriver driver, By obj) throws IOException{
		try {
			//Wait for the page to successfully load without waiting for any element
			waitUntilPageLoad(driver);
			
			WebDriverWait wait = new WebDriverWait(driver,60,2);
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(obj));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * Wait for 60 seconds until the element is visible in the DOM and return true if found else false
	 * @param driver
	 * @param el - Web element
	 * @param logger - ExtentTest
	 */
	public static boolean waitForElBoolean(WebDriver driver, WebElement el, ExtentTest logger){
		try {
			//Wait for the page to successfully load without waiting for any element
			waitUntilPageLoad(driver);
			
			WebDriverWait wait = new WebDriverWait(driver, 60, 2);
			wait.until(ExpectedConditions.visibilityOf(el));
			scroll(driver, el, logger);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * Wait for 60 seconds until the element is visible in the DOM and return true if found else false
	 * @param driver
	 * @param el - Web element
	 * @param logger - ExtentTest
	 */
	public static boolean waitForElBoolean(WebDriver driver, By obj, ExtentTest logger){
		try {
			//Wait for the page to successfully load without waiting for any element
			waitUntilPageLoad(driver);
			
			WebDriverWait wait = new WebDriverWait(driver,60,2);
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(obj));
			scrollObj(driver, obj, logger);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	

	public boolean isAlertPresents(WebDriver driver) {
        try {
        	WebDriverWait wa = new WebDriverWait(driver, 60, 2);
			wa.until(ExpectedConditions.alertIsPresent());
          return true;
        } catch (NoAlertPresentException e) {
          return false;
        }
      }
	
	
	 public static boolean isDialogPresent(WebDriver driver) {
	    try {
	        driver.getTitle();
	        return false;
	    } catch (UnhandledAlertException e) {
	        return true;
	    }
	}

	/**
	 * Scroll until the object is visible
	 * @param driver
	 * @param obj - Locator
	 * @param logger - ExtentTest
	 * @return 
	 * @throws IOException 
	 */
	public static boolean scrollObj (WebDriver driver, By obj, ExtentTest logger) throws IOException{
		try {
			if(waitForElBoolean(driver, obj.toString(), obj, logger)){
				WebElement element = driver.findElement(obj);
				scroll(driver, element, logger);
				return true;
			}
		} catch(Exception e) {
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
		}
		return false;
	} 
	
	/**
	 * Scroll until the element is visible
	 * @param driver
	 * @param obj - Locator
	 * @param logger - ExtentTest
	 * @return 
	 * @throws IOException 
	 */
	public static boolean scrollObj (WebDriver driver, WebElement el, ExtentTest logger) throws IOException{
		try {
			if(waitForElBoolean(driver, el.toString(), el, logger)){
				scroll(driver, el, logger);
				return true;
			}
		} catch(Exception e) {
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
		}
		return false;
	} 
	
	/**
	 * Scroll to element
	 * @param driver
	 * @param el - element
	 * @param logger - ExtentTest
	 */
	public static void scroll(WebDriver driver, WebElement el, ExtentTest logger) throws IOException{
		try {
			//Coordinates coOrdin = ((Locatable) el).getCoordinates();
			//coOrdin.inViewPort();
			
			Point loc = el.getLocation();
			loc.x = el.getLocation().getX() -200;
			loc.y = el.getLocation().getY() -200;
			
			((JavascriptExecutor) driver).executeScript("scroll(0,"+loc+")");
			Thread.sleep(200);
		} catch(Exception e) {
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
		}
	}
	
	
	/**
	 * An expectation for checking that an element is either invisible or not present on the DOM.
	 * @param driver
	 * @para obj - Locator 
	 * @throws InterruptedException 
	 */
	public static boolean waitUntilNotVisible(WebDriver driver, By obj) throws InterruptedException {
		boolean x = false;
		try {
			int count = driver.findElements(obj).size();
			Thread.sleep(800);
			WebDriverWait wait = new WebDriverWait(driver, 60, 2);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(obj));
			x = true;
			
			if(count>1){
				List <WebElement> el = driver.findElements(obj);
				
				for (WebElement we: el){
					while(we.getCssValue("display").equals("block")){
						if(!(we.getCssValue("display").equals("block"))){
							x=true;
						}
					}
				}
			}
		} catch(StaleElementReferenceException e) {
			Thread.sleep(5000);
			x = true;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}catch (Exception e) {
			x = false;
		} 
		return x;
	}	
	
	
	
	
	
	//=================================================================================================================//
	//**************************************************ELEMENT STATE**************************************************//
	
	/**
	 * Check is the element exist and confirm if found
	 * @param driver
	 * @param objName - Object name
	 * @param obj - locator
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean findConfirm(WebDriver driver,  String objName, By obj, ExtentTest logger, boolean wholeScreen) throws IOException {
		scrollObj(driver, obj, logger);
		if(waitForElBoolean(driver, objName, obj, logger)){
			util.logPass("Element Found: "+ objName, logger);
			return true;
		} else {
			util.logFail(driver, "Missing Element: "+ objName, logger, wholeScreen);
		}
		return false;
	}
	
	
	/**
	 * Check is the element doesn't exist
	 * @param driver
	 * @param objName - Object name
	 * @param obj - Locator
	 * @param logger - ExtentTest
	 * @throws IOException 
	 */
	public static void ConfirmHidden(WebDriver driver, String objName, By obj, ExtentTest logger, boolean wholeScreen) throws IOException {
		if(elementHidden(driver, obj)){
			util.logFail(driver, "Element Found:  "+objName, logger, wholeScreen);
		} else{
			util.logPass("Element Hidden: "+ objName, logger);
		}
	}
	
	
	/**
	 * Checks if the textbox field is disabled.
	 * @param driver
	 * @param obj
	 * @param value
	 * @param logger
	 * @throws IOException 
	 */
	public boolean checkDisabledTxtBox(WebDriver driver, String objName, By obj, ExtentTest logger, boolean wholeScreen) throws IOException{
		
		boolean y = false;
		String xx="";
	
		try{
			xx = driver.findElement(obj).getAttribute("readonly");
		} catch (Exception e){
			xx = "false";
		}
		
		if(waitForElBoolean(driver, objName, obj, logger)){
			if(xx.equals("false")){
				util.logFail(driver, objName + " is enabled", logger, wholeScreen);
				y=false;
			} else {
				util.logPass(objName + " is disabled as expected", logger);
				y=true;
			}
		} 
		return y;
	}
	
	
	/**
	 * Checks if the fields is enabled and return true.
	 * @param driver
	 * @param logger
	 * @param obj
	 * @param value
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static boolean checkEnabledBoolean(WebDriver driver, String objName, By obj, ExtentTest logger) throws IOException {
		if(waitForElBoolean(driver, objName, obj, logger)){
			if(driver.findElement(obj).isEnabled()){
				return true;
				
			} else {
				return false;
			} 
		} 
		return false;
	}
	
	/**
	 * Checks if the fields is enabled.
	 * @param driver
	 * @param logger
	 * @param obj
	 * @param value
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public boolean checkEnableLog(WebDriver driver, String objName, By obj, ExtentTest logger, boolean wholeScreen) throws InterruptedException, IOException{
		//scroll(driver, obj);
		Boolean x = false;
		
		if(waitForElBoolean(driver, objName, obj, logger)){
			if(driver.findElement(obj).isEnabled()){
				x = true;
				util.logPass(objName + " is enabled as expected", logger);
			} else {
				x = false;
				util.logFail(driver, objName + " is disabled", logger, wholeScreen);
			} 
		}
		return x;
	}

	
	/**
	 * Checks if the fields is disabled.
	 * @param driver
	 * @param logger
	 * @param obj
	 * @param value
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public boolean checkDisabledBoolean(WebDriver driver, String objName, By obj, ExtentTest logger) throws IOException{
		if(waitForElBoolean(driver, objName, obj, logger)){
			if(driver.findElement(obj).isEnabled()){
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 *  Get the list values from the list box
	 * @param driver - WebDriver
	 * @param locator - Locator
	 * @param logger - ExtentTest Logger
	 * @return
	 * @throws IOException 
	 */
	public ArrayList<String> getTheListValues(WebDriver driver,By locator,ExtentTest logger) throws IOException {
		ArrayList<String> a = new ArrayList<>();
		try {
			Select n = new Select(driver.findElement(locator));
			List<WebElement> listValues = n.getOptions();

			for (int i=0;i<listValues.size();i++){
				String value = listValues.get(i).getAttribute("text").toString().trim();
				a.add(value);
			}
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not found: ", logger, true);
			return null;
		} catch(Exception e) {
			util.logFail(driver, "Thrown Exception: "+ e.getMessage(), logger, true);
			return null;
		}
		return a;
	}

	
	/** To Check whether the desired field is Empty or not
	 * @param driver - WebDriver
	 * @param obj - locator
	 */
	public boolean verifyFieldIsEmpty(WebDriver driver, By obj, ExtentTest logger){
		WebElement element = driver.findElement(obj);

		if(element.getAttribute("value").isEmpty()){
			util.logPass("Value is Empty", logger);
			return true;
		}
		else{		
			util.logPass("Value is not Empty", logger);
			return false;
		}		
	}	
	

	/** To Check whether the desired field is disabled or not
	 * @param driver - WebDriver
	 * @param locator - Object locator
	 * @param message - description of the locator
	 * @throws IOException 
	 */
	public static boolean checkDisabled(WebDriver driver, By locator, String objName, ExtentTest logger, boolean wholeScreen) throws IOException {
		//String disableProperty = driver.findElement(locator).getAttribute("disabled");
		Boolean disable = driver.findElement(locator).isEnabled();
		
		if (disable==false) {
			util.logPass(objName + " is disabled", logger);				
			return true;
		}
		else{				 
			util.logFail(driver, objName + " is Enabled", logger, wholeScreen);
			return false;
		}
	}	
	
	/** To Check whether the desired field is disabled or not
	 * @param driver - WebDriver
	 * @param locator - Object locator
	 * @param message - description of the locator
	 * @throws IOException 
	 */
	public static boolean checkDisabled(WebDriver driver, WebElement el, String objName, ExtentTest logger, boolean wholeScreen) throws IOException {
		Boolean disable = el.isEnabled();
		
		if (disable==false) {
			util.logPass(objName + " is disabled", logger);				
			return true;
		}
		else{				 
			util.logFail(driver, objName + " is Enabled", logger, wholeScreen);
			return false;
		}
	}
	
	
	/**
	 * Check if the element exist (Wait for the element for 60 seconds)
	 * @param driver
	 * @param locator
	 */
	public static boolean elementExist(WebDriver driver, By locator){
		boolean exist = false;
		try{
			WebDriverWait wait = new WebDriverWait(driver, 60, 5);
			wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
			exist = driver.findElements(locator).size()>0;
		}
		catch(Exception e){
			exist=false;
		}
		return exist;
	}
	
	/**
	 * Check if the element exist (Wait for the element for 60 seconds)
	 * @param driver
	 * @param locator
	 */
	public static boolean elementHidden(WebDriver driver, By locator){
		boolean exist = false;
		try{
			WebDriverWait wait = new WebDriverWait(driver, 60, 5);
			wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOf((WebElement) locator)));
			exist = driver.findElements(locator).size()>0;
		}
		catch(Exception e){
			exist=false;
		}
		return exist;
	}
	
	/**
	 * Check if the element exist (Wait for the element for 60 seconds)
	 * @param driver
	 * @param locator
	 */
	public static boolean isDisplayed(WebDriver driver, By locator){
		boolean exist = false;
		try{
			exist = driver.findElements(locator).size()>0;
		} catch(Exception e){
			exist=false;
		}
		return exist;
	}
	
	
	/**
	 * Wait for 30 seconds until the list box contains > 0
	 * @param driver
	 * @param locator
	 */
	public static void waitListLoad(WebDriver driver, final By locator){
		WebDriverWait wait = new WebDriverWait(driver, 30,5);
	    
		List <WebElement> el = driver.findElements(locator);
		
		if(el.size()==1){
			// Wait until expected condition size of the dropdown increases and becomes more than 1
		    wait.until((ExpectedCondition<Boolean>) new ExpectedCondition<Boolean>(){
		        public Boolean apply(WebDriver driver)  {
		            Select select = new Select(driver.findElement(locator));
		            return select.getOptions().size()>0;
		        }
		    });
		    
		} else {
			for (WebElement we: el){
				while(we.getCssValue("display").equals("block")){
					try {
						if(!(we.getCssValue("display").equals("block"))){
							break;
						} 
					}catch (Exception e){
						break;
					}
				}
			}
			
		}
	}
	
	
	
	
	
	//=================================================================================================================//
	//***********************************************ELEMENT INTERACTION***********************************************//
	
	/**
	 * Return the value of a specific web element
	 * @param driver
	 * @param type
	 * @param element
	 * @param logger
	 * @throws IOException 
	 */
	public String getListValue(WebDriver driver, String objName, By element, ExtentTest logger) throws InterruptedException, IOException{
		String textvalue = null;
		try {
			
			if(waitForElBoolean(driver, objName, element, logger)){
				Select select = new Select(driver.findElement(element));
				WebElement option = select.getFirstSelectedOption();
				textvalue = option.getAttribute("innerText").trim();
			}
		} catch(Exception e){
			textvalue = "";
			
		}
		
		return textvalue;
	}
	
	
	/**
	 *  Get all the list values present in the drodown
	 * @param driver - Webdriver 
	 * @param obj - locator
	 * @param objName - Dropdown name
	 * @return
	 * @throws IOException 
	 */
	public List<WebElement> getTheDropdownValues(WebDriver driver,  String objName, By obj, ExtentTest logger) throws IOException {
		try {
			WebElement el = driver.findElement(obj);
			Select s = new Select(el);
			List<WebElement> options = s.getOptions();
			return options;
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not found: "+ objName, logger, true);
			return null;
		} catch(Exception e) {
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
			return null;
		}
	}
	
	
	
	/**
	 * Go to URL and wait for certain element (this can be null)
	 * @param driver - WebDriver 
	 * @param URL  - URL
	 * @param objName - Object name of element to wait after browser loads
	 * @param objWait - Object to wait
	 * @param logger - Extent Report
	 * @throws IOException 
	 */
	public static boolean getURLThenWaitForEl(WebDriver driver, String URL, String objName, By objToWait, ExtentTest logger) throws IOException {
		try {
			boolean found =false;
			String current = driver.getCurrentUrl();
			if(!current.equals(URL) && !base.isDisplayed(driver, objToWait)){
				driver.get(URL);
				if(base.waitUntilPageLoad(driver)) {
					found=driver.getCurrentUrl().equals(URL);
					return found;
				}
			} else {
				return true;
			}
			
			
			base.waitUntilPageLoad(driver);
			if(!objToWait.equals(null)) {
				if(base.waitForElBoolean(driver, false, objName, objToWait, logger)){
					return found;
				}
			}
			return found;
		}catch (NullPointerException e) {
			base.waitUntilPageLoad(driver);
		}catch(TimeoutException e) {
			util.logFail(driver, "Session timed out without element ["+objName+"] being present.", logger, true);
			return false;
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not visible: "+objName, logger, true);
			return false;
		} catch (Exception e) {
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger); 
			return false;
		}		
		return false;
    }
	
	/**
	 * Go to URL and wait for certain element (this can be null)
	 * @param driver - WebDriver 
	 * @param URL  - URL
	 * @param objName - Object name of element to wait after browser loads
	 * @param objWait - Element to wait
	 * @param logger - Extent Report
	 * @throws IOException 
	 */
	public static boolean getURLThenWaitForEl(WebDriver driver, String URL, String objName, WebElement objToWait, ExtentTest logger) throws IOException {
		try {
			boolean found =false;
			String current = driver.getCurrentUrl();
			if(!current.equals(URL) || !objToWait.isDisplayed()){
				driver.get(URL);
				if(base.waitUntilPageLoad(driver)) {
					found=driver.getCurrentUrl().equals(URL);
					return found;
				}
			} else {
				return true;
			}
			base.waitUntilPageLoad(driver);
			if(!objToWait.equals(null)) {
				if(base.waitForElBoolean(driver, false, objName, objToWait, logger)){
					return found;
				}
			}
			return found;
		}catch (NullPointerException e) {
			base.waitUntilPageLoad(driver);
		}catch(TimeoutException e) {
			util.logFail(driver, "Session timed out without element ["+objName+"] being present.", logger, true);
			return false;
		} catch(ElementNotVisibleException e){
			util.logFail(driver, "Element not visible: "+objName, logger, true);
			return false;
		
		} catch (Exception e) {
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger); 
			return false;
		}		
		return false;
    }
	
	

	/**
	 *  Return the current window handles 
	 * @param driver
	 * @return
	 * @throws IOException 
	 */

	public static String getTheCurrentWindowHandle(WebDriver driver, ExtentTest logger) throws IOException{
		try{
			String currentWindowName = driver.getWindowHandle();
			return currentWindowName;
		}
		catch(NoSuchWindowException e){
			util.logFail(driver, "No window found", logger, true);
			return null;
		}
	}

	
	public void handleMultipleWindows(String windowTitle, WebDriver driver) {
         Set<String> windows = driver.getWindowHandles();
        
         for (String window : windows) {
            if (!(driver.getTitle().contains(windowTitle))) {
            	driver.switchTo().window(window);
             }
         }
     }
	 
	
	
public static boolean openNewTab(WebDriver driver, String url,  ExtentTest logger) throws IOException{
		
		try {
			String currentWindow = base.getTheCurrentWindowHandle(driver, logger);
			Set<String> iniTialWindows = driver.getWindowHandles();
			int iniTialWindows_size = iniTialWindows.size();
			
			((JavascriptExecutor) driver).executeScript("window.open()");
		    
		
			Set<String> totalwindows = driver.getWindowHandles();
			if(totalwindows.size()>iniTialWindows_size) {
				base.switchToNewlyOpenedWindow(driver, currentWindow, logger);
				driver.get(url);
				base.waitUntilPageLoad(driver);
				return true;
			} else {
				util.logFail("There is NO newly opened window!", logger);
				return false;
			}
			
		} catch (Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger); 
		}
		return false;
	}
	
	
	 /**
	 *  Switch to other window handles
	 * @param driver - WebDriver 
	 * @param currentWindow  - Window handle, where drive has to switch
	 * @return
	 * @throws IOException 
	 */
	public static boolean switchToNewlyOpenedWindow(WebDriver driver, String currentWindow, ExtentTest logger) throws IOException {
		try{			
			Set<String> totalwindows = driver.getWindowHandles();
			for (String window : totalwindows){
				if (!window.equalsIgnoreCase(currentWindow)){
					driver.switchTo().window(window);
					waitUntilPageLoad(driver);
					break;
				}
			}			
		}
		catch(NoSuchWindowException e){
			util.logFail(driver, "No window found with the Window handle ["+e+"]", logger, true);
			return false;
		}		
		return true;		
	}	
		
	
	/**
	 * Validate the expected CSS Background Color Attribute
	 * @param driver
	 * @param element
	 * @param expected
	 * @param cssAttribute
	 * @param object1
	 * @param logger
	 * @throws IOException 
	 */
	public void checkColorAttribute(WebDriver driver, String objName, WebElement element, String expected_color_name,
			String expected_css_value, ExtentTest logger, boolean wholeScreen) throws AWTException, InterruptedException, IOException{
	
		String background = element.getCssValue("background-color");
		
		if(background.equals(expected_css_value)){
			util.logPass(objName+"'s color is "+expected_color_name+" as expected!", logger);
		}else {
			util.logFail(driver, objName+"'s color is NOT "+expected_color_name+" [Unexpected Color Attribute Value: "+background+"]", logger, wholeScreen);
		}
	}
	
	
	
	public static boolean validateLinkOpenInNewTab(WebDriver driver, WebElement el, String elName, String expected_url,  ExtentTest logger, 
			boolean wholeScreen) throws IOException{
		
		try {
			Set<String> iniTialWindows = driver.getWindowHandles();
			int iniTialWindows_size = iniTialWindows.size();
			
			String currentWindow = base.getTheCurrentWindowHandle(driver, logger);
			
			base.scrollObj(driver, el, logger);
			el.sendKeys(Keys.chord(Keys.CONTROL,Keys.RETURN));
			
		
			Set<String> totalwindows = driver.getWindowHandles();
			if(totalwindows.size()>iniTialWindows_size) {
				base.switchToNewlyOpenedWindow(driver, currentWindow, logger);
				base.waitUntilPageLoad(driver);
				String currentURL = driver.getCurrentUrl();
				base.compareThenCapture(driver, expected_url, currentURL, "[NEW TAB] "+elName+" ["+expected_url+"] link/button", logger, wholeScreen);
				return true;
			} else {
				util.logFail("There is NO newly opened window!", logger);
				return false;
			}
			
		} catch (Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger);
		}
		return false;
	}
	
	public static boolean validateBtnOpenInNewTab(WebDriver driver, WebElement el, String elName, String expected_url, boolean close, ExtentTest logger, 
			boolean wholeScreen) throws IOException{
		
		try {
			Set<String> iniTialWindows = driver.getWindowHandles();
			int iniTialWindows_size = iniTialWindows.size();
			
			String currentWindow = base.getTheCurrentWindowHandle(driver, logger);
			
			base.scrollObj(driver, el, logger);
			el.sendKeys(Keys.CONTROL,Keys.RETURN);
			
		
			Set<String> totalwindows = driver.getWindowHandles();
			if(totalwindows.size()>iniTialWindows_size) {
				base.switchToNewlyOpenedWindow(driver, currentWindow, logger);
				base.waitUntilPageLoad(driver);
				String currentURL = driver.getCurrentUrl();
				base.comparePatternThenCapture(driver, expected_url, currentURL, "[IN NEW TAB] "+elName+" ["+currentURL+"] button", logger, wholeScreen);
				
				if(close==true) {
					//Closing the newly opened tab
					close2ndTab(driver, logger, false);
				}
				return true;
			} else {
				util.logFail("There is NO newly opened window!", logger);
				return false;
			}
			
		} catch (Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger); 
		}
		return false;
	}
	
public static boolean close2ndTab(WebDriver driver, ExtentTest logger, boolean wholeScreen) throws IOException{
		
		try {
			//Closing the newly opened tab
			Set<String> totalwindows = driver.getWindowHandles();
			if(totalwindows.size()>1) {
				String currentWindow = base.getTheCurrentWindowHandle(driver, logger);
				driver.switchTo().window(currentWindow).close();
				boolean success = base.switchToNewlyOpenedWindow(driver, currentWindow, logger);
				return success;
			}
		} catch (Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger); 
		}
		return false;
	}
	
	//====================================================================================================================//
	//****************************************** DATA MANIPULATION / FORMATTING ******************************************//
	
	/**
	 * Format the date
	 * @param format
	 */
	public static String getDateFormat(String format){
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
	
	/**
	 * Format the date
	 * @param format
	 */
	public static String getDateFormat(String format, Date date){
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
	
	public static Date getTodayDate(WebDriver driver, ExtentTest logger) throws IOException{
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date currentDate = Date.from(Instant.now());
			currentDate = dateFormat.parse(dateFormat.format(currentDate));
			return currentDate;
		} catch (Exception e){
			util.logError("Thrown Exception : "+ ExceptionUtils.getStackTrace(e), logger); 
		}
		return null;
	}
	
	/**
	 * Format string to decimal
	 * @param format
	 */
	public String formatDecimal(String stringAmnt, DecimalFormat formatter){
		double amount = Double.parseDouble(stringAmnt);
		double rounded = (double) Math.round(amount * 100.0) / 100.0;
		String amnt = formatter.format(rounded);
		
		return amnt;
	}
	
	
	/**
	 * Provide random number based on given maximum integer excluding 0
	 * @param maxnum
	 */
	public static int randomNumInt (int maxnum){
		int n = 0;
		if(maxnum==1){
			n=1;
		} else{
			Random rand = new Random();
			n = rand.nextInt(maxnum - 1) + 1;
		}
		return n;
	} 
	
	/**
	 * Provide random number based on given range
	 * @param maxnum
	 */
	public static int randomNumInt (int min, int max){
		return min + (int)(Math.random() * ((max - min) + 1));
	} 
	

	/**
	 * Provide random number based on given range
	 * @param maxnum
	 */
	private static String value;
	public static String delSpclChar (){
		return value.replaceAll("[^a-zA-Z0-9-&\\s+]", ""); //removing special characters;
	}
	
	/**
	 * Return the current Date in String
	 * @return
	 */
	public String getCurrentDate() {				
		//java.util.Calendar cal=java.util.Calendar.getInstance();//calculating current date
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(java.util.Calendar.getInstance().getTime());
		//System.out.println("Date1 is "+timeStamp);
		return timeStamp;		
	}

	
	/**
	 *  Return the month name based on the month index
	 * @param m
	 * @return
	 */
	public static String Month_MM(String m){
		if(m.equalsIgnoreCase("January")){
			return "1";
		} else if(m.equalsIgnoreCase("February")){
			return "2";
		} else if(m.equalsIgnoreCase("March")){
			return "3";
		} else if(m.equalsIgnoreCase("April")){
			return "4";
		} else if(m.equalsIgnoreCase("May")){
			return "5";
		} else if(m.equalsIgnoreCase("June")){
			return "6";
		} else if(m.equalsIgnoreCase("July")){
			return "7";
		} else if(m.equalsIgnoreCase("August")){
			return "8";
		} else if(m.equalsIgnoreCase("September")){
			return "9";
		} else if(m.equalsIgnoreCase("October")){
			return "10";
		} else if(m.equalsIgnoreCase("November")){
			return "11";
		} else if(m.equalsIgnoreCase("December")){
			return "12";
		}
		return "";
	}
	
	
	
	
	
	//=================================================================================================================//
	//**************************************************COMPARING DATA**************************************************//
	
	/**
	 * Compare and take screenshot if string found contains expected 
	 * @param driver
	 * @param expected
	 * @param found
	 * @param element name
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean comparePatternThenLog(WebDriver driver, String expected, String found, String element, ExtentTest logger) throws IOException {
	
		if (Pattern.compile(Pattern.quote(expected), Pattern.CASE_INSENSITIVE).matcher(found).find()){
			util.logPass(element+" has been validated!", logger);
	    	return true;
	    } else{
	    	util.logFail(driver, element+" NOT FOUND. [EXPECTED: "+expected+"][FOUND: "+found+"]", logger, true);
	    	return false;
	    }
		
	}
	
	/**
	 * Compare and take screenshot if string found contains expected 
	 * @param driver
	 * @param expected
	 * @param found
	 * @param element name
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean comparePatternThenCapture(WebDriver driver, String expected, String found, String element, ExtentTest logger, 
			boolean wholeScreen) throws IOException {
	
		if (Pattern.compile(Pattern.quote(expected), Pattern.CASE_INSENSITIVE).matcher(found).find()){
	    	util.logPass(driver, element+" has been validated!", logger, wholeScreen);
	    	return true;
	    } else{
	    	util.logFail(driver, element+" NOT FOUND. [EXPECTED: "+expected+"][FOUND: "+found+"]", logger, wholeScreen);
	    	return false;
	    }
		
	}
	
	/**
	 * Compare and take screenshot if string found contains expected 
	 * @param driver
	 * @param expected
	 * @param found
	 * @param element name
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean compareThenCapture(WebDriver driver, String expected, String found, String element, ExtentTest logger, 
			boolean wholeScreen) throws IOException {
	
		if (Pattern.compile(Pattern.quote(expected), Pattern.CASE_INSENSITIVE).matcher(found).find()){
	    	util.logPass(driver, element+" has been validated!", logger, wholeScreen);
	    	return true;
	    } else{
	    	util.logFail(driver, element+" NOT FOUND. [EXPECTED: "+expected+"][FOUND: "+found+"]", logger, wholeScreen);
	    	return false;
	    }
		
	}

	
	/**
	 * Compare the strings and log in the extentReports
	 * @param driver
	 * @param expected
	 * @param found
	 * @param element name
	 * @param logger
	 * @param wholeScreen
	 * @throws IOException 
	 */
	public static boolean compareThenLog(WebDriver driver, String expected, String found, String element, ExtentTest logger) throws IOException {
		
	    if (found.equals(expected)){
	    	util.logPass(element+" has been validated!", logger);
	    	return true;
	    } else{
	    	util.logFail(element+" NOT FOUND. [EXPECTED: "+expected+"][FOUND: "+found+"]", logger);
	    	return false;
	    }
	}
	
	/**
	 * Compare the strings and return true or false (CASE SENSITIVE)
	 * @param a
	 * @param b
	 */
	public boolean compareString(String a, String b) {
	    boolean x = false;
		if (a.equals(b)){
	    	x = true;
	    } else{
	    	x = false;
	    }
		return x;
	}
	

	/**
	 * Comparing if String contains a specific pattern string
	 * @param driver
	 * @param expected
	 * @param found
	 * @param element name
	 * @param logger
	 * @throws IOException 
	 */
	public static boolean ifContains(WebDriver driver, String expected, String found, ExtentTest logger) throws IOException {
		if (Pattern.compile(Pattern.quote(expected), Pattern.UNICODE_CASE).matcher(found).find()){
	    	return true;
	    } else{
	    	return false;
	    }
		
	}

	/**
	 * Comparing if String contains a specific pattern string returns boolean
	 * @param text - where to match (ex. pineapple)
	 * @param pattern - a pattern to find (ex. apple)
	 * @throws IOException 
	 */
	public static boolean containPattern(String text, String pattern) throws IOException {
		if (Pattern.compile(Pattern.quote(pattern), Pattern.UNICODE_CASE).matcher(text).find()){
	    	return true;
	    } else{
	    	return false;
	    }
	}
	
	
	/**
	 * Find if ArrayList contains a certain string
	 * @param a - string
	 * @param array - arraylist to look into
	 */
	public static boolean findStringInArrayList(String a, ArrayList <String> array) {
		for (int x=0; x<array.size(); x++) {
			String temp = array.get(x).toString();
	        if (a.equals(temp)) {
	        	return true;
	        }
	    }
	    return false;
	}
	
	
	/**
	 * Verify if the string contains any of the values from arraylist
	 * @param a - string
	 * @param array - arraylist to look into
	 */
	public static boolean containsArraylistVal(ArrayList <String> array, String toCheck) {
		for (int x=0; x<array.size(); x++) {
			String temp = array.get(x).toString();
	        if (Pattern.compile(Pattern.quote(temp), Pattern.CASE_INSENSITIVE).matcher(toCheck).find()){
	        	return true;
	        }
	    }
	    return false;
	}
	
	
	/**
	 * Find if the array contains a certain string
	 * @param a - string to find
	 * @param array - array to look into
	 */
	public static String findStringInArrayReturnIndex(ArrayList <String> array, String a) {
		for (int x=0; x<array.size(); x++) {
			String temp = array.get(x).toString();
	        if (Pattern.compile(Pattern.quote(a), Pattern.CASE_INSENSITIVE).matcher(temp).find()){
	        	return String.valueOf(x);
	        }
	    }
	    return "false";
	}
	
	/**
	 * Find if the array contains a certain string
	 * @param a - string to find
	 * @param array - array to look into
	 */
	public static String findStringInArrayReturnIndex(String [] array, String a) {
		for (int x=0; x<array.length; x++) {
			String temp = array[x].toString();
	        if (Pattern.compile(Pattern.quote(temp), Pattern.CASE_INSENSITIVE).matcher(a).find()){
	        	return String.valueOf(x);
	        }
	    }
	    return "false";
	}
	
	
	
	/**
	 * Find if the array contains a certain string
	 * @param a - string to find
	 * @param array - array to look into
	 */
	public static String containStringInArray(String [] array, String a) {
		for (int x=0; x<array.length; x++) {
			String temp = array[x].toString();
	        if (Pattern.compile(Pattern.quote(temp), Pattern.CASE_INSENSITIVE).matcher(a).find()){
	        	return String.valueOf(x);
	        }
	    }
	    return "false";
	}
	
	/**
	 * Find if the array contains a certain string
	 * @param a - string to find
	 * @param array - array to look into
	 */
	public static boolean findStringInArray(String a, String [] array) {
		for (int x=0; x<array.length; x++) {
			String temp = array[x].toString();
	        if (a.equals(temp)) {
	        	return true;
	        }
	    }
	    return false;
	}
	
	/**
	 * Compare the strings (regardless of the case) and log in the extentReports
	 * @param driver
	 * @param a
	 * @param b
	 * @param logger
	 * @throws IOException 
	 */
	public void compareStringsIgnoreCase(WebDriver driver, String a, String b, ExtentTest logger) throws IOException {
	    if (a.equalsIgnoreCase(b)){
	    	util.logPass("String ["+a+"] equals to ["+b+"]", logger);
	    } else{
	    	util.logFail("String ["+a+"] not equals to ["+b+"]", logger);
	    }
	}
	
	/**
	 * Find if the list contains a certain string
	 * @param a string
	 * @param b list
	 */
	public boolean compareElements(String a, List <WebElement> b) {
	    for (WebElement we:b) {
	        if (a.equalsIgnoreCase(we.getAttribute("innerText").trim())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	
	/**
	 * Find if the list contains a certain string
	 * @param a string to find
	 * @param array
	 */
	public boolean compareString2Array(String a, String array []) {
		for (int x=0; x<array.length; x++) {
			//System.out.println(array[x]+"<<<");
	        if (a.equalsIgnoreCase(array[x].trim())) {
	        	return true;
	        }
	    }
	    return false;
	}
	
	public static String[] removeNullValueArray(String[] array){
		return array = Arrays.stream(array)
                     .filter(s -> (s != null && s.length() > 0))
                     .toArray(String[]::new);    
    }
	
	
	
	//=================================================================================================================//
	//=================================================================================================================//
}
package driver;

import java.io.File; 	
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class util {
	public final static String driverPath = System.getProperty("user.dir")+"\\driver\\chromedriver.exe";
	public final static String driverType = "webdriver.chrome.driver";	
	
	public final static String workbookName = "Scenarios.xlsx";
	public final static String workbookPath = System.getProperty("user.dir")+"\\src\\test\\resources\\"+workbookName;
	public static String sheetName;
	
	private static Map <String, String> map = new HashMap<>();
	public final static String reportPath = System.getProperty("user.dir")+"\\Report.html";
	public final static String XMLconfigs = System.getProperty("user.dir")+"\\XMLConfigs\\extent-config.xml";
	
	public static WebDriver driver = null;
	public static String scenario;
	public static String featureName;
	public static String currentStep;
	public static String stepForReport;
	
	public static String URL = "http://demo.automationtesting.in/Index.html";
	
	//Chrome Instance
	public final static String automation_chrome = "user-data-dir="+System.getProperty("user.home")+"\\AppData\\Local\\Google\\Chrome\\User Data\\automation_local";
	
	
	
	public static WebDriver getDriver() {
		System.setProperty(util.driverType, util.driverPath);
		WebDriver driver = new ChromeDriver(util.options());
		driver.manage().window().maximize();
		driver.get(URL);
		
        return driver;
    }
	
	/**
	 * Setting up the DesiredCapabilities of the chromedriver
	 * @param driver - WebDriver
	 * @return options - ChromeOptions
	 */
	public static ChromeOptions options (){
		ChromeOptions options = new ChromeOptions();
		System.setProperty(driverType, driverPath);
		options.addArguments(automation_chrome);
		options.addArguments("--incognito");
		options.addArguments("start-maximized"); // open Browser in maximized mode
		options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("--no-sandbox"); // Bypass OS security model
		//options.addArguments("--disable-gpu"); // applicable to windows os only
		options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
		
		return options;
	}
	
	
	/**
	 * Captures the screenshot of the page
	 * @param driver - WebDriver
	 * @return screeshotPath
	 */
	public static String captureScreenshot(WebDriver driver) throws IOException{	
		System.setProperty("org.uncommons.reportng.escape-output", "false");
        String screeshotPath=null;
        if(true){
        	try {
        		String imageFileName =  new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss").format(new GregorianCalendar().getTime())+ ".png"; 
                Screenshot screenshot = new AShot()
                		.shootingStrategy(ShootingStrategies.viewportPasting(100))
                		.takeScreenshot(driver);
                screeshotPath = System.getProperty("user.dir") + "\\extentReports\\screenshots\\" + imageFileName;
                ImageIO.write(screenshot.getImage(),"png",new File(screeshotPath));       
        	} catch (IOException e1) {
        		e1.printStackTrace();
        	}
        }
        return screeshotPath;
	}
	
	/**
	 * Prints the custom message in the Extend reports output with screenshot - Pass
	 * @param logger- Extend logger
	 * @param msg  - Message
	 * @param driver - WebDriver
	 * @throws IOException 
	 */
	public static void logScreenshot(WebDriver driver, ExtentTest logger) throws IOException {
		String screenshotPath = captureScreenshot(driver);
		//String image = logger.addScreenCaptureFromPath(screenshotPath);
		//logger.log(Status.INFO, image);
		
		logger.log(Status.INFO, (Markup) logger.addScreenCaptureFromPath(screenshotPath));
	}
	
	
	/**
	 * Captures the screenshot of the section
	 * @param driver - WebDriver
	 * @return screeshotPath
	 */
	public static String captureSection(WebDriver driver) throws IOException{	
		System.setProperty("org.uncommons.reportng.escape-output", "false");
        String screeshotPath=null;
        if(true){
        	try {
        		String imageFileName =  new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss").format(new GregorianCalendar().getTime())+ ".png"; 
        		
        		TakesScreenshot ts = (TakesScreenshot)driver;
                File source = ts.getScreenshotAs(OutputType.FILE);
                String dest = System.getProperty("user.dir") + "\\extentReports\\screenshots\\" + imageFileName + ".png";
                File destination = new File(dest);
                FileUtils.copyFile(source, destination);
                return dest;
        		
        	} catch (IOException e1) {
        		e1.printStackTrace();
        	}             
        }
        return screeshotPath;
	}


	/**	 Prints the custom message in the Extend reports output - Pass 
	 * @param msg - Message
	 * @param logger - ExtendTest Logger
	 */
	public static void logPass(String msg, ExtentTest logger){
		logger.pass(msg);
	}

	/**
	 * Prints the custom message in the Extend reports output with screenshot - Pass
	 * @param logger- Extend logger
	 * @param msg  - Message
	 * @param driver - WebDriver
	 * @throws IOException 
	 */
	public static void logPass(WebDriver driver, String msg, ExtentTest logger, boolean wholeScreen) throws IOException {
		String screenshotPath;
		if(wholeScreen) {
			screenshotPath = captureScreenshot(driver);
		} else {
			screenshotPath = captureSection(driver);
		}
		
		logger.log(Status.PASS, msg,  MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
	}
	
	/**
	 * Prints the custom message in the Extend reports output with screenshot - Pass
	 * @param logger- Extend logger
	 * @param msg  - Message
	 * @param driver - WebDriver
	 * @throws IOException 
	 */
	public static void logScreenshot(WebDriver driver, String msg, ExtentTest logger) throws IOException {
		String screenshotPath = captureScreenshot(driver);
		logger.log(Status.INFO, (Markup) logger.addScreenCaptureFromPath(screenshotPath));
	}
	
	/**	 Prints the custom message in the Extend reports output - Info 
	 * @param logger - ExtendTest Logger
	 * @param msg - Message
	 */
	public static void logInfo(String msg, ExtentTest logger){
		logger.info(msg);
	}
	
	/**	 Prints the custom message in the Extend reports output - Info 
	 * @param logger - ExtendTest Logger
	 * @param msg - Message
	 */
	public static void logSkip(ExtentTest logger, String msg){
		logger.skip(msg);
	}
	
	/**	 Prints the custom message in the Extend reports output - Fail 
	 * @param logger - ExtendTest Logger
	 * @param msg - Message
	 */
	public static void logError(String msg, ExtentTest logger){
		msg = msg.replace("\n", "<br/>");
		logger.error(msg);
	}
	
	/**
	 * Prints the custom message in the Extend reports output with screenshot - Info
	 * @param logger- Extend logger
	 * @param msg  - Message
	 * @param driver - WebDriver
	 * @throws IOException 
	 */
	public static void logInfo(WebDriver driver, String msg, ExtentTest logger) throws IOException{
		
		String screenshotPath = captureScreenshot(driver);
		logger.log(Status.INFO, (Markup) logger.addScreenCaptureFromPath(screenshotPath));
	}
		

	/**
	 * Prints the custom message in the Extend reports output with screenshot - Fail
	 * @param logger- Extend logger
	 * @param msg  - Message
	 * @param driver - WebDriver
	 * @throws IOException 
	 */
	public static void logFail(WebDriver driver,String msg, ExtentTest logger, boolean wholeScreen) throws IOException{
		
		String screenshotPath;
		if(wholeScreen) {
			screenshotPath = captureScreenshot(driver);
		} else {
			screenshotPath = captureSection(driver);
		}
		logger.log(Status.FAIL, (Markup) logger.addScreenCaptureFromPath(screenshotPath));
		
	}	
		
	
	
	/**
	 * Prints the custom message in the Extend reports output with screenshot - Fail
	 * @param logger- Extend logger
	 * @param msg  - Message
	 * @param driver - WebDriver
	 * @throws IOException 
	 */
	public static void logFail(WebDriver driver, ExtentTest logger, boolean wholeScreen) throws IOException{
		
		String screenshotPath;
		if(wholeScreen) {
			screenshotPath = captureScreenshot(driver);
		} else {
			screenshotPath = captureSection(driver);
		}
		//String image = logger.addScreenCaptureFromPath(screenshotPath);	
		logger.log(Status.FAIL, (Markup) logger.addScreenCaptureFromPath(screenshotPath));
	}
	
		
	/**	 Prints the custom message in the Extend reports output - Fail 
	 * @param logger - ExtendTest Logger
	 * @param msg - Message
	 */
	public static void logFail(String msg,ExtentTest logger){
		logger.fail(msg);
	}
	
	/**	Triggers a web dialog box after execution to ask if browser will be closed and terminated 
	 * @param driver - WebDriver
	 */
	public static void closeBrowser(WebDriver driver){
		disableAccessWarnings();
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true); 
		
		int reply = JOptionPane.showConfirmDialog(dialog, "EXECUTION DONE. Do you want to close the browser/driver?", "Close Browser", 
				JOptionPane.YES_NO_OPTION);
        
		if (reply == JOptionPane.YES_OPTION) {
			driver.quit();
		} 
	}
	
	/**	 This is to hide warning "illegal reflective access" that is known in Java 11
	 */
	@SuppressWarnings("unchecked")
    public static void disableAccessWarnings() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);

            Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

            Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
        }
    }
	
	public static String get(String sheetName, String scenario, String columnName) throws IOException {
		//FileInputStream fis;
		org.apache.poi.ss.usermodel.Workbook tempWB;
		
		int k=0;
		try {
			
			if(workbookPath.contains(".xlsx") || workbookPath.contains(".xlsm")){
				tempWB = new XSSFWorkbook(workbookPath);
			} else{				
				InputStream inp = new FileInputStream(workbookPath);
				tempWB = (org.apache.poi.ss.usermodel.Workbook) new HSSFWorkbook(new POIFSFileSystem(inp));					
			}
			
			org.apache.poi.ss.usermodel.Sheet sheet = tempWB.getSheet(sheetName);
			
			int rows = sheet.getPhysicalNumberOfRows();

			try {
				for(int i=0;i<rows;i++){
					//Getting the number of defined cells in the row
					int cols = sheet.getRow(0).getPhysicalNumberOfCells();
					
					for (int j = 0; j < cols; j++) { 
						if(sheet.getRow(i).getCell(j,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().replace(".0", "").equalsIgnoreCase(columnName)){ 
							k=j;
						}
						
						map.put(sheet.getRow(i).getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().replace(".0", ""),
								sheet.getRow(i).getCell(k,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString().replace(".0", ""));
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			tempWB.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return map.get(scenario);
	}
	
	
	/*
	 * 
	 */
	public static ArrayList <String> stepDescFull(String...columnNames) throws IOException {
		String fullStepDescription = util.currentStep;
		ArrayList <String> data = new ArrayList<String>();
		data.add(fullStepDescription);
		try {
			for(int x=0; x<columnNames.length; x++) {
				String dataValue = util.get(util.sheetName, util.scenario, columnNames[x]);
				data.add(dataValue);
				fullStepDescription = fullStepDescription.replace(columnNames[x], dataValue);
			}
			
			data.set(0, fullStepDescription);
		} catch (Exception e) {
			e.printStackTrace();
		}
		util.stepForReport = fullStepDescription;
		return data;
	}
	
}


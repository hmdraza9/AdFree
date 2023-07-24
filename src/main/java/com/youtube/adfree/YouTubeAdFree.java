package com.youtube.adfree;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class YouTubeAdFree {

	Logger log = LogManager.getLogger(YouTubeAdFree.class);
	static YouTubeAdFree yt = new YouTubeAdFree();
	public static String currentURL;
	public static int currentHour;
	public static int currentMin;
	public static int currentSec;
	public static String osName = System.getProperty("os.name");
	public static String ytURL = "https://www.youtube.com";
	public static String searchBoxXpath = "//input[@id='search']";
	public static String SkipAdButton = "//div[contains(@id,'ad-text') and contains(text(),'Skip')]/parent::button";
	public static String searchIcon = "//button[contains(@id,'search-icon')]";
	public static String searchResultFirst = "(//a[@id='video-title'])[1]";
	public static String fullScreenVideo = "//button[contains(@title,'ull screen')]";
	public static String videoScreen = "//div[@class='html5-video-container']/video";
	Robot rb ;

	static WebDriver driver;

	public static void main(String[] args) throws InterruptedException, IOException {
		yt.freeYoutuber(yt, driver);

	}

	public void freeYoutuber(YouTubeAdFree yt, WebDriver driver) throws IOException {
		long curTime = System.currentTimeMillis();
		long tempCurTime = curTime;
		ChromeOptions opt = new ChromeOptions();
		opt.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
		driver = new ChromeDriver(opt);
		if (osName.toLowerCase().contains("window")) {
			log.info("Running on operating system: " + osName);
			System.out.println("Running on operating system: " + osName);
			System.setProperty("webdriver.chrome.driver", "C:/all-driver/chromedriver.exe");
			log.info("Property set, 'webdriver.chrome.driver': " + System.getProperty("webdriver.chrome.driver"));
			System.out.println("Property set, 'webdriver.chrome.driver': " + System.getProperty("webdriver.chrome.driver"));
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

		try {
			driver.manage().window().maximize();
			driver.get(ytURL);
			Thread.sleep(5000);
			ts(driver, yt.printTime(), " Opening URL: " + ytURL);
			yt.loadNewVideo(driver);
			log.info("Title: " + driver.getTitle());
			System.out.println("Title: " + driver.getTitle());
			currentURL = driver.getCurrentUrl();
			log.info("Current URL: " + currentURL);
			System.out.println("Current URL: " + currentURL);
			String startTime = yt.printTime();

			for (int i = 0; i < 10000000; i++) {
				if (driver.findElements(By.xpath(SkipAdButton)).size() > 0) {

					ts(driver, Integer.toString(i) + "_" + yt.printTime(), "Clicking 'Skip ad', count: " + (i + 1));
					driver.findElement(By.xpath(SkipAdButton)).click();
//					log.info("Start time: "+startTime+"; Time now: "+yt.printTime());
					System.out.println("Start time: "+startTime+"; Time now: "+yt.printTime());

				} else {
					Thread.sleep(5000);
					log.warn("Start time: " + startTime + "; Current time: " + yt.printTime());
					System.out.println("Start time: " + startTime + "; Current time: " + yt.printTime());
				}
				if (i > 0 && i % 1 == 0)
					log.info("Screen time: " + getScreenTime(curTime));
				System.out.println("Screen time: " + getScreenTime(curTime));

				log.info("tempCurTime: " + tempCurTime + "; time since current video: "
						+ (System.currentTimeMillis() - tempCurTime) / 1000 + " seconds;  Current URL: "
						+ driver.getCurrentUrl() + "; previous URL: " + currentURL);
				System.out.println("tempCurTime: " + tempCurTime + "; time since current video: "
						+ (System.currentTimeMillis() - tempCurTime) / 1000 + " seconds;  Current URL: "
						+ driver.getCurrentUrl() + "; previous URL: " + currentURL);
//				if (currentMin > 30 && !(driver.getCurrentUrl().contentEquals(currentURL))) {
				if ((System.currentTimeMillis() - tempCurTime) > 1800000
						&& !(driver.getCurrentUrl().contentEquals(currentURL))) {
					log.info("Video changed within " + currentSec + " seconds,  changing video!");
					System.out.println("Video changed within " + currentSec + " seconds,  changing video!");
					loadNewVideo(driver);
					tempCurTime = System.currentTimeMillis();
				} else {
					log.info("No video change");
					System.out.println("No video change");
				}

			}

			driver.quit();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.error("InterruptedException exception occurred");
			System.out.println("InterruptedException exception occurred");
			e.printStackTrace();
			driver.quit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("IO_Exception exception occurred");
			System.out.println("IO_Exception exception occurred");
			e.printStackTrace();
			driver.quit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Some exception occurred");
			System.out.println("Some exception occurred");
			e.printStackTrace();
			yt.ts(driver, "failedScreen_" + yt.printTime(), "Some exception occurred");
			driver.quit();
		}

	}

	public ArrayList<String> readTextFile() {

		ArrayList<String> lineList = new ArrayList<String>();
		String tempFilePath = "";
		String currUser = System.getProperty("user.name");
		if (currUser.contains("hmd")) {
			tempFilePath = "C:/Users/" + currUser + "/Desktop/YouTubeForKids.txt";
		} else
			tempFilePath = "C:/Users/Hp/Desktop/YouTubeForKids.txt";

		File file = new File(tempFilePath);

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st;

			while ((st = br.readLine()) != null) {
				lineList.add(st);
			}

			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("IOException exception occurred");
			System.out.println("IOException exception occurred");
		}
		Collections.shuffle(lineList);
		return lineList;

	}

	public String printTime() {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d-MMM-yyyy_HH-mm-ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);

	}

	public static ArrayList<String> randomizeVideoTitle() {

		ArrayList<String> videoTitleList = new ArrayList<String>();
		videoTitleList.add("Baby Cat I Black sheep #soundvariations -   Baby songs - Nursery Rhymes & Kids Songs");
		videoTitleList.add("Omar & Hana I 40 minutes compilation of series   I Islamic Cartoons");
		videoTitleList
				.add("Durood Ibrahim Song (Allah Humma Salli) + More   Islamic Songs For Kids Compilation I Nasheed");
		videoTitleList.add(
				"Old MacDonald Had A Farm and Many More   Nursery Rhymes for Children I Kids Songs by   ChuChu TV");
		videoTitleList.add("The Best Alhamdulilah Song + More Islamic Songs for kids Compilation I Nasheed");
		videoTitleList.add("ABC Song with ChuChu Toy Train");
		videoTitleList.add("Baby Loves Stargazing - Twinkle Twinkle Little Star");
		videoTitleList.add("ABC Song + More Nursery Rhymes");
		videoTitleList.add("Phonics Song, Learn Abc and Preschool Rhymes for Kids");
		videoTitleList.add("Phonics Song with TWO Words - A For Apple - ABC Alphabet Songs");
		videoTitleList.add("ChuChu TV Numbers Song - NEW Short Version");
		videoTitleList.add("Number song 1-20 for children | Counting numbers | The Singing Walrus");
		videoTitleList.add("Living Things | Science Song for Kids | Elementary Life Science");
		videoTitleList.add("The Seed Song - What Do Seeds Need?");
		videoTitleList.add("Fish Vs Bird | 4KUHD | Blue Planet II | BBC Earth");
		videoTitleList.add("Bird Of Paradise Courtship Spectacle");
		videoTitleList.add("SCUBA Diving Egypt Red Sea - Underwater Video HD");

		Collections.shuffle(videoTitleList);

		return videoTitleList;
	}

	public void ts(WebDriver driver, String name, String message) throws IOException {

		TakesScreenshot scrShot = ((TakesScreenshot) driver);
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File DestFile = new File("C:/all-screenshot/" + name + ".png");
		FileUtils.copyFile(SrcFile, DestFile);
		log.info(message + " scr name: " + name + ".png");
		System.out.println(message + " scr name: " + name + ".png");
	}

	public String getScreenTime(long startTime) {
		long tempVal = 45678909876545L;
		String timeStringReturn = "";
		long timeDiff = (System.currentTimeMillis() - startTime);
		if (timeDiff / 3600000 > 0) {
			tempVal = timeDiff / 3600000;
			timeStringReturn = timeStringReturn + tempVal + " hours; ";
			currentHour = (int) tempVal;
			timeDiff = timeDiff % 3600000;
		}

		if (timeDiff / 60000 > 0) {
			tempVal = timeDiff / 60000;
			timeStringReturn = timeStringReturn + tempVal + " minutes; ";
			currentMin = (int) tempVal;
			timeDiff = timeDiff % 60000;
		}

		if (timeDiff / 1000 > 0) {
			tempVal = timeDiff / 1000;
			timeStringReturn = timeStringReturn + tempVal + " seconds;";
			currentSec = (int) tempVal;
		}

		return timeStringReturn;
		// mil to sec /1000
		// mil to min /60000
		// mil to hour /360000
	}

	public void loadNewVideo(WebDriver driver) throws InterruptedException, AWTException {

		rb = new Robot();
		log.info("Loading new video...");
		System.out.println("Loading new video...");
		String txtVideoTitle = "";
		rb.keyPress(KeyEvent.VK_ESCAPE);
		rb.keyPress(KeyEvent.VK_ESCAPE);
		rb.keyPress(KeyEvent.VK_ESCAPE);
		rb.keyPress(KeyEvent.VK_ESCAPE);
		try {
			ArrayList<String> tempList = new ArrayList<String>();
			tempList = yt.readTextFile();
			int tempInt = tempList.size();
			txtVideoTitle = yt.readTextFile().get(tempInt / 2 + 1);
			log.info("Loading video title from text file: " + txtVideoTitle);
			System.out.println("Loading video title from text file: " + txtVideoTitle);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			ArrayList<String> tempList = randomizeVideoTitle();
			int tempInt = tempList.size();
			txtVideoTitle = tempList.get(tempInt / 2 + 1);
			log.info("Loading video title from code: " + txtVideoTitle);
			System.out.println("Loading video title from code: " + txtVideoTitle);
		}

		WebElement searchBox = driver.findElement(By.xpath(searchBoxXpath));
		searchBox.clear();
		searchBox.sendKeys(txtVideoTitle);
		WebElement searchButton = driver.findElement(By.xpath(searchIcon));
		searchButton.click();
		try {
			driver.findElement(By.xpath(searchResultFirst)).click();
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			searchButton.click();
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].click();", searchButton);
			log.info("Clicked using JavascriptExecutor");
			System.out.println("Clicked using JavascriptExecutor");
			Thread.sleep(3000);
			driver.findElement(By.xpath(searchResultFirst)).click();
		}
		driver.findElement(By.xpath(fullScreenVideo)).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath(videoScreen)).click();
		for(int i=0;i<20;i++) {
			rb.keyPress(KeyEvent.VK_DOWN);
			Thread.sleep(100);
		}
		rb.keyPress(KeyEvent.VK_UP);
		rb.keyPress(KeyEvent.VK_UP);
		rb.keyPress(KeyEvent.VK_UP);
		rb.keyPress(KeyEvent.VK_UP);
		Thread.sleep(500);
		driver.findElement(By.xpath(videoScreen)).click();
		Thread.sleep(5000);
		currentURL = driver.getCurrentUrl();
	}

}

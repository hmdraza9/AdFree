package com.youtube.adfree;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

public class YouTubeAdFree {
	private static final Logger log = LogManager.getLogger(YouTubeAdFree.class);
	static YouTubeAdFree yt = new YouTubeAdFree();
	File file;
	JavascriptExecutor js;
	public static String currentURL;
	public static int currentHour;
	public static int currentMin;
	public static int currentSec;
	public static int waitBeforeVideoShuffle = 60;
	public static String osName = System.getProperty("os.name");
	public static String ytURL = "https://www.youtube.com";
	public static String searchBoxXpath = "//input[@id='search']";
	public static String videoScreen = "//div[@class='html5-video-container']/video";
	public static String SkipAdButton = "//div[contains(@id,'ad-text') and contains(text(),'Skip')]/parent::button";
	public static String searchIcon = "//button[contains(@id,'search-icon')]";
	public static String searchResultFirst = "(//a[@id='video-title'])[1]";
	public static String fullScreenVideo = "//button[contains(@title,'ull screen')]";
	public static String ytChannelTabXpath = "//div[@id='tabsContent']//*[@role='tab']//div[text()='$tab']";
	public static String ytChannelLatestVideoXpath = "//div[@id='player-container']/parent::*/div[@id='content']//div[@id='metadata-container']//a";

	static WebDriver driver;

	public static void main(String[] args) throws InterruptedException, IOException {
		do {
			try {
				yt.freeYoutuber(yt);
			} catch (IOException e) {
				e.printStackTrace();
				yt.freeYoutuber(yt);
			}
		} while (false);// making this true will run this code indefinitely
//		System.out.println("readTextFile: "+yt.readTextFile().get(0));

	}

	public void freeYoutuber(YouTubeAdFree yt) throws IOException {
		long curTime = System.currentTimeMillis();
		long tempCurTime = curTime;
		ChromeOptions opt = new ChromeOptions();
		opt.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));// com.youtube.adfree
		driver = new ChromeDriver(opt);
		if (osName.toLowerCase().contains("window")) {
			printSolution("Running on operating system: " + osName);
		}
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

		try {
			driver.manage().window().maximize();
			driver.get(ytURL);
			Thread.sleep(5000);
			ts(driver, yt.printTime(), "Opening URL: " + ytURL);
			yt.loadNewVideo(driver);
			printSolution("Current Title: " + driver.getTitle());
			currentURL = driver.getCurrentUrl();
			printSolution("Current URL: " + currentURL);
			String startTime = yt.printTime();
			String tempWait = yt.propsReader("waitBeforeVideoShuffle");
			try {
				if (tempWait == null)
					waitBeforeVideoShuffle = 30;
				else
					waitBeforeVideoShuffle = Integer.valueOf(tempWait);
			} catch (Exception e) {
				waitBeforeVideoShuffle = 30;
			}

			printSolution("waitBeforeVideoShuffle = " + waitBeforeVideoShuffle + " mins");
			for (int i = 0; i < 10000000; i++) {
				if (driver.findElements(By.xpath(SkipAdButton)).size() > 0) {

					ts(driver, Integer.toString(i) + "_" + yt.printTime(), "Clicking 'Skip ad', count: " + (i + 1));

					js = (JavascriptExecutor) driver;
					js.executeScript(
							"window.scrollTo(0, Math.max(document.documentElement.scrollHeight, document.body.scrollHeight, document.documentElement.clientHeight));");
					driver.findElement(By.xpath(SkipAdButton)).click();
//					printSolution("Start time: "+startTime+"; Time now: "+yt.printTime());

				} else {
					Thread.sleep(5000);
					log.warn("Start time: " + startTime + "; Current time: " + yt.printTime());
				}
				if (i > 0 && i % 1 == 0) {
					printSolution("Screen time: " + getScreenTime(curTime));
				}

				printSolution("tempCurTime: " + tempCurTime + "; time since current video: "
						+ (System.currentTimeMillis() - tempCurTime) / 1000 + " seconds;  Current URL: "
						+ driver.getCurrentUrl() + "; previous URL: " + currentURL);
//				if (currentMin > 30 && !(driver.getCurrentUrl().contentEquals(currentURL))) {
				printSolution("Current time in ms: " + System.currentTimeMillis());
				printSolution("Current video start time: " + tempCurTime);
				printSolution("Video change cut off ms: " + (System.currentTimeMillis() - tempCurTime));
				printSolution("Wait before video change value: " + (waitBeforeVideoShuffle * 60 * 1000));
				if ((System.currentTimeMillis() - tempCurTime) > (waitBeforeVideoShuffle * 60 * 1000)
						&& !(driver.getCurrentUrl().contentEquals(currentURL))) {
					printSolution("Video changed within " + currentSec + " seconds,  changing video!");
					loadNewVideo(driver);
					tempCurTime = System.currentTimeMillis();
				} else {
					printSolution("No video change");
				}

			}

			driver.quit();
		} catch (InterruptedException e) {
			log.error("InterruptedException occurred");
			e.printStackTrace();
			yt.ts(driver, "failedScreen_" + yt.printTime(), "InterruptedException occurred");
			driver.quit();
			printSolution("Exception : " + e.getLocalizedMessage().replace("\n", "||"));
		} catch (IOException e) {
			log.error("IO_Exception exception occurred");
			e.printStackTrace();
			yt.ts(driver, "failedScreen_" + yt.printTime(), "IO_Exception occurred");
			driver.quit();
			printSolution("Exception : " + e.getLocalizedMessage().replace("\n", "||"));
		} catch (Exception e) {
			log.error("Some exception occurred");
			e.printStackTrace();
			driver.quit();
			printSolution("Exception : " + e.getLocalizedMessage().replace("\n", "||"));
		} finally {
			driver.quit();
		}

	}

	public ArrayList<String> readTextFile() {

		ArrayList<String> lineList = new ArrayList<String>();
		String tempFilePath = "";
		String currUser = System.getProperty("user.name");

		tempFilePath = "src/test/resources/YouTubeForKids.txt";

		file = new File(tempFilePath);

		if (!file.exists()) {
			log.info("Getting title from C:User location file.");
			if (currUser.contains("hmd")) {
				tempFilePath = "C:/Users/" + currUser + "/Desktop/YouTubeForKids.txt";
			} else {
				tempFilePath = "C:/Users/Hp/Desktop/YouTubeForKids.txt";
				file = new File(tempFilePath);
			}

		} else {
			log.info("Getting title from project file.");
			file = new File(tempFilePath);
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st;

			while ((st = br.readLine()) != null) {
				lineList.add(st);
			}

			br.close();
		} catch (IOException e) {
			log.error("IOException exception occurred");
		}
		Collections.shuffle(lineList);
		return lineList;

	}

	public String printTime() {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d-MMM-yyyy_HH-mm-ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);

	}

	public String propsReader(String keyTerm) {
		String returnString = "";
		String propsPath = "C:/all-props/config.properties";

		try {
			FileInputStream fis = new FileInputStream(new File(propsPath));
			Properties props = new Properties();
			props.load(fis);
			returnString = props.getProperty(keyTerm);
		} catch (FileNotFoundException e) {
			printSolution("Properties file not found at: " + propsPath);
			e.printStackTrace();
		} catch (IOException e) {
			printSolution("Properties file not found at: " + propsPath);
			e.printStackTrace();
		}
		printSolution("Returned from props: " + returnString);
		return returnString;
	}

	public static ArrayList<String> randomizeVideoTitle() {

		ArrayList<String> videoTitleList = new ArrayList<String>();
		videoTitleList.add("https://www.youtube.com/watch?v=136P-4EfryA");
		videoTitleList.add("https://www.youtube.com/watch?v=Si5auXCYWDI");
		videoTitleList.add("https://www.youtube.com/watch?v=ZarMVS7MQSk");
		videoTitleList.add("https://www.youtube.com/watch?v=WJQJQrbsuIE");
		videoTitleList.add("Masha and the Bear");
		videoTitleList.add("https://www.youtube.com/watch?v=r9i1sR2Q9GE");
		videoTitleList.add("https://www.youtube.com/watch?v=VXAKb1lagw4");
		videoTitleList.add("https://www.youtube.com/watch?v=aaqZZdbDA3Y");
		videoTitleList.add("https://www.youtube.com/watch?v=cGM_VhNXMmo");
		videoTitleList.add("Masha and the Bear");
		videoTitleList.add("Masha and the Bear");
		videoTitleList.add("https://www.youtube.com/watch?v=mbv63Lz2vAw");
		videoTitleList.add("https://www.youtube.com/watch?v=mbv63Lz2vAw");
		videoTitleList.add("Number song 1-20 for children | Counting numbers | The Singing Walrus");
		videoTitleList.add("Baby Cat I Black sheep #soundvariations -   Baby songs - Nursery Rhymes & Kids Songs");

		Collections.shuffle(videoTitleList);

		return videoTitleList;
	}

	public void ts(WebDriver driver, String name, String message) throws IOException {

		TakesScreenshot scrShot = ((TakesScreenshot) driver);
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File DestFile = new File("C:/all-screenshot/" + name + ".png");
		FileUtils.copyFile(SrcFile, DestFile);
		printSolution(message + " scr name: " + name + ".png");
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

	public void loadNewVideo(WebDriver driver) throws InterruptedException, AWTException, IOException {

		printSolution("Loading new video...");
		String txtVideoTitle = "";
		Robot rb = new Robot();
		rb.keyPress(KeyEvent.VK_ESCAPE);
		rb.keyPress(KeyEvent.VK_ESCAPE);
		rb.keyPress(KeyEvent.VK_ESCAPE);
		rb.keyPress(KeyEvent.VK_ESCAPE);
		try {
			ArrayList<String> tempList = new ArrayList<String>();
			tempList = yt.readTextFile();
			int tempInt = tempList.size();
			if (tempInt > 1) {
				txtVideoTitle = tempList.get(tempInt / 2 + 1);
			} else
				txtVideoTitle = tempList.get(0);
			printSolution("Loading video title from file: " + txtVideoTitle);
		} catch (Exception ex) {
			printSolution("Exception: " + ex.getMessage());
			ArrayList<String> tempList = randomizeVideoTitle();
			int tempInt = tempList.size();
			if (tempInt > 1) {
				txtVideoTitle = tempList.get(tempInt / 2 + 1);
			} else
				txtVideoTitle = tempList.get(0);
			printSolution("Loading video title from code: " + txtVideoTitle);
		}

		if (!txtVideoTitle.contains("www.youtube.com")) {
			printSolution("Loading video by title: " + txtVideoTitle);
			WebElement searchBox = driver.findElement(By.xpath(searchBoxXpath));
			searchBox.clear();
			searchBox.sendKeys(txtVideoTitle);
			WebElement searchButton = driver.findElement(By.xpath(searchIcon));
			searchButton.click();
			try {
				driver.findElement(By.xpath(searchResultFirst)).click();
			} catch (NoSuchElementException e) {
				e.printStackTrace();
				searchButton.click();
				js = (JavascriptExecutor) driver;
				js.executeScript("arguments[0].click();", searchButton);
				searchButton.submit();
				printSolution("Clicked using JavascriptExecutor");
				Thread.sleep(3000);
				driver.findElement(By.xpath(searchResultFirst)).click();
			}
			try {
				driver.findElement(By.xpath(fullScreenVideo)).click();
			} catch (Exception e) {
				e.printStackTrace();
				rb.keyPress(KeyEvent.VK_F);
			}
			Thread.sleep(1000);
		} else {
			printSolution("Loading video by URL: " + txtVideoTitle);
			driver.get(txtVideoTitle);

			try {
				playChannelVideo(driver, txtVideoTitle, rb);
			} catch (Exception e) {
				e.printStackTrace();
				ts(driver, "", "");
			}

			Thread.sleep(1000);

			try {
				WebElement temp = driver.findElement(By.xpath(fullScreenVideo));
				temp.click();
				log.info("Fullscreen element class attribute: " + temp.getAttribute("class"));
				log.info("Clicked fullscreen with click()");
			} catch (ElementNotInteractableException eni) {
				WebElement temp = driver.findElement(By.xpath(fullScreenVideo));
				log.info("Fullscreen element class attribute: " + temp.getAttribute("class"));
				Actions action = new Actions(driver);
				action.moveToElement(temp).click();
				log.info("Clicked fullscreen with Actions");
			} catch (Exception e) {
				e.printStackTrace();
				rb.keyPress(KeyEvent.VK_F);
				log.info("Clicked fullscreen with Robot");
			}
			Thread.sleep(2000);
		}
		yt.controlVolume(driver, rb, 20);
		Thread.sleep(5000);

		currentURL = driver.getCurrentUrl();
	}

	public static void playChannelVideo(WebDriver driver, String txtChannelUrl, Robot rb) {

		if (txtChannelUrl.contains("www.youtube.com/@")) {

			log.info("Loading channel: " + txtChannelUrl.substring(25) + "'s latest video");
			driver.findElement(By.xpath(ytChannelTabXpath.replace("$tab", "Home"))).click();
			driver.findElement(By.xpath(ytChannelLatestVideoXpath)).click();
//			rb.keyPress(KeyEvent.VK_F);
		}

	}

	public void controlVolume(WebDriver driver, Robot rb, int defaultVolume) {

//		try {
//			rb.keyPress(KeyEvent.VK_F);
//			for (int i = 0; i < 20; i++) {
//				rb.keyPress(KeyEvent.VK_DOWN);
//				Thread.sleep(100);
//			}
//			String desiredVolumeInPercent;
//			try {
//				desiredVolumeInPercent = yt.propsReader("desiredVolumeInPercent");
//			} catch (Exception e) {
//				e.printStackTrace();
//				desiredVolumeInPercent = String.valueOf(defaultVolume);
//			}
//			int tempInt = Integer.parseInt(desiredVolumeInPercent) / 5;
//			for (int i = 0; i < tempInt; i++) {
//				rb.keyPress(KeyEvent.VK_UP);
//				Thread.sleep(100);
//			}
//
////			driver.findElement(By.xpath(videoScreen)).click();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public static void printSolution(String toPrint) {

		log.info(toPrint);
//		System.out.println(toPrint);

	}

}

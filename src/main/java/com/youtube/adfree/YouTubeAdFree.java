package com.youtube.adfree;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class YouTubeAdFree {
	
	Logger log = LogManager.getLogger(YouTubeAdFree.class);
	static YouTubeAdFree yt = new YouTubeAdFree();
	
	static WebDriver driver;

	public static void main(String[] args) throws InterruptedException, IOException {
				yt.freeYoutuber(yt, driver);
		
	}
	
	public void freeYoutuber(YouTubeAdFree yt, WebDriver driver) {
		String txtVideoTitle = "";
		try {
			ArrayList<String> tempList = new ArrayList<String>();
			tempList = yt.readTextFile();
			int tempInt = tempList.size();
			txtVideoTitle = yt.readTextFile().get(tempInt/2+1);
			log.info("Loading video title from text file: "+txtVideoTitle);
		}
		catch(Exception ex) {
			System.out.println("Exception: "+ex.getMessage());
			ArrayList<String> tempList = randomizeVideoTitle();
			int tempInt = tempList.size();
			txtVideoTitle = tempList.get(tempInt/2+1);
			log.warn("Loading video title from code: "+txtVideoTitle);
		}
		driver = new ChromeDriver();
		String osName = System.getProperty("os.name");
		String searchBoxXpath = "//input[@id='search']";
		String firstVideoDuration = "//span[@class='ytp-time-duration']";
		String SkipAdButton = "//div[contains(@id,'ad-text') and contains(text(),'Skip')]/parent::button";
		String searchIcon = "//button[contains(@id,'search-icon')]";
		String searchResultFirst = "(//a[@id='video-title'])[1]";
		String fullScreenVideo = "//button[contains(@title,'ull screen')]";
		if (osName.toLowerCase().contains("window")) {
			log.info("Running on operating system: " + osName);
			System.setProperty("webdriver.chrome.driver", "C:/all-driver/chromedriver.exe");
			log.info("Property set, 'webdriver.chrome.driver': "+System.getProperty("webdriver.chrome.driver"));
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		
		try {
			driver.manage().window().maximize();
			driver.get("https://www.youtube.com");
			Thread.sleep(5000);
			ts(driver, yt.printTime());
			WebElement searchBox = driver.findElement(By.xpath(searchBoxXpath));
			searchBox.sendKeys(txtVideoTitle);
			WebElement searchButton = driver.findElement(By.xpath(searchIcon));
			searchButton.click();
			driver.findElement(By.xpath(searchResultFirst)).click();
			driver.findElement(By.xpath(fullScreenVideo)).click();
			log.info("Title: "+driver.getTitle());
			String startTime = yt.printTime();

			for (int i = 0; i < 10000000; i++) {
				if (driver.findElements(By.xpath(SkipAdButton)).size() > 0) {

					ts(driver, Integer.toString(i)+"_"+yt.printTime());
					driver.findElement(By.xpath(SkipAdButton)).click();
					log.info("Start time: "+startTime+"; Time now: "+yt.printTime());

				} else {
					Thread.sleep(5000);
					log.warn("Start time: "+startTime+"; Time now: "+yt.printTime());
				}
			}

			driver.quit();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.error("InterruptedException exception occurred");
			e.printStackTrace();
			driver.quit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("IOException exception occurred");
			e.printStackTrace();
			driver.quit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Some exception occurred");
			e.printStackTrace();
			driver.quit();
		}
		
	}

	public ArrayList<String> readTextFile() {
		
		ArrayList<String> lineList = new ArrayList<String>();
		String tempFilePath = "C:/Users/"+System.getProperty("user.name")+"/Desktop/YouTubeForKids.txt";
		File file = new File(tempFilePath);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st;
			
			while((st=br.readLine())!=null) {
				lineList.add(st);				
			}

			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("IOException exception occurred");
		}
		Collections.shuffle(lineList);
		return lineList;
		
	}
	

	public String printTime() {

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);

	}

	public static ArrayList<String> randomizeVideoTitle() {
		
		ArrayList<String> videoTitleList= new ArrayList<String>();
		videoTitleList.add("Baby Cat I Black sheep #soundvariations -   Baby songs - Nursery Rhymes & Kids Songs");
		videoTitleList.add("Omar & Hana I 40 minutes compilation of series   I Islamic Cartoons");
		videoTitleList.add("Durood Ibrahim Song (Allah Humma Salli) + More   Islamic Songs For Kids Compilation I Nasheed");
		videoTitleList.add("Old MacDonald Had A Farm and Many More   Nursery Rhymes for Children I Kids Songs by   ChuChu TV");
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


	public void ts(WebDriver driver, String name) throws IOException {

		TakesScreenshot scrShot = ((TakesScreenshot) driver);
		File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
		File DestFile = new File("C:/all-screenshot/" + name + ".png");
		FileUtils.copyFile(SrcFile, DestFile);
		log.info("scr done! "+name + ".png");
	}


}

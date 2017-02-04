package core;
 
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import java.io.*;
import java.math.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
 
public class JSON_API_eCOM {
 
       public static void main(String[] args) throws InterruptedException, IOException {
              String us_currency_symbol = "$";
 
              String ip_Euro = "88.191.179.56";
              String ip_Yuan = "61.135.248.220";
              String ip_Pound = "92.40.254.196";
              String ip_Hryvnia = "93.183.203.67";
              String ip_Ruble = "213.87.141.36";
 
             ////////////////////////////////////////////////////////////////////////////////
 
              Logger logger = Logger.getLogger("");
              logger.setLevel(Level.OFF);

             String url = "https://www.amazon.com/All-New-Amazon-Echo-Dot-Add-Alexa-To-Any-Room/dp/B01DFKC2SO";

              WebDriver driver;
              System.setProperty("webdriver.chrome.driver", "./src/main/resources/chromedriver");
              System.setProperty("webdriver.chrome.silentOutput", "true");
              ChromeOptions option = new ChromeOptions();
              option.addArguments("-start-fullscreen");
              driver = new ChromeDriver(option);
              driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
              driver.get(url);
 
             // All-New Echo Dot (2nd Generation) - Black
              String product_title = driver.findElement(By.id("productTitle")).getText();   
double original_price = Double.parseDouble(driver.findElement(By.id("priceblock_ourprice")).getText().replace("$", "")); // 49.99

              driver.quit();
 
             ////////////////////////////////////////////////////////////////////////////////
 
              //URL api_url = new URL("http://www.geoplugin.net/json.gp?ip=" + ip_Ruble);
              URL api_url = new URL("http://www.geoplugin.net/json.gp?ip=" + ip_Yuan);
              final String e_cName = "geoplugin_countryName";
              final String e_cCode = "geoplugin_currencyCode";
              final String e_cSymbol = "geoplugin_currencySymbol_UTF8";
              String country_name = null;
              String currency_code = null;
              String currency_symbol = null;
              InputStream is = api_url.openStream();
              JsonParser parser = Json.createParser(is);
 
              while (parser.hasNext()) {
                     Event e = parser.next();
                     if (e == Event.KEY_NAME) {switch (parser.getString()) {
 
             case e_cName: parser.next(); country_name = parser.getString();break;   // France
              case e_cCode:parser.next(); currency_code = parser.getString();break;      // EUR
             case e_cSymbol:parser.next(); currency_symbol = parser.getString();break;}}} // €
 
             ////////////////////////////////////////////////////////////////////////////////
 
       		double rate = 0;
              String rate_id = "USD" + currency_code;          // USDEUR
             // select * from yahoo.finance.xchange where pair in ("USDEUR")
 
String rate_sql = "select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(\"" + rate_id + "\")";
URL rate_url = new URL("http://query.yahooapis.com/v1/public/yql?q=" + rate_sql + "&format=json&env=store://datatables.org/alltableswithkeys");

              InputStream is2 = rate_url.openStream();
              JsonParser jp = Json.createParser(is2);      
              is2 = rate_url.openStream();
              jp = Json.createParser(is2);
 
              while (jp.hasNext()) {
                     Event c = jp.next();
                     if(c == Event.KEY_NAME) {switch (jp.getString()) {
 
             case "Rate": jp.next(); rate = Double.parseDouble(jp.getString()); break;}}} // 0.9345
 
             ///////////////////////////////////////////////////////////////////////////////
 
       double eur_price = new BigDecimal(original_price * rate).setScale(2, RoundingMode.HALF_UP).doubleValue();
       System.out.println("Item: " + product_title + "; "
                                   + "US Price: " + us_currency_symbol + original_price + "; "
                                   + "for country: " + country_name + "; "
                                   + "Local Price: " + currency_symbol + eur_price);
    }
}


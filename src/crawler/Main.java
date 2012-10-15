package crawler;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("xOmduR5VzLHDqvHk2HtVg")
		  .setOAuthConsumerSecret("mMwh7MQfd31kNtnmEC8xLhF73PYDAQCnSIBfQ1hnQw")
		  .setOAuthAccessToken("379667899-DmETSxIDlODECIfzmR4V2DZjcrxmaj7PhQ4IH6d0")
		  .setOAuthAccessTokenSecret("bYnZ1wHSg0XLpnLEWzIuQTTWRPAvggwtzcXQQjp0H4")
		  .setJSONStoreEnabled(true);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		String query = "apple";  
		
		TweetCrawler twcrawler = new TweetCrawler(twitter, query);
		
		twcrawler.setPathToSave("/DATA/tweets");
		
		twcrawler.start();
		
	}

}

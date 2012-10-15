package crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RelatedResults;
import twitter4j.Status;
import twitter4j.User;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TweetCrawler extends Thread {

	private final int INTERVALS = 10000;
	private Twitter twitter;
	private String query;
	private HashSet<String> rawData;
	private String pathToSave;
	
	/* Constructor of class */

	public TweetCrawler(Twitter twitter, String query) {
		this.twitter = twitter;
		this.query = query;
		rawData = new HashSet<String>();
	}

	// Override method run()

	public void run() {

		QueryResult qr = null;				
		
		int count = 0;
		int page = 1;				
		while (true) {
			try {
				Query tquery = new Query(query);
				tquery.setLang("en");
				tquery.setPage(page);				
				qr = twitter.search(tquery);

				List<Tweet> tweets = qr.getTweets();

				if (tweets.size() == 0) {

					try {
						Thread.sleep(INTERVALS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				for (Tweet tweet : tweets) {	
					/* Format of the document crawler
					 * Line1: id
					 * Line2: text
					 * Line3: hashtag
					 * Line4: urls
					 * Line5: retweet:replies:followers:friends:status:lists
					 * 
					 * 
					 * */
					
					
					if(rawData.add(tweet.toString())){
						
						
						List<String> urlList = FindUrls.extractUrls(tweet.getText());
						
						long userID = tweet.getFromUserId();
						User user = twitter.showUser(userID);
						int followersCount = user.getFollowersCount();
						int statusCount = user.getStatusesCount();
						int friendsCount = user.getFavouritesCount();
						int listCount = user.getListedCount();
												
						
						File file = new File(pathToSave+"/"+query.replaceAll(" ", "_")+"_"+tweet.getId()+".txt");
						FileWriter fileWriter = new FileWriter(file);
						BufferedWriter buffWriter = new BufferedWriter(fileWriter);											
						
						
						//Writing here
						buffWriter.write(tweet.getId()+"\n"); //id of tweet
						buffWriter.write(tweet.getText()+"\n"); 	//content of tweet
						
						//Get hash tag
						HashtagEntity entities[] = tweet.getHashtagEntities();
						if(entities != null){
							if(entities.length!=0){
							buffWriter.write(entities.length+":");
							for(HashtagEntity entity : entities){
								buffWriter.write(entity.getText()+" ");
							}
							buffWriter.write("\n");
							}else{
								buffWriter.write("0\n");
							}
								
						}
						//URL in tweet
						if(urlList.isEmpty()){
							buffWriter.write("0\n");
						}else{
							buffWriter.write(urlList.size()+":");
							for(int i = 0; i < urlList.size(); i++){
								if(i != ( urlList.size() - 1 )){
									buffWriter.write(urlList.get(i)+" ");
								}else{
									buffWriter.write(urlList.get(i)+"\n");
								}
									
							}
						}
						
						
						
						buffWriter.write(twitter.getRetweets(tweet.getId()).size()+":"); //numer of retweet					
						//Check tweet has replies or not
						RelatedResults result = twitter.getRelatedResults(tweet.getId());
						List<Status> conversations = result.getTweetsWithConversation();
						if(!conversations.isEmpty()){
							buffWriter.write(conversations.size()+":");  // number of replies
						}else{
							buffWriter.write("0:");
						}
						buffWriter.write(followersCount+":"+friendsCount+":"+statusCount+":"+listCount);												
						
						//retweet:replies:followers:friends:status:lists
						
						//
						
						
						//Close file
						buffWriter.close();
						fileWriter.close();
						
						//Thread.sleep(10000);
					}
					
				}

			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				System.err.println(new Date().toLocaleString());
				//e.printStackTrace();
				System.out.println("Sleep in 500 seconds for limitation...");
				if (e.exceededRateLimitation())
					try {
						Thread.sleep(500000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			page++;
		}
	}
	
	public Twitter getTwitter() {
		return twitter;
	}

	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public void setPathToSave(String path){
		this.pathToSave = path;
	}
	
}

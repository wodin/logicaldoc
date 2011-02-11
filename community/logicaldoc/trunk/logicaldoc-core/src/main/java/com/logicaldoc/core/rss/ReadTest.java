package com.logicaldoc.core.rss;

public class ReadTest {
	public static void main(String[] args) {
		FeedParser parser = new FeedParser("http://www.logicaldoc.com/company/news.feed?type=rss");
			//new FeedParser("http://www.logicaldoc.com/company/news.feed?type=rss");
			//new FeedParser("http://www.vogella.de/article.rss");
		Feed feed = parser.readFeed();
		System.out.println(feed);
		for (FeedMessage message : feed.getMessages()) {
			System.out.println(message.getLink());
		}
	}
}

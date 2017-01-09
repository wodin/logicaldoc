package com.logicaldoc.core.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringUtils;

/**
 * Parses an RSS feed
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.1
 */
public class FeedParser {
	public static final String TITLE = "title";

	public static final String DESCRIPTION = "description";

	public static final String CHANNEL = "channel";

	public static final String LANGUAGE = "language";

	public static final String COPYRIGHT = "copyright";

	public static final String LINK = "link";

	public static final String AUTHOR = "author";

	public static final String ITEM = "item";

	public static final String PUB_DATE = "pubDate";

	public static final String GUID = "guid";

	public final URL url;

	public FeedParser(String feedUrl) {
		try {
			if (feedUrl != null && StringUtils.isNotEmpty(feedUrl))
				this.url = new URL(feedUrl);
			else
				this.url = new URL("http://www.logicaldoc.com/news/rss.html");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public Feed readFeed() {
		Feed feed = null;
		try {

			boolean isFeedHeader = true;
			// Set header values intial to the empty string
			String description = "";
			String title = "";
			String link = "";
			String language = "";
			String copyright = "";
			String author = "";
			String pubdate = "";
			String guid = "";

			SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = read();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document
			while (eventReader.hasNext()) {

				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart() == (ITEM)) {
						if (isFeedHeader) {
							isFeedHeader = false;
							feed = new Feed(title, link, description, language, copyright, pubdate);
						}
						event = eventReader.nextEvent();
						continue;
					}

					if (event.asStartElement().getName().getLocalPart() == (TITLE)) {
						event = eventReader.nextEvent();
						title = event.asCharacters().getData();
						continue;
					}
					if (event.asStartElement().getName().getLocalPart() == (DESCRIPTION)) {
						event = eventReader.nextEvent();
						try {
							description = event.asCharacters().getData();
						} catch (Throwable t) {
						}
						continue;
					}

					if (event.asStartElement().getName().getLocalPart() == (LINK)) {
						event = eventReader.nextEvent();
						try {
							link = event.asCharacters().getData();
						} catch (Throwable t) {

						}
						continue;
					}

					if (event.asStartElement().getName().getLocalPart() == (GUID)) {
						event = eventReader.nextEvent();
						guid = event.asCharacters().getData();
						continue;
					}
					if (event.asStartElement().getName().getLocalPart() == (LANGUAGE)) {
						event = eventReader.nextEvent();
						language = event.asCharacters().getData();
						continue;
					}
					if (event.asStartElement().getName().getLocalPart() == (AUTHOR)) {
						event = eventReader.nextEvent();
						author = event.asCharacters().getData();
						continue;
					}
					if (event.asStartElement().getName().getLocalPart() == (PUB_DATE)) {
						event = eventReader.nextEvent();
						pubdate = event.asCharacters().getData();
						continue;
					}
					if (event.asStartElement().getName().getLocalPart() == (COPYRIGHT)) {
						event = eventReader.nextEvent();
						copyright = event.asCharacters().getData();
						continue;
					}
				} else if (event.isEndElement()) {
					if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
						FeedMessage message = new FeedMessage();
						message.setAuthor(author);
						message.setDescription(description);
						message.setGuid(guid);
						message.setLink(link);
						message.setTitle(title);
						if (StringUtils.isNotBlank(pubdate))
							try {
								message.setPubDate(df.parse(pubdate));
							} catch (ParseException e) {
								System.err.println(e.getMessage());
							}
						feed.getMessages().add(message);
						event = eventReader.nextEvent();
						continue;
					}
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		return feed;

	}

	private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

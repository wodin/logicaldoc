package com.logicaldoc.core.rss;

import java.util.Locale;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.rss.dao.FeedMessageDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * This task retrieve and store all feeds from an Url.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class ProductNews extends Task {
	public static final String NAME = "ProductNews";

	private UserDAO userDao;

	private FeedMessageDAO feedMessageDao;

	private ContextProperties config;

	private long saved = 0;

	private long errors = 0;

	public ProductNews() {
		super(NAME);
		log = LogFactory.getLog(ProductNews.class);
	}

	@Override
	public boolean isIndeterminate() {
		return false;
	}

	@Override
	protected void runTask() throws Exception {
		log.info("Start retrieving news");
		errors = 0;
		saved = 0;
		size = 0;
		try {

			// Clean the DB from feed messages older that 1 year.
			feedMessageDao.deleteOld();

			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			String url = config.getProperty("news.url");
			FeedParser parser = new FeedParser(url);
			Feed feed = parser.readFeed();
			// First of all feed messages to be saved
			size = feed.getMessages().size();

			log.info("Found a total of " + size + " feed messages to be saved");

			for (FeedMessage message : feed.getMessages()) {
				try {
					log.debug("Parsing message " + message.getTitle());
					if (feedMessageDao.findByGuid(message.getGuid()) == null) {
						// The parsing message is not already saved into the
						// database, so we save it.
						feedMessageDao.store(message);
						saved++;
					}
					log.debug("Parsed message " + message.getTitle());
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
					errors++;
				} finally {
					next();
				}
				if (interruptRequested)
					return;
			}
		} finally {
			log.info("Retrieving news finished");
			log.info("Retrieved news: " + saved);
			log.info("Errors: " + errors);
		}
	}

	@Override
	protected String prepareReport(Locale locale) {
		StringBuffer sb = new StringBuffer();
		if (saved > 0)
			sb.append(I18N.message("feednewsfound", locale, new Object[] { saved }) + ": ");
		else
			sb.append(I18N.message("feednewsnotfound", locale));
		sb.append("\n");
		sb.append(I18N.message("errors", locale) + ": ");
		sb.append(errors);
		return sb.toString();
	}

	public UserDAO getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}

	public FeedMessageDAO getFeedMessageDao() {
		return feedMessageDao;
	}

	public void setFeedMessageDao(FeedMessageDAO feedMessageDao) {
		this.feedMessageDao = feedMessageDao;
	}

	public ContextProperties getConfig() {
		return config;
	}

	public void setConfig(ContextProperties config) {
		this.config = config;
	}
}

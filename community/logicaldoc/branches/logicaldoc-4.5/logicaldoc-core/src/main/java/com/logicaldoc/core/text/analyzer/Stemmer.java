package com.logicaldoc.core.text.analyzer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tartarus.snowball.SnowballProgram;

import com.logicaldoc.core.i18n.Language;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 */
class Stemmer {
	protected static Log log = LogFactory.getLog(Stemmer.class);

	// Wrapped snowball stemmer
	private SnowballProgram stemmer;

	private Method stemMethod;

	@SuppressWarnings("unchecked")
	public Stemmer(Language language) throws SecurityException, NoSuchMethodException {
		String lng = language.getDefaultDisplayLanguage();
		try {
			Class stemClass = Class.forName("org.tartarus.snowball.ext." + lng + "Stemmer");
			stemmer = (SnowballProgram) stemClass.newInstance();
			stemMethod = stemmer.getClass().getMethod("stem", new Class[0]);
		} catch (Exception e) {
			log.info("Error instantiating stemmer for language:" + language);
			log.info("Trying with English Stemmer.");
			// Default with English stemmer
			stemmer = new org.tartarus.snowball.ext.EnglishStemmer();
			stemMethod = stemmer.getClass().getMethod("stem", new Class[0]);
		}
	}

	/**
	 * Stemms the given term to a unique <tt>discriminator</tt>.
	 * 
	 * @param term java.lang.String The term that should be stemmed
	 * @return java.lang.String Discriminator for <tt>term</tt>
	 */
	public String stem(String term) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (term == null)
			return null;
		stemmer.setCurrent(term.toLowerCase());
		stemMethod.invoke(stemmer, new Object[0]);
		return stemmer.getCurrent();
	}
}
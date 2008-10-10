package com.logicaldoc.core.text.analyze;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;


/**
 * @author Michael Scholz
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AnalyzerFactory {
    
    private static Map<String, Analyzer> analyzers = new HashMap<String, Analyzer>();
    
    static {        
		// Get languages from LanguageManager
		Collection<Language> languages = LanguageManager.getInstance().getLanguages();
		for (Language language : languages) {
			String iso639_2 = language.getLanguage();
			Analyzer analyzer = new Analyzer(iso639_2, 4);
			analyzers.put(iso639_2, analyzer);
		}
    }
    
    public static Analyzer getAnalyzer(String language) {
        if ((language == null) || (!analyzers.containsKey(language)))
            return analyzers.get("en");
        return analyzers.get(language);
    } 
} 

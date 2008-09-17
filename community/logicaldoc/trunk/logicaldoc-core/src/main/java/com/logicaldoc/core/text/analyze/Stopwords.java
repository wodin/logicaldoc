package com.logicaldoc.core.text.analyze;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created on 13.01.2005
 */
public class Stopwords {
	
	protected static Log log = LogFactory.getLog(Stopwords.class);
	
    private static Map<String, String[]> stopwordsMap = new HashMap<String, String[]>();
	
	private final static String[] GERMAN_STOP_WORDS = { "der", "die", "das", "dass", "daß", "ein", "dies", "dem",
			"den", "des", "zu", "zum", "zur", "eine", "einer", "einem", "einen", "eines", "auf", "aus", "am", "im",
			"in", "um", "an", "und", "oder", "ohne", "mit", "ich", "du", "er", "sie", "es", "wir", "ihr", "mein",
			"sein", "dein", "euer", "eure", "mich", "dich", "sich", "durch", "wegen", "bei", "neben", "vor", "nach",
			"von", "vom", "als", "für", "wird", "werde", "werden", "werdet", "werd,", "wurde", "wurden", "wurdet",
			"wurdest", "wurd", "würde", "würden", "würdet", "würdest", "würd", "kann", "können", "konn", "muss", "muß",
			"müss", "müßte", "müssen", "musste", "mußtet", "mußte", "müsste", "wer", "wie", "was", "wem", "wen",
			"wessen", "wo", "womit", "wofür", "wodurch", "wobei", "wonach", "welch", "welcher", "welchen", "welches",
			"welchem", "nicht", "nur", "damit", "so", "auch", "ist", "sind", "war", "waren", "man", "also", "aber",
			"über", "soll", "will", "woll", "wollen", "wollt", "wolltet", "willst", "wollte", "wolltest", "wollten",
			"weil", "noch", "dabei", "dann", "danach", "ja", "nein", "immer", "nie", "jetzt", "heute", "mehr",
			"weniger", "all", "solch", "solcher", "solche", "solches", "solchen", "solchem", "bereits", "zwischen",
			"innen", "aussen", "außen", "innerhalb", "außerhalb", "wieder", "wider", "gegen", "wenn", "hat", "hab",
			"je", "jed", "jede", "jeder", "jedes", "jeden", "jedem", "gar", "dar", "einzeln", "möglich", "haben",
			"hat", "hatte", "hast", "hattest", "habt", "hattet", "hatten", "hätt", "hätte", "hätten", "hättest",
			"hättet", "oben", "unten", "über", "unter", "obere", "untere", "weiterhin", "desweiteren", "gut",
			"schlecht", "allgemein", "wichtig", "etwas", "anhand", "jedoch", "dazu", "dafür", "ers", "zwei", "drit",
			"drei", "vier", "erster", "zweiter", "dritter", "vierter", "beispiel", "beispielsweise", "bis", "neu",
			"neue", "neues", "neuen", "neuer", "neuem", "andere", "anderes", "anderen", "anderer", "anders", "anderem",
			"richtig", "falsch", "sowie" };

	private final static String[] FRENCH_STOP_WORDS = { "alors", "au", "aucuns", "aussi", "autre", "avant", "avec",
			"avoir", "bon", "car", "ce", "cela", "ces", "ceux", "chaque", "ci", "comme", "comment", "dans", "des",
			"du", "dedans", "dehors", "depuis", "deux", "devrait", "doit", "donc", "dos", "droite", "début", "elle",
			"elles", "en", "encore", "essai", "est", "et", "eu", "fait", "faites", "fois", "font", "force", "haut",
			"hors", "ici", "il", "ils", "je", "juste", "la", "le", "les", "leur", "là", "ma", "maintenant", "mais",
			"mes", "mine", "moins", "mon", "mot", "même", "ni", "nommés", "notre", "nous", "nouveaux", "ou", "où",
			"par", "parce", "parole", "pas", "personnes", "peut", "peu", "pièce", "plupart", "pour", "pourquoi",
			"quand", "que", "quel", "quelle", "quelles", "quels", "qui", "sa", "sans", "ses", "seulement", "si",
			"sien", "son", "sont", "sous", "soyez", "sujet", "sur", "ta", "tandis", "tellement", "tels", "tes", "ton",
			"tous", "tout", "trop", "très", "tu", "valeur", "voie", "voient", "vont", "votre", "vous", "vu", "ça",
			"étaient", "état", "étions", "été", "être" };

	private final static String[] ENGLISH_STOP_WORDS = { "a", "and", "are", "as", "at", "be", "but", "by", "for", "if",
			"in", "into", "is", "it", "no", "not", "of", "on", "or", "s", "such", "too", "that", "the", "their",
			"then", "there", "these", "they", "this", "to", "was", "will", "with", "thus", "have", "has", "had", "do",
			"did", "yes", "than", "those", "just", "like", "about", "which", "who", "what", "whom", "when", "where",
			"within", "without", "whose", "although", "all", "because", "while", "how", "here", "any", "some",
			"during", "next", "previous", "does", "between", "been", "one", "two", "three", "four", "five", "six",
			"seven", "eight", "nine", "ten", "bottom", "top", "down", "up", "left", "right", "whether", "whole",
			"also", "now", "onto", "still", "often", "more", "most", "good", "best", "go", "better", "gone", "went",
			"many", "much", "lot", "sever" };

	private final static String[] SPANISH_STOP_WORDS = { "un", "una", "unas", "unos", "uno", "sobre", "todo",
			"también", "tras", "otro", "algún", "alguno", "alguna",

			"algunos", "algunas", "ser", "es", "soy", "eres", "somos", "sois", "estoy", "esta", "estamos", "estais",

			"estan", "en", "para", "atras", "porque", "por qué", "estado", "estaba", "ante", "antes", "siendo",

			"ambos", "pero", "por", "poder", "puede", "puedo", "podemos", "podeis", "pueden", "fui", "fue", "fuimos",

			"fueron", "hacer", "hago", "hace", "hacemos", "haceis", "hacen", "cada", "fin", "incluso", "primero",

			"desde", "conseguir", "consigo", "consigue", "consigues", "conseguimos", "consiguen", "ir", "voy", "va",

			"vamos", "vais", "van", "vaya", "bueno", "ha", "tener", "tengo", "tiene", "tenemos", "teneis", "tienen",

			"el", "la", "lo", "las", "los", "su", "aqui", "mio", "tuyo", "ellos", "ellas", "nos", "nosotros",
			"vosotros",

			"vosotras", "si", "dentro", "solo", "solamente", "saber", "sabes", "sabe", "sabemos", "sabeis", "saben",

			"ultimo", "largo", "bastante", "haces", "muchos", "aquellos", "aquellas", "sus", "entonces", "tiempo",

			"verdad", "verdadero", "verdadera", "cierto", "ciertos", "cierta", "ciertas", "intentar", "intento",

			"intenta", "intentas", "intentamos", "intentais", "intentan", "dos", "bajo", "arriba", "encima", "usar",

			"uso", "usas", "usa", "usamos", "usais", "usan", "emplear", "empleo", "empleas", "emplean", "ampleamos",

			"empleais", "valor", "muy", "era", "eras", "eramos", "eran", "modo", "bien", "cual", "cuando", "donde",

			"mientras", "quien", "con", "entre", "sin", "trabajo", "trabajar", "trabajas", "trabaja", "trabajamos",

			"trabajais", "trabajan", "podria", "podrias", "podriamos", "podrian", "podriais", "yo", "aquel", "mi",

			"de", "a", "e", "i", "o", "u", "y" };

	/**
	 * A list of Italian stop words taken from
	 * <code><a href="http://www.snowball.tartarus.org/algorithms/italian/stop.txt">http://www.snowball.tartarus.org/algorithms/italian/stop.txt</a></code>
	 */
	private final static String[] ITALIAN_STOP_WORDS = { "a", "abbia", "abbiamo", "abbiano", "abbiate", "ad", "agl",
			"agli", "ai", "al", "all", "alla", "alle", "allo", "anche", "avemmo", "avendo", "avesse", "avessero",
			"avessi", "avessimo", "aveste", "avesti", "avete", "aveva", "avevamo", "avevano", "avevate", "avevi",
			"avevo", "avrà", "avrai", "avranno", "avrebbe", "avrebbero", "avrei", "avremmo", "avremo", "avreste",
			"avresti", "avrete", "avrò", "avuta", "avute", "avuti", "avuto", "c", "che", "chi", "ci", "coi", "come",
			"con", "contro", "cui", "da", "dagl", "dagli", "dai", "dal", "dall", "dalle", "dallo", "degl", "degli",
			"dei", "del", "dell", "della", "delle", "dello", "di", "dov", "dove", "e", "è", "ebbe", "ebbero", "ebbi",
			"ed", "erano", "eravamo", "eravate", "eri", "ero", "essendo", "fa", "fà", "facciamo", "facciano", "faccio",
			"facemmo", "facendo", "facesse", "facessero", "facessi", "facessimo", "faceste", "facesti", "faceva",
			"facevamo", "facevano", "facevate", "facevi", "facevo", "fai", "fanno", "farà", "farai", "faranno",
			"farebbe", "farebbero", "farei", "faremmo", "faremo", "fareste", "faresti", "farete", "farò", "fece",
			"fecero", "fossero", "fossimo", "foste", "fosti", "fu", "fui", "fummo", "furono", "gli", "ha", "hai",
			"hanno", "ho", "i", "il", "in", "io", "l", "la", "là", "le", "lei", "li", "lì", "lo", "loro", "lui", "ma",
			"mi", "mia", "mie", "miei", "mio", "ne", "negl", "negli", "nei", "nel", "nell", "nella", "nelle", "nello",
			"noi", "non", "nostra", "nostre", "nostri", "nostro", "o", "per", "perché", "più", "quale", "quanta",
			"quante", "quanti", "quanto", "quella", "quelle", "quelli", "quello", "questa", "queste", "questi",
			"questo", "sarà", "sarai", "saranno", "sarebbe", "sarebbero", "sarei", "saremmo", "saremo", "sareste",
			"saresti", "sarete", "sarò", "se", "sei", "si", "sì", "sia", "siamo", "siano", "siate", "siete", "sono",
			"sta", "stai", "stando", "stanno", "starà", "starai", "staranno", "starebbe", "starebbero", "starei",
			"staremmo", "staremo", "stareste", "staresti", "starete", "starò", "stava", "stavamo", "stavano",
			"stavate", "stavi", "stavo", "stemmo", "stesse", "stessero", "stessi", "stessimo", "steste", "stesti",
			"stette", "stettero", "stetti", "stia", "stiamo", "stiano", "stiate", "sto", "su", "sua", "sue", "sugl",
			"sugli", "sui", "sul", "sull", "sulla", "sulle", "sullo", "suo", "suoi", "ti", "tra", "tu", "tua", "tue",
			"tuo", "tuoi", "tutti", "tutto", "un", "una", "uno", "vi", "voi", "vostra", "vostre", "vostri", "vostro" };

    static {        
    	stopwordsMap.put("en", ENGLISH_STOP_WORDS);
    	stopwordsMap.put("de", GERMAN_STOP_WORDS);
    	stopwordsMap.put("es", SPANISH_STOP_WORDS);
    	stopwordsMap.put("it", ITALIAN_STOP_WORDS);
    	stopwordsMap.put("fr", FRENCH_STOP_WORDS);
    }
    
    public static void addStopwords(String iso639_2, String[] stopWords) {
    	stopwordsMap.put(iso639_2, stopWords);
    }
    
	public static String[] getStopwords(String iso639_2) {
		String[] stopWords = stopwordsMap.get(iso639_2);
		if (stopWords == null)
			stopWords = stopwordsMap.get("en");
		return stopWords;
	}
	
	public static String[] loadStopwords(String stopWordsFile) throws IOException {
		
		List<String> swlist = new ArrayList<String>();
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		log.fatal(loader.getResource(stopWordsFile));
		InputStream is =  loader.getResourceAsStream(stopWordsFile);
		
		Properties pp = new Properties();
		pp.load(is);
		Set<Object> xxx = pp.keySet();
		is.close();
		
		for (Object object : xxx) {
			swlist.add((String) object);
		}
		
		return (String[]) swlist.toArray(new String[0]);
	}
}

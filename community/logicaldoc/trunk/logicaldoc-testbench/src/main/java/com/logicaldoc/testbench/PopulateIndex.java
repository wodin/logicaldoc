package com.logicaldoc.testbench;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JEditorPane;
import javax.swing.text.DefaultEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

/**
 * Generates database records browsing an existing filesystem in LogicalDOC's
 * format, and accessing an existing LogicalDOC DB.
 * <p>
 * <b>NOTE:</b> The file system must be compliant with the one used by
 * LogicalDOC to store document archive files, so folders must be named with
 * internal menu id.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class PopulateIndex {
	public static final String FIELD_KEYWORDS = "keywords";

	public static final String FIELD_SUMMARY = "summary";

	public static final String FIELD_LENGTH = "length";

	public static final String FIELD_CONTENT = "content";

	public static final String FIELD_TYPE = "type";

	public static final String FIELD_PATH = "path";

	public static final String FIELD_SOURCE_TYPE = "sourceType";

	public static final String FIELD_DATE = "date";

	public static final String FIELD_SOURCE_DATE = "sourceDate";

	public static final String FIELD_COVERAGE = "coverage";

	public static final String FIELD_SOURCE_AUTHOR = "sourceAuthor";

	public static final String FIELD_SOURCE = "source";

	public static final String FIELD_SIZE = "size";

	public static final String FIELD_TITLE = "title";

	public static final String FIELD_DOC_ID = "docId";

	protected static Log log = LogFactory.getLog(PopulateIndex.class);

	private static Map<String, String[]> stopwordsMap = new HashMap<String, String[]>();

	private final static String[] GERMAN_STOP_WORDS = { "der", "die", "das", "dass", "daß", "ein", "dies", "dem",
			"den", "des", "zu", "zum", "zur", "eine", "einer", "einem", "einen", "eines", "auf", "aus", "am", "im",
			"in", "um", "an", "und", "oder", "ohne", "mit", "ich", "du", "er", "sie", "es", "wir", "ihr", "mein",
			"sein", "dein", "euer", "eure", "mich", "dich", "sich", "durch", "wegen", "bei", "neben", "vor", "nach",
			"von", "vom", "als", "für", "wird", "werde", "werden", "werdet", "werd,", "wurde", "wurden", "wurdet",
			"wurdest", "wurd", "würde", "würden", "würdet", "würdest", "würd", "kann", "können", "konn", "muss",
			"muß", "müss", "müßte", "müssen", "musste", "mußtet", "mußte", "müsste", "wer", "wie", "was",
			"wem", "wen", "wessen", "wo", "womit", "wofür", "wodurch", "wobei", "wonach", "welch", "welcher",
			"welchen", "welches", "welchem", "nicht", "nur", "damit", "so", "auch", "ist", "sind", "war", "waren",
			"man", "also", "aber", "über", "soll", "will", "woll", "wollen", "wollt", "wolltet", "willst", "wollte",
			"wolltest", "wollten", "weil", "noch", "dabei", "dann", "danach", "ja", "nein", "immer", "nie", "jetzt",
			"heute", "mehr", "weniger", "all", "solch", "solcher", "solche", "solches", "solchen", "solchem",
			"bereits", "zwischen", "innen", "aussen", "außen", "innerhalb", "außerhalb", "wieder", "wider", "gegen",
			"wenn", "hat", "hab", "je", "jed", "jede", "jeder", "jedes", "jeden", "jedem", "gar", "dar", "einzeln",
			"möglich", "haben", "hat", "hatte", "hast", "hattest", "habt", "hattet", "hatten", "hätt", "hätte",
			"hätten", "hättest", "hättet", "oben", "unten", "über", "unter", "obere", "untere", "weiterhin",
			"desweiteren", "gut", "schlecht", "allgemein", "wichtig", "etwas", "anhand", "jedoch", "dazu", "dafür",
			"ers", "zwei", "drit", "drei", "vier", "erster", "zweiter", "dritter", "vierter", "beispiel",
			"beispielsweise", "bis", "neu", "neue", "neues", "neuen", "neuer", "neuem", "andere", "anderes", "anderen",
			"anderer", "anders", "anderem", "richtig", "falsch", "sowie" };

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
			"ed", "erano", "eravamo", "eravate", "eri", "ero", "essendo", "fa", "fà", "facciamo", "facciano",
			"faccio", "facemmo", "facendo", "facesse", "facessero", "facessi", "facessimo", "faceste", "facesti",
			"faceva", "facevamo", "facevano", "facevate", "facevi", "facevo", "fai", "fanno", "farà", "farai",
			"faranno", "farebbe", "farebbero", "farei", "faremmo", "faremo", "fareste", "faresti", "farete", "farò",
			"fece", "fecero", "fossero", "fossimo", "foste", "fosti", "fu", "fui", "fummo", "furono", "gli", "ha",
			"hai", "hanno", "ho", "i", "il", "in", "io", "l", "la", "là", "le", "lei", "li", "lì", "lo", "loro",
			"lui", "ma", "mi", "mia", "mie", "miei", "mio", "ne", "negl", "negli", "nei", "nel", "nell", "nella",
			"nelle", "nello", "noi", "non", "nostra", "nostre", "nostri", "nostro", "o", "per", "perché", "più",
			"quale", "quanta", "quante", "quanti", "quanto", "quella", "quelle", "quelli", "quello", "questa",
			"queste", "questi", "questo", "sarà", "sarai", "saranno", "sarebbe", "sarebbero", "sarei", "saremmo",
			"saremo", "sareste", "saresti", "sarete", "sarò", "se", "sei", "si", "sì", "sia", "siamo", "siano",
			"siate", "siete", "sono", "sta", "stai", "stando", "stanno", "starà", "starai", "staranno", "starebbe",
			"starebbero", "starei", "staremmo", "staremo", "stareste", "staresti", "starete", "starò", "stava",
			"stavamo", "stavano", "stavate", "stavi", "stavo", "stemmo", "stesse", "stessero", "stessi", "stessimo",
			"steste", "stesti", "stette", "stettero", "stetti", "stia", "stiamo", "stiano", "stiate", "sto", "su",
			"sua", "sue", "sugl", "sugli", "sui", "sul", "sull", "sulla", "sulle", "sullo", "suo", "suoi", "ti", "tra",
			"tu", "tua", "tue", "tuo", "tuoi", "tutti", "tutto", "un", "una", "uno", "vi", "voi", "vostra", "vostre",
			"vostri", "vostro" };

	static {
		stopwordsMap.put("en", ENGLISH_STOP_WORDS);
		stopwordsMap.put("de", GERMAN_STOP_WORDS);
		stopwordsMap.put("es", SPANISH_STOP_WORDS);
		stopwordsMap.put("it", ITALIAN_STOP_WORDS);
		stopwordsMap.put("fr", FRENCH_STOP_WORDS);
	}

	private String language;

	private File rootFolder;

	private File indexFolder;

	private int count = 0;

	private long startDocId = 10000;

	private IndexWriter writer;

	public PopulateIndex() {
		try {
			Properties conf = new Properties();
			conf.load(this.getClass().getResourceAsStream("/conf.properties"));
			this.rootFolder = new File(conf.getProperty("files.rootFolder"));
			this.indexFolder = new File(conf.getProperty("index.indexFolder"));
			this.language = conf.getProperty("database.language");
			this.startDocId = Long.parseLong(conf.getProperty("files.startDocId"));
		} catch (IOException e) {
		}
	}

	public File getIndexFolder() {
		return indexFolder;
	}

	public void setIndexFolder(File indexFolder) {
		this.indexFolder = indexFolder;
	}

	public long getStartDocId() {
		return startDocId;
	}

	public void setStartDocId(long startDocId) {
		this.startDocId = startDocId;
	}

	public File getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(File rootFolder) {
		this.rootFolder = rootFolder;
	}

	/**
	 * Populates the full-text index
	 */
	public void populate() {
		log.fatal("Start of database population");
		count = 0;
		Locale locale = new Locale(language);
		Analyzer analyzer = new SnowballAnalyzer(locale.getDisplayName(Locale.ENGLISH), stopwordsMap.get(language));

		try {
			writer = new IndexWriter(indexFolder, analyzer, false);
			addDocuments(rootFolder, "/");
			writer.optimize();
		} catch (Throwable e) {
			log.error(e);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (Exception e) {
					log.error(e);
				}
		}
		log.fatal("End of index population");
	}

	/**
	 * Adds all documents inside the specified dir
	 * 
	 * @param dir The directory to browse
	 * @param path Path for 'path' field
	 * @throws SQLException
	 */
	private void addDocuments(File dir, String path) {
		long parentFolderId = Long.parseLong(dir.getName());
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && !files[i].getName().startsWith("doc_")) {
				// Recursive invocation
				addDocuments(files[i], path + "/" + parentFolderId);
			} else if (files[i].isDirectory() && files[i].getName().startsWith("doc_")) {
				try {
					long docId = insertDocument(files[i], path.replaceAll("//", "/"));
					if ((count % 100 == 0)&& docId>0) {
						log.info("Added index document " + docId);						
					}
				} catch (Throwable e) {
					e.printStackTrace();
					log.error(e);
				}
			}
		}
	}

	private long insertDocument(File dir, String path) throws CorruptIndexException, IOException {
		File docFile = dir.listFiles()[0];
		String filename = docFile.getName();
		String title = filename.substring(0, filename.lastIndexOf("."));
		String type = filename.substring(filename.lastIndexOf(".") + 1);
		long filesize = docFile.length();
		long id = Long.parseLong(dir.getName().substring(dir.getName().lastIndexOf("_") + 1));

		//Skip condition
		if(id<startDocId)
			return -1;
		
		Document doc = new Document();
		doc.add(new Field(FIELD_DOC_ID, String.valueOf(id), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_TITLE, title, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_SIZE, String.valueOf(filesize), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_SOURCE, "LogicalDOC", Field.Store.NO, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_SOURCE_AUTHOR, "admin", Field.Store.NO, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_SOURCE_TYPE, "type", Field.Store.NO, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_COVERAGE, "test", Field.Store.NO, Field.Index.TOKENIZED));
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		doc.add(new Field(FIELD_SOURCE_DATE, df.format(docFile.lastModified()), Field.Store.YES,
				Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_DATE, df.format(docFile.lastModified()), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_TYPE, type, Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_PATH, path, Field.Store.YES, Field.Index.UN_TOKENIZED));

		String content = parse(docFile);
		doc.add(new Field(FIELD_CONTENT, content, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_LENGTH, String.valueOf(content.length()), Field.Store.YES, Field.Index.NO));

		int summarysize = Math.min(content.length(), 500);
		String summary = content.substring(0, summarysize);
		doc.add(new Field(FIELD_SUMMARY, summary, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_KEYWORDS, extractKeywords(5, content), Field.Store.YES, Field.Index.TOKENIZED));
		
		writer.addDocument(doc);
		
		count++;
		return id;
	}

	/**
	 * This method extracts a specified number of keywords and appends them to a
	 * String.
	 * <p>
	 * 
	 * <b>Note:</b> This is a quick-and-dirty implementation that basically
	 * tokenizes the text and reports the first found words
	 */
	private String extractKeywords(int count, String text) {
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(text, " ", false);
		int i = 0;
		while (st.hasMoreTokens() && i < count) {
			String token = st.nextToken();
			if (token.length() > 5) {
				sb.append(token);
				i++;
				if (st.hasMoreTokens())
					sb.append(",");
			}
		}
		return sb.toString();
	}

	/**
	 * Reads a file in a string
	 */
	protected String parse(File file) {
		String content = "";
		try {
			DefaultEditorKit editorkit = new DefaultEditorKit();
			JEditorPane editor = new JEditorPane();
			editor.setEditorKit(editorkit);

			FileInputStream fis = new FileInputStream(file);
			editorkit.read(fis, editor.getDocument(), 0);

			content = editor.getDocument().getText(0, editor.getDocument().getLength());
			fis.close();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return content;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
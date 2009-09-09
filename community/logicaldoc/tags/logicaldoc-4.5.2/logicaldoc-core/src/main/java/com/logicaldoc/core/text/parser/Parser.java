package com.logicaldoc.core.text.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

/**
 * @author Michael Scholz
 */
public interface Parser {
	
	public String getVersion();

	public String getContent();

	public String getAuthor();

	public String getSourceDate();

	public String getTags();

	public String getTitle();
	
	public void parse(File file);
	
	public void parse(File file, Locale locale);
	
    /**
     * Returns a reader for the text content of the given binary document.
     * The content type and character encoding (if available and applicable)
     * are given as arguments. The given content type is guaranteed to be
     * one of the types reported by {@link #getContentTypes()} unless the
     * implementation explicitly permits other content types.
     * <p>
     * The implementation can choose either to read and parse the given
     * document immediately or to return a reader that does it incrementally.
     * The only constraint is that the implementation must close the given
     * stream latest when the returned reader is closed. The caller on the
     * other hand is responsible for closing the returned reader.
     * <p>
     * The implemenation should only throw an exception on transient
     * errors, i.e. when it can expect to be able to successfully extract
     * the text content of the same binary at another time. An effort
     * should be made to recover from syntax errors and other similar problems.
     * <p>
     * This method should be thread-safe, i.e. it is possible that this
     * method is invoked simultaneously by different threads to extract the
     * text content of different documents. On the other hand the returned
     * reader does not need to be thread-safe.
     *
     * @param stream   binary document from which to extract text
     * @param type     MIME type of the given document, lower case
     * @param encoding the character encoding of the binary data,
     *                 or <code>null</code> if not available
     * @return reader for the extracted text content
     * @throws IOException on transient errors
     */
    public Reader extractText(InputStream stream, String type, String encoding)
        throws IOException;
    
    public String readText(Reader reader, String encoding) throws IOException;
}
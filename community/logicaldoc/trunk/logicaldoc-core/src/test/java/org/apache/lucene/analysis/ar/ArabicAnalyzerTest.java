package org.apache.lucene.analysis.ar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class ArabicAnalyzerTest {

	@Test
	public void testTokenStream() {

		BufferedReader IN = null;
		BufferedWriter BW = null;
		String inputFile = "target/test-classes/Arabic/UTF-8.txt";
		String outputFile = "C:/tmp/testdocs/UTF-8.out.txt";
		String inputEncoding = "UTF-8";
		String outputEncoding = "UTF-8";

		int counter = 0;
		try {
			IN = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), inputEncoding));
			// FileOutputStream out = new FileOutputStream(outputFile);
			// BW = new BufferedWriter(new OutputStreamWriter(out,
			// outputEncoding));
			ArabicAnalyzer arabicAnalyzer = new ArabicAnalyzer(Version.LUCENE_30);

			TokenStream ts = arabicAnalyzer.tokenStream(null, IN);

			while (ts.incrementToken()) {
				System.out.println(ts.toString());
				// BW.write(ts.toString());
				// BW.newLine();
				counter++;
			}
		} catch (IOException e) {
			throw new RuntimeException("Problem : " + e.getMessage());
		} finally {
			try {
				if (IN != null)
					IN.close();
			} catch (IOException e) {
			}
			// try {
			// if (BW != null)
			// BW.close();
			// } catch (IOException e) {}
		}

		System.err.println(counter);
		Assert.assertEquals(371, counter);
	}

	@Test
	public void testTokenStream2() {

		BufferedReader IN = null;
		BufferedWriter BW = null;
		String inputFile = "target/test-classes/Arabic/cp1256.txt";
		String outputFile = "C:/tmp/testdocs/cp1256.out.txt";
		String inputEncoding = "Cp1256";
		String outputEncoding = "UTF-8";

		int counter = 0;
		try {
			IN = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), inputEncoding));
			// FileOutputStream out = new FileOutputStream(outputFile);
			// BW = new BufferedWriter(new OutputStreamWriter(out,
			// outputEncoding));
			ArabicAnalyzer arabicAnalyzer = new ArabicAnalyzer(Version.LUCENE_30);

			TokenStream ts = arabicAnalyzer.tokenStream(null, IN);

			while (ts.incrementToken()) {
				System.out.println(ts.toString());
				// BW.write(ts.toString());
				// BW.newLine();
				counter++;
			}
		} catch (IOException e) {
			throw new RuntimeException("Problem : " + e.getMessage());
		} finally {
			try {
				if (IN != null)
					IN.close();
			} catch (IOException e) {
			}
			// try {
			// if (BW != null)
			// BW.close();
			// } catch (IOException e) {}
		}

		System.err.println(counter);
		Assert.assertEquals(577, counter);
	}

	@Test
	public void testTokenStream3() {

		BufferedReader IN = null;
		BufferedWriter BW = null;
		String inputFile = "target/test-classes/Arabic/ktAb.txt";
		String outputFile = "C:/tmp/testdocs/ktAb.out.txt";
		String inputEncoding = "Cp1256";
		String outputEncoding = "UTF-8";

		int counter = 0;
		try {
			IN = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), inputEncoding));
			// FileOutputStream out = new FileOutputStream(outputFile);
			// BW = new BufferedWriter(new OutputStreamWriter(out,
			// outputEncoding));
			ArabicAnalyzer arabicAnalyzer = new ArabicAnalyzer(Version.LUCENE_30);

			TokenStream ts = arabicAnalyzer.tokenStream(null, IN);

			while (ts.incrementToken()) {
				System.out.println(ts.toString());
				// BW.write(ts.toString());
				// BW.newLine();
				counter++;
			}
		} catch (IOException e) {
			throw new RuntimeException("Problem : " + e.getMessage());
		} finally {
			try {
				if (IN != null)
					IN.close();
			} catch (IOException e) {
			}
			// try {
			// if (BW != null)
			// BW.close();
			// } catch (IOException e) {}
		}

		System.err.println(counter);
		Assert.assertEquals(1, counter);
	}

	@Test
	public void testTokenStream4() {

		BufferedReader IN = null;
		BufferedWriter BW = null;
		String inputFile = "target/test-classes/Arabic/tktbn.txt";
		String outputFile = "C:/tmp/testdocs/tktbn.out.txt";
		String inputEncoding = "Cp1256";
		String outputEncoding = "UTF-8";

		int counter = 0;
		try {
			IN = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), inputEncoding));
			// FileOutputStream out = new FileOutputStream(outputFile);
			// BW = new BufferedWriter(new OutputStreamWriter(out,
			// outputEncoding));
			ArabicAnalyzer arabicAnalyzer = new ArabicAnalyzer(Version.LUCENE_30);

			TokenStream ts = arabicAnalyzer.tokenStream(null, IN);

			while (ts.incrementToken()) {
				System.out.println(ts.toString());
				// BW.write(ts.toString());
				// BW.newLine();
				counter++;
			}
		} catch (IOException e) {
			throw new RuntimeException("Problem : " + e.getMessage());
		} finally {
			try {
				if (IN != null)
					IN.close();
			} catch (IOException e) {
			}

		}

		System.err.println(counter);
		Assert.assertEquals(1, counter);
	}

}

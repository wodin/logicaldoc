/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.logicaldoc.core.text.parser;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;

/**
 * Helper class for HTML parsing
 */
public class HTMLSAXParser extends AbstractSAXParser {

    private StringBuffer buffer;

    public HTMLSAXParser() {
         super(new HTMLConfiguration());
    }
    
    public HTMLSAXParser(XMLParserConfiguration parserConfig) { 
        super(parserConfig);
    }

    public void startDocument(XMLLocator arg0,
                              String arg1,
                              NamespaceContext arg2,
                              Augmentations arg3) throws XNIException {
        super.startDocument(arg0, arg1, arg2, arg3);
        buffer = new StringBuffer();
    }

    public void characters(XMLString xmlString, Augmentations augmentations)
            throws XNIException {
        super.characters(xmlString, augmentations);
        buffer.append(xmlString.toString());
    }

    /**
     * Returns parsed content
     *
     * @return String Parsed content
     */
    public String getContents() {
		String tmp = buffer.toString();
		if (tmp != null && tmp.length() > 1) {
			tmp = tmp.replaceAll("\\p{Blank}+", " ");
			tmp = tmp.replaceAll("\\s+", " ");
		}
		return tmp;
	}
}

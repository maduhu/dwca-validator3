/*
 * Copyright 2009 GBIF. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.gbif.dwca.model.factory;

import org.gbif.dwca.model.Vocabulary;
import org.gbif.dwca.model.VocabularyConcept;
import org.gbif.dwca.model.VocabularyTerm;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import com.google.inject.Inject;
import org.apache.commons.digester.Digester;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Building from XML definitions
 */
public class VocabularyFactory {
  public static final String VOCABULARY_NAMESPACE = "http://rs.gbif.org/thesaurus/";
  protected static Logger log = LoggerFactory.getLogger(VocabularyFactory.class);
  private HttpClient httpClient;
  private SAXParserFactory saxf;

  @Inject
  public VocabularyFactory(HttpClient httpClient, SAXParserFactory saxf) {
    super();
    this.saxf = saxf;
    this.httpClient = httpClient;
  }

  /**
   * Builds a Vocabulary from the supplied input stream
   *
   * @param is For the XML
   * @return The extension
   * @throws SAXException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public Vocabulary build(InputStream is) throws IOException, SAXException, ParserConfigurationException {
    Digester digester = new Digester(saxf.newSAXParser());
    digester.setNamespaceAware(true);
    digester.setXIncludeAware(false);
    digester.setRuleNamespaceURI(VOCABULARY_NAMESPACE);

    Vocabulary tv = new Vocabulary();
    digester.push(tv);

    // build the thesaurus
    digester.addCallMethod("*/thesaurus", "setTitle", 1);
    digester.addRule("*/thesaurus", new CallParamNoNSRule(0, "title"));

    digester.addCallMethod("*/thesaurus", "setDescription", 1);
    digester.addRule("*/thesaurus", new CallParamNoNSRule(0, "description"));

    digester.addCallMethod("*/thesaurus", "setLink", 1);
    digester.addRule("*/thesaurus", new CallParamNoNSRule(0, "relation"));

    digester.addCallMethod("*/thesaurus", "setUri", 1);
    digester.addRule("*/thesaurus", new CallParamNoNSRule(0, "URI"));

    // build the concept
    digester.addObjectCreate("*/concept", VocabularyConcept.class);

    digester.addCallMethod("*/concept", "setLink", 1);
    digester.addRule("*/concept", new CallParamNoNSRule(0, "relation"));

    digester.addCallMethod("*/concept", "setDescription", 1);
    digester.addRule("*/concept", new CallParamNoNSRule(0, "description"));

    digester.addCallMethod("*/concept", "setUri", 1);
    digester.addRule("*/concept", new CallParamNoNSRule(0, "URI"));

    digester.addCallMethod("*/concept", "setIdentifier", 1);
    digester.addRule("*/concept", new CallParamNoNSRule(0, "identifier"));

    // build the terms
    digester.addObjectCreate("*/preferred/term", VocabularyTerm.class);
    VocabularyTerm t = new VocabularyTerm();

    digester.addCallMethod("*/preferred/term", "setLang", 1);
    digester.addRule("*/preferred/term", new CallParamNoNSRule(0, "lang"));

    digester.addCallMethod("*/preferred/term", "setTitle", 1);
    digester.addRule("*/preferred/term", new CallParamNoNSRule(0, "title"));

    digester.addSetNext("*/preferred/term", "addPreferredTerm");

    // build alternative terms
    digester.addObjectCreate("*/alternative/term", VocabularyTerm.class);
    VocabularyTerm talt = new VocabularyTerm();

    digester.addCallMethod("*/alternative/term", "setLang", 1);
    digester.addRule("*/alternative/term", new CallParamNoNSRule(0, "lang"));

    digester.addCallMethod("*/alternative/term", "setTitle", 1);
    digester.addRule("*/alternative/term", new CallParamNoNSRule(0, "title"));

    digester.addSetNext("*/alternative/term", "addAlternativeTerm");

    // add concept
    digester.addSetNext("*/concept", "addConcept");

    digester.parse(is);
    return tv;
  }

}

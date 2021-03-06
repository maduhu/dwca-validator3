/*
 * Copyright 2009 GBIF. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.gbif.dwca.model.factory;

import org.gbif.dwca.model.Extension;
import org.gbif.dwca.model.ExtensionProperty;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.commons.digester.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

/**
 * Building from XML definitions. Because an extension can reference thesauri, this is a 2 pass parsing process.
 * 
 * <pre>
 * - the first pass looks for thesauri, that need built
 * - these are built, and then a map<thesaurusURL, Vocabulary> is put onto the root of the second parse stack
 * - this map is then used to get the Vocabulary in the second pass of parsing
 * </pre>
 */
@Singleton
public class ExtensionFactory {
  protected static Logger log = LoggerFactory.getLogger(ExtensionFactory.class);
  public static final String EXTENSION_NAMESPACE = "http://rs.gbif.org/extension/";
  private ThesaurusHandlingRule thesaurusRule;
  private SAXParserFactory saxf;

  @Inject
  public ExtensionFactory(ThesaurusHandlingRule thesaurusRule, SAXParserFactory factory) {
    super();
    this.thesaurusRule = thesaurusRule;
    this.saxf = factory;
  }

  /**
   * Builds an extension from the supplied input stream
   * 
   * @param is For the XML
   * @return The extension
   * @throws SAXException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public Extension build(InputStream is, URL url, boolean dev) throws IOException, SAXException, ParserConfigurationException {

    // in order to deal with arbitrary namespace prefixes we need to parse namespace aware!
    Digester digester = new Digester(saxf.newSAXParser());
    digester.setRuleNamespaceURI(EXTENSION_NAMESPACE);

    Extension e = new Extension(url, dev);
    digester.push(e);

    digester.addCallMethod("*/extension", "setTitle", 1);
    digester.addRule("*/extension", new CallParamNoNSRule(0, "title"));

    digester.addCallMethod("*/extension", "setName", 1);
    digester.addCallParam("*/extension", 0, "name");

    digester.addCallMethod("*/extension", "setNamespace", 1);
    digester.addCallParam("*/extension", 0, "namespace");

    digester.addCallMethod("*/extension", "setRowType", 1);
    digester.addCallParam("*/extension", 0, "rowType");

    digester.addCallMethod("*/extension", "setLink", 1);
    digester.addRule("*/extension", new CallParamNoNSRule(0, "relation"));

    digester.addCallMethod("*/extension", "setDescription", 1);
    digester.addRule("*/extension", new CallParamNoNSRule(0, "description"));

    digester.addCallMethod("*/extension", "setSubject", 1);
    digester.addRule("*/extension", new CallParamNoNSRule(0, "subject"));

    // build the properties
    digester.addObjectCreate("*/property", ExtensionProperty.class);

    digester.addCallMethod("*/property", "setQualname", 1);
    digester.addCallParam("*/property", 0, "qualName");

    digester.addCallMethod("*/property", "setName", 1);
    digester.addCallParam("*/property", 0, "name");

    digester.addCallMethod("*/property", "setNamespace", 1);
    digester.addCallParam("*/property", 0, "namespace");

    digester.addCallMethod("*/property", "setGroup", 1);
    digester.addCallParam("*/property", 0, "group");

    digester.addCallMethod("*/property", "setType", 1);
    digester.addCallParam("*/property", 0, "type");

    digester.addCallMethod("*/property", "setRequired", 1);
    digester.addCallParam("*/property", 0, "required");

    digester.addCallMethod("*/property", "setLink", 1);
    digester.addRule("*/property", new CallParamNoNSRule(0, "relation"));

    digester.addCallMethod("*/property", "setDescription", 1);
    digester.addRule("*/property", new CallParamNoNSRule(0, "description"));

    digester.addCallMethod("*/property", "setExamples", 1);
    digester.addCallParam("*/property", 0, "examples");

    digester.addCallMethod("*/property", "setColumnLength", 1);
    digester.addCallParam("*/property", 0, "columnLength");

    // This is a special rule that will use the url2ThesaurusMap
    // to set the Vocabulary based on the attribute "thesaurus"
    digester.addRule("*/property", thesaurusRule);

    digester.addSetNext("*/property", "addProperty");

    digester.parse(is);
    return e;
  }

}

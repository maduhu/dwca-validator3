package org.gbif.dwca.model;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.google.common.base.Objects.equal;

/**
 * A Darwin Core extension definition
 *
 * @author markus
 *
 */
public class Extension implements Comparable<Extension>{
  private String title; // human title
  private String name; // table, file & xml tag naming. no whitespace allowed
  private URL url;
  private Term rowType;
  private String subject;
  private String description;
  private String namespace;
  private URL link; // to documentation
  private boolean installed;
  private List<ExtensionProperty> properties = new ArrayList<ExtensionProperty>();
  private boolean core = false;
  private boolean dev = false;
  private Date modified = new Date();

  public Extension(URL url, boolean dev) {
    super();
    this.url = url;
    this.dev = dev;
  }

  public void addProperty(ExtensionProperty property) {
    property.setExtension(this);
    properties.add(property);
  }

  /**
   * @see java.lang.Comparable#compareTo(Object)
   */
  public int compareTo(Extension object) {
    return new CompareToBuilder()
      .append(this.rowType.qualifiedName(), object.rowType.qualifiedName())
      .append(this.url.toString(), object.url.toString())
      .toComparison();
  }

  /**
   * @see java.lang.Object#equals(Object)
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Extension)) {
      return false;
    }
    Extension o = (Extension) other;
    return equal(rowType, o.rowType) && equal(url.toString(), o.url.toString());
  }

  public String getDescription() {
    return description;
  }

  public URL getLink() {
    return link;
  }

  public Date getModified() {
    return modified;
  }

  public String getName() {
    return name;
  }

  public String getNamespace() {
    return namespace;
  }

  public List<ExtensionProperty> getProperties() {
    return properties;
  }

  public ExtensionProperty getProperty(Term term) {
    return getProperty(term.qualifiedName());
  }

  public ExtensionProperty getProperty(String term) {
    if (term == null) {
      return null;
    }
    for (ExtensionProperty p : properties) {
      if (term.equalsIgnoreCase(p.getQualname())) {
        return p;
      }
    }
    return null;
  }

  public Term getRowType() {
    return rowType;
  }

  public String getSubject() {
    return subject;
  }

  public String getTitle() {
    return title;
  }

  public URL getUrl() {
    return url;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(rowType, url);
  }

  public boolean hasProperty(Term term) {
    return getProperty(term) != null;
  }

  public boolean hasProperty(String term) {
    return getProperty(term) != null;
  }

  public boolean isCore() {
    return core;
  }

  public boolean isDev() {
    return dev;
  }

  public boolean isInstalled() {
    return installed;
  }

  public void setCore(boolean core) {
    this.core = core;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setInstalled(boolean installed) {
    this.installed = installed;
  }

  public void setLink(String link) {
    URL url;
    try {
      url = new URL(link);
      this.link = url;
    } catch (MalformedURLException e) {
    }
  }

  public void setLink(URL link) {
    this.link = link;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public void setName(String name) {
    this.name = name.replaceAll("\\s", "_");
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public void setProperties(List<ExtensionProperty> properties) {
    this.properties = properties;
  }

  public void setRowType(String rowType) {
    setRowType(TermFactory.instance().findTerm(rowType));
  }

  public void setRowType(Term rowType) {
    this.rowType = rowType;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return new ToStringBuilder(this).append("name", this.name).append("rowType", this.rowType).toString();
  }

}

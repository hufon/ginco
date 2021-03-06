/**
 * Copyright or © or Copr. Ministère Français chargé de la Culture
 * et de la Communication (2013)
 * <p/>
 * contact.gincoculture_at_gouv.fr
 * <p/>
 * This software is a computer program whose purpose is to provide a thesaurus
 * management solution.
 * <p/>
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * <p/>
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited liability.
 * <p/>
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 * <p/>
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.mcc.ginco.beans;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Bean represents <b>thesaurus</b> table, main container containing
 * all other beans.
 */
@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
@SuppressWarnings("serial")
public class Thesaurus implements Serializable, IAuditableBean {
    private String identifier;
    private String contributor;
    private String coverage;
    private Date date;
    private String description;
    private String publisher;
    private String relation;
    private String rights;
    private String source;
    private String subject;
    private String title;
    private Date created;
    private Boolean defaultTopConcept;
    private Boolean archived;
    private ThesaurusType type;
    private ThesaurusOrganization creator;
    private Boolean polyHierarchical;
    
    private Set<Language> lang = new HashSet<Language>();
    private Set<ThesaurusFormat> format = new HashSet<ThesaurusFormat>();
    private Set<ThesaurusVersionHistory> versions;

    public Thesaurus() {
    }   

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getCoverage() {
        return coverage;
    }

    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

	public ThesaurusType getType() {
		return type;
	}

	public void setType(ThesaurusType type) {
		this.type = type;
	}

	public ThesaurusOrganization getCreator() {
		return creator;
	}

	public void setCreator(ThesaurusOrganization creator) {
		this.creator = creator;
	}		

	@JsonIgnore
	public Set<ThesaurusVersionHistory> getVersions() {
		return versions;
	}

	public void setVersions(Set<ThesaurusVersionHistory> versions) {
		this.versions = versions;
	}

    /**
     * @return All languages available for this Thesaurus
     */
	 @NotAudited
    @JsonIgnore
    public Set<Language> getLang() {
        return lang;
    }

    /**
     * @param lang
     */
    public void setLang(Set<Language> lang) {
        this.lang = lang;
    }
    
    /**
     * @param lang
     */
    public void addLang(Language lang) {
        this.lang.add(lang);
    }

	public Boolean isDefaultTopConcept() {
		return defaultTopConcept;
	}

	public void setDefaultTopConcept(Boolean defaultTopConcept) {
		this.defaultTopConcept = defaultTopConcept;
	}

	@Override
	public String getThesaurusId() {		
		return identifier;
	}

    public Boolean isArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean isPolyHierarchical() {
        return polyHierarchical;
    }

    public void setPolyHierarchical(Boolean polyHierarchical) {
        this.polyHierarchical = polyHierarchical;
    }
    
    public Set<ThesaurusFormat> getFormat() {
		return format;
	}

	public void setFormat(Set<ThesaurusFormat> format) {
		this.format = format;
	}
	
	public void addFormat(ThesaurusFormat format) {
        this.format.add(format);
    }
}
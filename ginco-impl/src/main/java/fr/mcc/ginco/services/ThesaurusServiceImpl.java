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
package fr.mcc.ginco.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.mcc.ginco.beans.Language;
import fr.mcc.ginco.beans.Thesaurus;
import fr.mcc.ginco.beans.ThesaurusOrganization;
import fr.mcc.ginco.beans.ThesaurusVersionHistory;
import fr.mcc.ginco.dao.IGenericDAO.SortingTypes;
import fr.mcc.ginco.dao.IThesaurusDAO;
import fr.mcc.ginco.dao.IThesaurusVersionHistoryDAO;
import fr.mcc.ginco.exceptions.BusinessException;
import fr.mcc.ginco.exceptions.TechnicalException;
import fr.mcc.ginco.exports.IGincoThesaurusExportService;
import fr.mcc.ginco.exports.ISKOSExportService;
import fr.mcc.ginco.helpers.ThesaurusHelper;
import fr.mcc.ginco.utils.DateUtil;
import fr.mcc.ginco.utils.LabelUtil;
import fr.mcc.ginco.utils.LanguageComparator;

/**
 * Implementation of the thesaurus service Contains methods relatives to the
 * Thesaurus object
 */
@Transactional(readOnly=true, rollbackFor = BusinessException.class)
@Service("thesaurusService")
public class ThesaurusServiceImpl implements IThesaurusService {

	@Value("${ginco.default.language}")
	private String defaultLang;

    @Value("${publish.path}")
    private String publishPath;

    @Value("${archive.path}")
    private String archivePath;

	@Inject
	@Named("thesaurusDAO")
	private IThesaurusDAO thesaurusDAO;

    @Inject
    @Named("skosExportService")
    private ISKOSExportService exportService;

	@Inject
	@Named("thesaurusVersionHistoryDAO")
	private IThesaurusVersionHistoryDAO thesaurusVersionHistoryDAO;

	@Inject
	@Named("thesaurusHelper")
	private ThesaurusHelper	thesaurusHelper;

    @Inject
    @Named("gincoThesaurusExportService")
    private IGincoThesaurusExportService gincoThesaurusExportService;


	/*
	 * (non-Javadoc)
	 *
	 * @see fr.mcc.ginco.IThesaurusService#getThesaurusById(java.lang.String)
	 */
    @Override
	public Thesaurus getThesaurusById(String id) {
		return thesaurusDAO.getById(id);
	}

    @Override
    public Thesaurus getDefaultThesaurus(){
    	Thesaurus defaultThesaurus = new Thesaurus();
    	defaultThesaurus.setTitle(LabelUtil.getDefaultLabel("default.thesaurus.title"));

    	ThesaurusOrganization defaultOrganisation = new ThesaurusOrganization();
    	defaultOrganisation.setHomepage(LabelUtil.getDefaultLabel("default.thesaurus.creator.homepage"));
    	defaultOrganisation.setEmail(LabelUtil.getDefaultLabel("default.thesaurus.creator.email"));
    	defaultOrganisation.setName(LabelUtil.getDefaultLabel("default.thesaurus.creator.name"));
    	defaultThesaurus.setCreator(defaultOrganisation);

    	defaultThesaurus.setContributor(LabelUtil.getDefaultLabel("default.thesaurus.creator.contributor"));
    	defaultThesaurus.setRights(LabelUtil.getDefaultLabel("default.thesaurus.rights"));
    	defaultThesaurus.setDescription(LabelUtil.getDefaultLabel("default.thesaurus.description"));
    	defaultThesaurus.setCoverage(LabelUtil.getDefaultLabel("default.thesaurus.coverage"));
    	defaultThesaurus.setSubject(LabelUtil.getDefaultLabel("default.thesaurus.subject"));
    	defaultThesaurus.setPublisher(LabelUtil.getDefaultLabel("default.thesaurus.publisher"));

    	return defaultThesaurus;
    }

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.mcc.ginco.IThesaurusService#getThesaurusList()
	 */
	@Override
	public List<Thesaurus> getThesaurusList() {
		return thesaurusDAO.findAll("title", SortingTypes.asc);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fr.mcc.ginco.IThesaurusService#updateThesaurus(fr.mcc.ginco.beans.Thesaurus
	 * , fr.mcc.ginco.beans.users.IUser)
	 */
	@Transactional(readOnly=false)
	@Override
	public Thesaurus updateThesaurus(Thesaurus object) throws BusinessException {
		 Thesaurus result = thesaurusDAO.update(object);

		 //We get the versions of the thesaurus we are creating/updating
		 //If no version, we initialize one with status PROJECT
		 List<ThesaurusVersionHistory> versionsOfCurrentThesaurus = thesaurusVersionHistoryDAO.findVersionsByThesaurusId(result.getIdentifier());
		 if (versionsOfCurrentThesaurus == null || versionsOfCurrentThesaurus.isEmpty()) {
			ThesaurusVersionHistory defaultVersion = thesaurusHelper.buildDefaultVersion(result);
			Set<ThesaurusVersionHistory> versions = new HashSet<ThesaurusVersionHistory>();
			versions.add(defaultVersion);
			thesaurusVersionHistoryDAO.update(defaultVersion);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fr.mcc.ginco.IThesaurusService#getThesaurusLanguages(java.lang.String)
	 */
	@Override
	public List<Language> getThesaurusLanguages(String thesaurusId)
			throws BusinessException {
		Thesaurus th = thesaurusDAO.getById(thesaurusId);
		if (th == null) {
			throw new BusinessException("Invalid thesaurusId : " + thesaurusId, "invalid-thesaurus-id");
		}
		Set<Language> languages = th.getLang();
		List<Language> orderedLangs = new ArrayList<Language>();
		orderedLangs.addAll(languages);
		Collections.sort(orderedLangs, new LanguageComparator(defaultLang));
		Collections.reverse(orderedLangs);
		return orderedLangs;
	}

    @Transactional(readOnly=false)
    @Override
    public Thesaurus archiveThesaurus(Thesaurus thesaurus)
            throws BusinessException, TechnicalException {

        String fileContent = gincoThesaurusExportService.getThesaurusExport(thesaurus);
        File ready = new File(archivePath + thesaurus.getTitle().replaceAll("[^a-zA-Z0-9\\._]+", "_") + "_"
                + DateUtil.toString(DateUtil.nowDate()).replaceAll(" ", "_")
                + ".xml");
        try {
            File checkPath = new File(archivePath);
            if(!checkPath.exists()) {
                FileUtils.forceMkdir(checkPath);
            }
            FileWriter writer = new FileWriter(ready);
            writer.write(fileContent);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new TechnicalException("Error writing file to path : " + publishPath, e);
        }

        thesaurus.setArchived(Boolean.TRUE);
        return thesaurusDAO.update(thesaurus);
    }

    @Override
    public void publishThesaurus(Thesaurus object) throws BusinessException {
        File export = exportService.getSKOSExport(object);
        File ready = new File(publishPath + object.getTitle() + " "
                + DateUtil.toString(DateUtil.nowDate())
                + ".rdf");
        try {
            File checkPath = new File(publishPath);
            if(!checkPath.exists()) {
                FileUtils.forceMkdir(checkPath);
            }
            FileUtils.copyFile(export, ready);
        } catch (IOException e) {
            throw new TechnicalException("Error copying file to path : " + publishPath, e);
        }
    }

    @Transactional(readOnly=false)
    @Override
    public Thesaurus destroyThesaurus(Thesaurus object) throws BusinessException {
        try {
            return thesaurusDAO.delete(object);
        } catch (HibernateException ex) {
            throw new BusinessException("Error deleting thesaurus!","error-deleting-thesaurus",ex);
        }
    }



}
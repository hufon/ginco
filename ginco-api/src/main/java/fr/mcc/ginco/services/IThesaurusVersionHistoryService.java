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
 * requirements in conditions enabling the security of their systemsand/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 * <p/>
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.mcc.ginco.services;

import fr.mcc.ginco.beans.Thesaurus;
import fr.mcc.ginco.beans.ThesaurusVersionHistory;
import fr.mcc.ginco.exceptions.BusinessException;

import java.util.List;

/**
 * Service used to work with {@link ThesaurusVersionHistory} objects, contains basic
 * methods exposed to client part.
 *
 * @see fr.mcc.ginco.beans.ThesaurusVersionHistory
 */
public interface IThesaurusVersionHistoryService {
	
	/**
	 * @param thesaurusId
	 * @return A list of {@ThesaurusVersionHistory}
	 */
	List<ThesaurusVersionHistory> getVersionsByThesaurusId(String thesaurusId);
	
	/**
     * Get a single ThesaurusVersionHistory by its id
     *
     * @param id to search
     * @return {@code null} if not found
     */
	ThesaurusVersionHistory getThesaurusVersionHistoryById(String id) throws BusinessException;
	
	/**
	 * Create or update a new ThesaurusVersionHistory
	 * @param {@ThesaurusVersionHistory}
	 * @return The created or updated {@ThesaurusVersionHistory} object
	 */
	ThesaurusVersionHistory createOrUpdateVersion(ThesaurusVersionHistory version);

    /**
     * Creates a new version of Thesaurus with status PUBLISHED.
     * @param thesaurus
     * @return
     */
    ThesaurusVersionHistory publishThesaurus(Thesaurus thesaurus, String userId);

    /**
     * Indicates if al least one published version exists.
     * @param thesaurus
     * @return
     */
    Boolean hasPublishedVersion(Thesaurus thesaurus);
}
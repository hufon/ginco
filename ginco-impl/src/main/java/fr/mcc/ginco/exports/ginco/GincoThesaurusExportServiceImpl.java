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
package fr.mcc.ginco.exports.ginco;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.mcc.ginco.beans.Alignment;
import fr.mcc.ginco.beans.ConceptHierarchicalRelationship;
import fr.mcc.ginco.beans.NodeLabel;
import fr.mcc.ginco.beans.Note;
import fr.mcc.ginco.beans.SplitNonPreferredTerm;
import fr.mcc.ginco.beans.Thesaurus;
import fr.mcc.ginco.beans.ThesaurusArray;
import fr.mcc.ginco.beans.ThesaurusConcept;
import fr.mcc.ginco.beans.ThesaurusConceptGroup;
import fr.mcc.ginco.beans.ThesaurusConceptGroupLabel;
import fr.mcc.ginco.beans.ThesaurusTerm;
import fr.mcc.ginco.exceptions.TechnicalException;
import fr.mcc.ginco.exports.IGincoExportServiceUtil;
import fr.mcc.ginco.exports.IGincoThesaurusExportService;
import fr.mcc.ginco.exports.result.bean.GincoExportedThesaurus;
import fr.mcc.ginco.exports.result.bean.JaxbList;
import fr.mcc.ginco.services.ICustomConceptAttributeTypeService;
import fr.mcc.ginco.services.ICustomTermAttributeTypeService;
import fr.mcc.ginco.services.INodeLabelService;
import fr.mcc.ginco.services.ISplitNonPreferredTermService;
import fr.mcc.ginco.services.IThesaurusArrayService;
import fr.mcc.ginco.services.IThesaurusConceptGroupLabelService;
import fr.mcc.ginco.services.IThesaurusConceptGroupService;
import fr.mcc.ginco.services.IThesaurusConceptService;
import fr.mcc.ginco.services.IThesaurusTermService;
import fr.mcc.ginco.services.IThesaurusVersionHistoryService;

@Transactional(readOnly = true)
@Service("gincoThesaurusExportService")
public class GincoThesaurusExportServiceImpl implements
		IGincoThesaurusExportService {	

	@Inject
	@Named("thesaurusConceptService")
	private IThesaurusConceptService thesaurusConceptService;
	
	@Inject
	@Named("customTermAttributeTypeService")
	private ICustomTermAttributeTypeService customTermAttributeTypeService;
	
	@Inject
	@Named("customConceptAttributeTypeService")
	private ICustomConceptAttributeTypeService customConceptAttributeTypeService;

	@Inject
	@Named("gincoExportServiceUtil")
	private IGincoExportServiceUtil gincoExportServiceUtil;

	@Inject
	@Named("thesaurusConceptGroupLabelService")
	private IThesaurusConceptGroupLabelService thesaurusConceptGroupLabelService;

	@Inject
	@Named("thesaurusArrayService")
	private IThesaurusArrayService thesaurusArrayService;

	@Inject
	@Named("nodeLabelService")
	private INodeLabelService nodeLabelService;

	@Inject
	@Named("thesaurusConceptGroupService")
	private IThesaurusConceptGroupService thesaurusConceptGroupService;

	@Inject
	@Named("thesaurusTermService")
	private IThesaurusTermService thesaurusTermService;

	@Inject
	@Named("splitNonPreferredTermService")
	private ISplitNonPreferredTermService splitNonPreferredTermService;

	@Inject
	@Named("thesaurusVersionHistoryService")
	private IThesaurusVersionHistoryService thesaurusVersionHistoryService;

	@Inject
	@Named("gincoConceptExporter")
	private GincoConceptExporter gincoConceptExporter;

	@Inject
	@Named("gincoTermExporter")
	private GincoTermExporter gincoTermExporter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.mcc.ginco.exports.IGincoExportService#getThesaurusExport(fr.mcc.ginco
	 * .beans.Thesaurus)
	 */
	@Override
	public String getThesaurusExport(Thesaurus thesaurus)
			throws TechnicalException {
		GincoExportedThesaurus thesaurusToExport = new GincoExportedThesaurus();
		String thesaurusId = thesaurus.getIdentifier();

		// ---Exporting the thesaurus
		thesaurusToExport.setThesaurus(thesaurus);

		// Exporting the thesaurus' versions
		thesaurusToExport.setThesaurusVersions(thesaurusVersionHistoryService
				.getVersionsByThesaurusId(thesaurusId));

		// Exporting the thesaurus' concepts
		thesaurusToExport.setConcepts(thesaurusConceptService
				.getConceptsByThesaurusId(null, thesaurusId, null, false));

		// Exporting sandboxed terms
		List<ThesaurusTerm> sandboxedTerms = thesaurusTermService
				.getPaginatedThesaurusSandoxedTermsList(0, thesaurusTermService
						.getSandboxedTermsCount(thesaurusId).intValue(),
						thesaurusId);
		for (ThesaurusTerm thesaurusTerm : sandboxedTerms) {
			thesaurusToExport.getTerms().add(thesaurusTerm);
		}

		// ---Exporting the terms and the concepts of the thesaurus
		List<ThesaurusConcept> concepts = thesaurusConceptService
				.getConceptsByThesaurusId(null, thesaurusId, null, false);
		for (ThesaurusConcept thesaurusConcept : concepts) {

			// Exporting terms of the concepts
			List<ThesaurusTerm> terms = thesaurusTermService
					.getTermsByConceptId(thesaurusConcept.getIdentifier());
			for (ThesaurusTerm thesaurusTerm : terms) {
				thesaurusToExport.getTerms().add(thesaurusTerm);
			}

			// Exporting term notes
			for (ThesaurusTerm thesaurusTerm : terms) {
				JaxbList<Note> termNotes = gincoTermExporter
						.getExportTermNotes(thesaurusTerm);
				if (termNotes != null && !termNotes.isEmpty()) {
					thesaurusToExport.getTermNotes().put(
							thesaurusTerm.getIdentifier(), termNotes);
				}
			}

			// Exporting concept notes
			JaxbList<Note> conceptNotes = gincoConceptExporter
					.getExportConceptNotes(thesaurusConcept);
			if (conceptNotes != null && !conceptNotes.isEmpty()) {
				thesaurusToExport.getConceptNotes().put(
						thesaurusConcept.getIdentifier(), conceptNotes);
			}

			// Exporting the parent concepts
			JaxbList<ConceptHierarchicalRelationship> parentConceptHierarchicalRelationship = gincoConceptExporter
					.getExportHierarchicalConcepts(thesaurusConcept);
			if (parentConceptHierarchicalRelationship != null
					&& !parentConceptHierarchicalRelationship.isEmpty()) {
				thesaurusToExport.getHierarchicalRelationship().put(
						thesaurusConcept.getIdentifier(),
						parentConceptHierarchicalRelationship);
			}

			// Exporting associative relationship
			JaxbList<String> associations = gincoConceptExporter
					.getExportAssociativeRelationShip(thesaurusConcept);
			if (associations != null && !associations.isEmpty()) {
				thesaurusToExport.getAssociativeRelationship().put(
						thesaurusConcept.getIdentifier(), associations);
			}
			
			// Exporting alignments
			JaxbList<Alignment> alignments = gincoConceptExporter.getExportAlignments(thesaurusConcept);
			if (alignments != null && !alignments.isEmpty()) {
				thesaurusToExport.getAlignments().put(
						thesaurusConcept.getIdentifier(), alignments);
			}

		}
		
		//Exporting Custom Attributes Types for concepts and terms
		thesaurusToExport.setTermAttributeTypes(customTermAttributeTypeService.getAttributeTypesByThesaurus(thesaurus));
		thesaurusToExport.setConceptAttributeTypes(customConceptAttributeTypeService.getAttributeTypesByThesaurus(thesaurus));

		// ---Exporting the arrays
		List<ThesaurusArray> arrays = thesaurusArrayService
				.getAllThesaurusArrayByThesaurusId(null, thesaurusId);
		thesaurusToExport.setConceptArrays(arrays);

		// ---Exporting the labels of all arrays
		for (ThesaurusArray thesaurusArray : arrays) {
			JaxbList<NodeLabel> arrayLabels = new JaxbList<NodeLabel>();
			arrayLabels.getList().clear();
			arrayLabels.getList().add(
					nodeLabelService.getByThesaurusArray(thesaurusArray
							.getIdentifier()));
			thesaurusToExport.getConceptArrayLabels().put(
					thesaurusArray.getIdentifier(), arrayLabels);
		}

		// ---Exporting the concept groups
		List<ThesaurusConceptGroup> groups = thesaurusConceptGroupService
				.getAllThesaurusConceptGroupsByThesaurusId(null, thesaurusId);
		for (ThesaurusConceptGroup thesaurusGroup : groups) {
			thesaurusToExport.getConceptGroups().add(thesaurusGroup);
		}

		// ---Exporting the labels of all concept groups
		for (ThesaurusConceptGroup thesaurusConceptGroup : groups) {
			JaxbList<ThesaurusConceptGroupLabel> groupLabels = new JaxbList<ThesaurusConceptGroupLabel>();
			groupLabels.getList().clear();
			groupLabels.getList().add(
					thesaurusConceptGroupLabelService
							.getByThesaurusConceptGroup(thesaurusConceptGroup
									.getIdentifier()));
			thesaurusToExport.getConceptGroupLabels().put(
					thesaurusConceptGroup.getIdentifier(), groupLabels);
		}

		// ---Exporting complex concepts
		List<SplitNonPreferredTerm> complexConcepts = splitNonPreferredTermService
				.getSplitNonPreferredTermList(0, splitNonPreferredTermService
						.getSplitNonPreferredTermCount(thesaurusId).intValue(),
						thesaurusId);
		for (SplitNonPreferredTerm complexConcept : complexConcepts) {
			thesaurusToExport.getComplexConcepts().add(complexConcept);
		}

		// We encode it in XML
		return gincoExportServiceUtil
				.serializeThesaurusToXmlWithJaxb(thesaurusToExport);
	}
}
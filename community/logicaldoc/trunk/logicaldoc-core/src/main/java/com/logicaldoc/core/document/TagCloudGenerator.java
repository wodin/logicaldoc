package com.logicaldoc.core.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;
import com.logicaldoc.core.task.Task;

/**
 * This task generate all data needed by the tag cloud panel.
 * <p>
 * Each tag cloud is serialized in a custom attribute of the generic with
 * type='tagcloud' and subtype='-'. For each tag cloud a new attribute named
 * using the keyword will be created and its value will be 'occurrence|scale'.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class TagCloudGenerator extends Task {

	private static final String SUBTYPE_TAGCLOUD = "-";

	private static final String TYPE_TAGCLOUD = "tagcloud";

	public static final String NAME = "TagCloudGenerator";

	private DocumentDAO documentDao;

	private GenericDAO genericDao;

	public TagCloudGenerator() {
		super(NAME);
		log = LogFactory.getLog(TagCloudGenerator.class);
	}

	public void setGenericDao(GenericDAO genericDao) {
		this.genericDao = genericDao;
	}

	public void setDocumentDao(DocumentDAO documentDao) {
		this.documentDao = documentDao;
	}

	@Override
	public boolean isIndeterminate() {
		return true;
	}

	@Override
	protected void runTask() throws Exception {
		log.info("Start tag clouds generation");

		// Obtain the proper generic that will store TagClouds
		Generic generic = genericDao.findByAlternateKey(TYPE_TAGCLOUD, SUBTYPE_TAGCLOUD);
		if (generic == null) {
			generic = new Generic(TYPE_TAGCLOUD, SUBTYPE_TAGCLOUD);
			genericDao.store(generic);
		}
		genericDao.initialize(generic);
		generic.getAttributes().clear();

		// Iterate over tag clouds and serialize them as extended attributes
		List<TagCloud> tags = getTagClouds();
		for (TagCloud tagCloud : tags) {
			generic.setValue(tagCloud.getKeyword(), tagCloud.getOccurence() + "|" + tagCloud.getScale());
		}
		genericDao.store(generic);

		log.info("End tag clouds generation");
	}

	/**
	 * Computes the list of tag clouds
	 */
	public List<TagCloud> getTagClouds() {
		List<TagCloud> tags = new ArrayList<TagCloud>();

		HashMap<String, Integer> keywords = (HashMap<String, Integer>) documentDao.findAllKeywords();
		if (keywords.isEmpty())
			return tags;

		for (String key : keywords.keySet()) {
			Integer val = keywords.get(key);
			TagCloud tc = new TagCloud(key, val);
			tags.add(tc);
		}

		// order the list by occurrences
		Comparator<TagCloud> compOccurrence = new TagCloudComparatorOccurrence();
		Collections.sort(tags, compOccurrence);
		Collections.reverse(tags);

		// keep only the first 30 elements
		if (tags.size() > 30)
			tags = tags.subList(0, 30);

		// Find the Max frequency
		int maxValue = tags.get(0).getOccurence();
		log.debug("maxValue = " + maxValue);

		for (TagCloud cloud : tags) {
			double scale = ((double) cloud.getOccurence()) / maxValue;
			int scaleInt = (int) Math.ceil(scale * 10);
			cloud.setScale(scaleInt);
		}

		return tags;
	}

	class TagCloudComparatorOccurrence implements Comparator<TagCloud> {
		public int compare(TagCloud tc0, TagCloud tc1) {
			return new Integer(tc0.getOccurence()).compareTo(tc1.getOccurence());
		}
	}
}

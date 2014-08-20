package com.logicaldoc.core.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.GenericDAO;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.dao.TenantDAO;
import com.logicaldoc.core.task.Task;
import com.logicaldoc.util.config.ContextProperties;

/**
 * This task generate all data needed by the tag cloud panel.
 * <p>
 * Each tag cloud is serialized in a custom attribute of the generic with
 * type='tagcloud' and subtype='-'. For each tag cloud a new attribute named
 * using the tag will be created and its value will be 'occurrence|scale'.
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

	private TenantDAO tenantDao;

	public TagCloudGenerator() {
		super(NAME);
		log = LoggerFactory.getLogger(TagCloudGenerator.class);
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
	public boolean isConcurrent() {
		return true;
	}

	@Override
	protected void runTask() throws Exception {
		log.info("Start tag clouds generation");

		List<Tenant> tenants = tenantDao.findAll();

		for (Tenant tenant : tenants) {
			log.info("Generating tag cloud for tenant " + tenant);

			// Obtain the proper generic that will store TagClouds
			Generic generic = genericDao.findByAlternateKey(TYPE_TAGCLOUD, SUBTYPE_TAGCLOUD, null, tenant.getId());
			if (generic == null) {
				generic = new Generic(TYPE_TAGCLOUD, SUBTYPE_TAGCLOUD);
				generic.setTenantId(tenant.getId());
				genericDao.store(generic);
			}
			genericDao.initialize(generic);
			generic.getAttributes().clear();

			// Iterate over tag clouds and serialize them as extended attributes
			List<TagCloud> tags = getTagClouds(tenant.getId());
			for (TagCloud tagCloud : tags) {
				generic.setValue(tagCloud.getTag(), tagCloud.getCount() + "|" + tagCloud.getScale());
			}
			genericDao.store(generic);
		}

		log.info("End tag clouds generation");
	}

	/**
	 * Computes the list of tag clouds
	 */
	public List<TagCloud> getTagClouds(long tenantId) {
		List<TagCloud> tags = new ArrayList<TagCloud>();

		HashMap<String, Integer> tgs = (HashMap<String, Integer>) documentDao.findTags(null, tenantId);
		if (tgs.isEmpty())
			return tags;

		for (String key : tgs.keySet()) {
			Integer val = tgs.get(key);
			TagCloud tc = new TagCloud(key, val);
			tags.add(tc);
		}

		// order the list by occurrences
		Comparator<TagCloud> compOccurrence = new TagCloudComparatorOccurrence();
		Collections.sort(tags, compOccurrence);
		Collections.reverse(tags);

		// keep only the first N elements
		int n = 30;
		ContextProperties config;
		try {
			config = new ContextProperties();
			n = config.getInt("tagcloud.maxtags");
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		if (tags.size() > n)
			tags = tags.subList(0, n);

		// Find the Max frequency
		int maxValue = tags.get(0).getCount();
		log.debug("maxValue = " + maxValue);

		for (TagCloud cloud : tags) {
			double scale = ((double) cloud.getCount()) / maxValue;
			int scaleInt = (int) Math.ceil(scale * 10);
			cloud.setScale(scaleInt);
		}

		return tags;
	}

	class TagCloudComparatorOccurrence implements Comparator<TagCloud> {
		public int compare(TagCloud tc0, TagCloud tc1) {
			return new Integer(tc0.getCount()).compareTo(tc1.getCount());
		}
	}

	public void setTenantDao(TenantDAO tenantDao) {
		this.tenantDao = tenantDao;
	}
}

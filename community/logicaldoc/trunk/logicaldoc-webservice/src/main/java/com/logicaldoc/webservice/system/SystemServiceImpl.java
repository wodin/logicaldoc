package com.logicaldoc.webservice.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.SystemInfo;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.GenericDAO;
import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.stats.StatsCollector;
import com.logicaldoc.util.Context;
import com.logicaldoc.webservice.AbstractService;
import com.logicaldoc.webservice.WSParameter;

/**
 * System Web Service Implementation
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class SystemServiceImpl extends AbstractService implements SystemService {

	protected static Logger log = LoggerFactory.getLogger(SystemServiceImpl.class);

	@Override
	public WSParameter[] getStatistics(String sid) throws Exception {
		User user = validateSession(sid);

		GenericDAO genDao = (GenericDAO) Context.getInstance().getBean(GenericDAO.class);

		WSParameter[] parameters = new WSParameter[15];
		try {
			/*
			 * Repository statistics
			 */
			Generic gen = genDao.findByAlternateKey(StatsCollector.STAT, "docdir", null, user.getTenantId());
			WSParameter docDirSize = new WSParameter();
			docDirSize.setName("repo_docs");
			if (gen != null)
				docDirSize.setValue(gen.getString1());
			else
				docDirSize.setValue("0");
			parameters[0] = docDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "userdir", null, user.getTenantId());
			WSParameter userDirSize = new WSParameter();
			userDirSize.setName("repo_users");
			if (gen != null)
				userDirSize.setValue(gen.getString1());
			else
				userDirSize.setValue("0");
			parameters[1] = userDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "indexdir", null, user.getTenantId());
			WSParameter indexDirSize = new WSParameter();
			indexDirSize.setName("repo_fulltextindex");
			if (gen != null)
				indexDirSize.setValue(gen.getString1());
			else
				indexDirSize.setValue("0");

			parameters[2] = indexDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "importdir", null, user.getTenantId());
			WSParameter importDirSize = new WSParameter();
			importDirSize.setName("repo_import");
			if (gen != null)
				importDirSize.setValue(gen.getString1());
			else
				importDirSize.setValue("0");
			parameters[3] = importDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "exportdir", null, user.getTenantId());
			WSParameter exportDirSize = new WSParameter();
			exportDirSize.setName("repo_export");
			if (gen != null)
				exportDirSize.setValue(gen.getString1());
			else
				exportDirSize.setValue("0");
			parameters[4] = exportDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "plugindir", null, user.getTenantId());
			WSParameter pluginsDirSize = new WSParameter();
			pluginsDirSize.setName("repo_plugins");
			if (gen != null)
				pluginsDirSize.setValue(gen.getString1());
			else
				pluginsDirSize.setValue("0");
			parameters[5] = pluginsDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "dbdir", null, user.getTenantId());
			WSParameter dbDirSize = new WSParameter();
			dbDirSize.setName("repo_database");
			if (gen != null)
				dbDirSize.setValue(gen.getString1());
			else
				dbDirSize.setValue("0");

			parameters[6] = dbDirSize;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "logdir", null, user.getTenantId());
			WSParameter logsDirSize = new WSParameter();
			logsDirSize.setName("repo_logs");
			if (gen != null)
				logsDirSize.setValue(gen.getString1());
			else
				logsDirSize.setValue("0");

			parameters[7] = logsDirSize;

			/*
			 * Document statistics
			 */
			gen = genDao.findByAlternateKey(StatsCollector.STAT, "notindexeddocs", null, user.getTenantId());
			WSParameter notIndexed = new WSParameter();
			notIndexed.setName("docs_notindexed");
			notIndexed.setValue(gen != null ? Long.toString(gen.getInteger1()) : "0");
			parameters[8] = notIndexed;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "indexeddocs", null, user.getTenantId());
			WSParameter indexed = new WSParameter();
			indexed.setName("docs_indexed");
			indexed.setValue(gen != null ? Long.toString(gen.getInteger1()) : "0");
			parameters[9] = indexed;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "deleteddocs", null, user.getTenantId());
			WSParameter deletedDocs = new WSParameter();
			deletedDocs.setName("docs_trash");
			deletedDocs.setValue(gen != null ? Long.toString(gen.getInteger1()) : "0");
			parameters[10] = deletedDocs;

			/*
			 * Folders statistics
			 */
			gen = genDao.findByAlternateKey(StatsCollector.STAT, "withdocs", null, user.getTenantId());
			WSParameter notEmptyFolders = new WSParameter();
			notEmptyFolders.setName("folder_withdocs");
			notEmptyFolders.setValue(gen != null ? Long.toString(gen.getInteger1()) : "0");
			parameters[11] = notEmptyFolders;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "empty", null, user.getTenantId());
			WSParameter emptyFolders = new WSParameter();
			emptyFolders.setName("folder_empty");
			emptyFolders.setValue(gen != null ? Long.toString(gen.getInteger1()) : "0");
			parameters[12] = emptyFolders;

			gen = genDao.findByAlternateKey(StatsCollector.STAT, "deletedfolders", null, user.getTenantId());
			WSParameter deletedFolders = new WSParameter();
			deletedFolders.setName("folder_trash");
			deletedFolders.setValue(gen != null ? Long.toString(gen.getInteger1()) : "0");
			parameters[13] = deletedFolders;

			/*
			 * Last run
			 */
			gen = genDao.findByAlternateKey(StatsCollector.STAT, "lastrun", null, user.getTenantId());
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = gen != null ? df.parse(gen.getString1()) : null;
			WSParameter lastrun = new WSParameter();
			lastrun.setName("stats_lastrun");
			if (date != null) {
				lastrun.setValue(df.format(date));
			} else {
				lastrun.setValue("");
			}
			parameters[14] = lastrun;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return parameters;
	}

	@Override
	public String[] getLanguages(String tenant) throws Exception {
		List<String> langs = new ArrayList<String>();

		try {
			for (Language lang : LanguageManager.getInstance().getActiveLanguages(tenant)) {
				langs.add(lang.getLocale().toString());
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return langs.toArray(new String[0]);
	}

	@Override
	public WSSystemInfo getInfo() throws Exception {
		SystemInfo inf = SystemInfo.get();
		WSSystemInfo info = new WSSystemInfo();
		BeanUtils.copyProperties(info, inf);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		info.setDate(df.format(inf.getDate()));

		return info;
	}
}

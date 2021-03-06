package org.sakaiproject.importer.impl.translators;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.importer.api.Importable;
import org.sakaiproject.importer.api.IMSResourceTranslator;
import org.sakaiproject.importer.impl.Blackboard55FileParser;
import org.sakaiproject.importer.impl.XPathHelper;
import org.sakaiproject.importer.impl.importables.Announcement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Bb55AnnouncementTranslator implements IMSResourceTranslator {
	
	private static Log log = LogFactory.getLog(Bb55AnnouncementTranslator.class);

	public String getTypeName() {
		// TODO Auto-generated method stub
		return "resource/x-bb-announcement";
	}

	public boolean processResourceChildren() {
		return false;
	}

	public Importable translate(Node archiveResource, Document descriptor, String contextPath, String archiveBasePath) {
		// create a new generic object to return
		Announcement item = new Announcement();

		// Dates from Bb are formatted like '2007-05-08 23:45:00 EDT'
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss zzz");

		// populate the generic object fields
		item.setTitle(XPathHelper.getNodeValue("/ANNOUNCEMENT/TITLE/@value", descriptor));
		item.setDescription(XPathHelper.getNodeValue("/ANNOUNCEMENT/DESCRIPTION/TEXT", descriptor));

		item.setHtml(Boolean.getBoolean(XPathHelper.getNodeValue("/ANNOUNCEMENT/FLAGS/ISHTML/@value", descriptor)));
		item.setLiternalNewline(Boolean.getBoolean(XPathHelper.getNodeValue("/ANNOUNCEMENT/FLAGS/ISNEWLINELITERAL/@value", descriptor)));
		item.setPermanent(Boolean.getBoolean(XPathHelper.getNodeValue("/ANNOUNCEMENT/ISPERMANENT/@value", descriptor)));

		// attempt to parse the start date
		try {
			Date d = df.parse(XPathHelper.getNodeValue("/ANNOUNCEMENT/DATES/RESTRICTSTART/@value", descriptor));
			item.setStart(d);
		} catch (ParseException e) {
			// report it but continue
			log.warn("Could not parse date startdate for "+item.getTitle()+": " + e.toString());
		}

		// attempt to parse the end date
		try {
			Date d = df.parse(XPathHelper.getNodeValue("/ANNOUNCEMENT/DATES/RESTRICTEND/@value", descriptor));
			item.setEnd(d);
		} catch (ParseException e) {
			// report it but continue
			log.warn("Could not parse date enddate for "+item.getTitle()+": " + e.toString());
		}

		log.info("Translation complete for BB55 announcement item:" + item.getTitle());
		log.debug("Announcement item: " + item.toString());
		item.setLegacyGroup(Blackboard55FileParser.ANNOUNCEMENT_GROUP);
		return item;
	}

}

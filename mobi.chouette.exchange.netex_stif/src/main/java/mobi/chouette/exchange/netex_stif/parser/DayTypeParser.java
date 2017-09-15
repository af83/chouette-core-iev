package mobi.chouette.exchange.netex_stif.parser;

import org.xmlpull.v1.XmlPullParser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex_stif.Constant;
import mobi.chouette.exchange.netex_stif.validator.DayTypeValidator;
import mobi.chouette.exchange.netex_stif.validator.ValidatorFactory;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class DayTypeParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);
		xpp.require(XmlPullParser.START_TAG, null, DAY_TYPE);
		int columnNumber = xpp.getColumnNumber();
		int lineNumber = xpp.getLineNumber();
		DayTypeValidator validator = (DayTypeValidator) ValidatorFactory.getValidator(context, DayTypeValidator.class);
		String id = xpp.getAttributeValue(null, ID);
		Timetable timeTable = ObjectFactory.getTimetable(referential, id);
		Long version = (Long) context.get(VERSION);
		timeTable.setObjectVersion(version);

		// for post import checkPoints
		validator.addLocation(context, timeTable, lineNumber, columnNumber);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			// log.info("DayTypeParser tag : "+ xpp.getName());
			if (xpp.getName().equals(NAME)) {
				timeTable.setComment(xpp.nextText());
			} else if (xpp.getName().equals(PROPERTIES)) {
				parseProperties(xpp, context, timeTable);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseProperties(XmlPullParser xpp, Context context, Timetable timetable) throws Exception {
		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals(PROPERTY_OF_DAY)) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals(DAYS_OF_WEEK)) {
						String day = xpp.nextText();
						try {
							timetable.addDayType(DayTypeEnum.valueOf(day));
						} catch (Exception ex) {
							log.warn("unmanaged daytype " + day);
						}

					}
				}
			}
		}
	}

	static {
		ParserFactory.register(DayTypeParser.class.getName(), new ParserFactory() {
			private DayTypeParser instance = new DayTypeParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}

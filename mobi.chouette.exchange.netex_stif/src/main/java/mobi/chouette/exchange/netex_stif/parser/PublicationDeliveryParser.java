package mobi.chouette.exchange.netex_stif.parser;

import org.xmlpull.v1.XmlPullParser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex_stif.Constant;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class PublicationDeliveryParser implements Parser, Constant {

	

	@Override
	public void parse(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		//Referential referential = (Referential) context.get(REFERENTIAL);
		String version = xpp.getAttributeValue(null, VERSION);
		// TODO vérifier que la version soit celle du stif
	
		
		
		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals(DATA_OBJECTS)) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals(COMPOSITE_FRAME)) {
						Parser compositeFrameParser = ParserFactory.create(CompositeFrameParser.class.getName());
						compositeFrameParser.parse(context);
					}
				}
			}  else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

}

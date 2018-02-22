package mobi.chouette.exchange.netex_stif.parser;

import java.util.Collection;

import org.xmlpull.v1.XmlPullParser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex_stif.NetexStifConstant;
import mobi.chouette.exchange.netex_stif.validator.FrameValidator;
import mobi.chouette.exchange.netex_stif.validator.ValidatorFactory;
import mobi.chouette.model.LineLite;

@Log4j
public class CompositeFrameParser implements Parser {

	@SuppressWarnings("unchecked")
	@Override
	public void parse(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(Constant.PARSER);
		xpp.require(XmlPullParser.START_TAG, null, NetexStifConstant.COMPOSITE_FRAME);

		String type = null;
		boolean valid = false;
		int columnNumber = xpp.getColumnNumber();
		int lineNumber = xpp.getLineNumber();

		FrameValidator validator = (FrameValidator) ValidatorFactory.getValidator(context, FrameValidator.class);
		Collection<String> frames = (Collection<String>) context.get(NetexStifConstant.COMPOSITE_FRAMES);
		
		String modification = xpp.getAttributeValue(null, NetexStifConstant.MODIFICATION);
		if (modification != null && modification.equals("delete")) {
			LineLite line = (LineLite) context.get(Constant.LINE);
			log.info("parsing " + line.getObjectId() + " for deletion");
			while (xpp.nextTag() == XmlPullParser.START_TAG) {
				if (xpp.getName().equals(NetexStifConstant.TYPE_OF_FRAME_REF)) {
					type = xpp.getAttributeValue(null, NetexStifConstant.REF);
					valid = validator.checkForbiddenCompositeFrames(context, type, lineNumber, columnNumber);
					frames.add(type); // to check mandatory frames
					// add expected GeneralFrames for validation
					Collection<String> gframes = (Collection<String>) context.get(NetexStifConstant.GENERAL_FRAMES);
					gframes.add(NetexStifConstant.NETEX_STRUCTURE);
					gframes.add(NetexStifConstant.NETEX_HORAIRE);
					XPPUtil.skipSubTree(log, xpp);
				} else {
					XPPUtil.skipSubTree(log, xpp);
				}
			}
		} else {
			// normal frame
			while (xpp.nextTag() == XmlPullParser.START_TAG) {
				if (xpp.getName().equals(NetexStifConstant.TYPE_OF_FRAME_REF)) {
					type = xpp.getAttributeValue(null, NetexStifConstant.REF);
					valid = validator.checkForbiddenCompositeFrames(context, type, lineNumber, columnNumber);
					frames.add(type); // to check mandatory frames
					XPPUtil.skipSubTree(log, xpp);
				} else if (xpp.getName().equals(NetexStifConstant.FRAMES) && valid) {
					while (xpp.nextTag() == XmlPullParser.START_TAG) {
						if (xpp.getName().equals(NetexStifConstant.GENERAL_FRAME)) {
							Parser parser = ParserFactory.create(GeneralFrameParser.class.getName());
							parser.parse(context);
						} else {
							XPPUtil.skipSubTree(log, xpp);
						}
					}
				} else {
					XPPUtil.skipSubTree(log, xpp);
				}
			}
		}
	}

	static {
		ParserFactory.register(CompositeFrameParser.class.getName(), new ParserFactory() {
			private CompositeFrameParser instance = new CompositeFrameParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}

package mobi.chouette.exchange.netex_stif.importer;

import java.io.File;
import java.net.URL;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import lombok.Getter;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netex_stif.Constant;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

public class NetexStifSAXErrorHandler implements ErrorHandler, Constant {

	private static final String XML_1 = "1-NETEX-XML-1";
	private static final String XML_2 = "1-NETEX-XML-2";

	private ValidationReporter validationReporter;
	private Context context;
	private String fileName;

	@Getter
	private boolean hasErrors = false;

	public NetexStifSAXErrorHandler(Context context, String fileURL) throws Exception {
		this.context = context;
		validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, XML_1, "E");
		validationReporter.addItemToValidationReport(context, XML_2, "W");

		fileName = new File(new URL(fileURL).toURI()).getName();
	}

	public void handleError(Exception error) {
		if (error instanceof SAXParseException) {
			SAXParseException cause = (SAXParseException) error;
			DataLocation location = new DataLocation(fileName, cause.getLineNumber(), cause.getColumnNumber());
			validationReporter.addCheckPointReportError(context, XML_1, location, cause.getMessage());
		} else {
			DataLocation location = new DataLocation(fileName, 1, 1);
			location.setName("xml-failure");
			validationReporter.addCheckPointReportError(context, XML_1, location, error.toString());
		}
	}

	private void handleError(SAXParseException error, SEVERITY severity) {
		String key = "others";
		if (error.getMessage().contains(":")) {
			String newKey = error.getMessage().substring(0, error.getMessage().indexOf(":")).trim();
			if (!newKey.contains(" ")) {
				if (newKey.contains("."))
					newKey = newKey.substring(0, newKey.indexOf("."));
				key = newKey;
			}
		}
		if (severity.equals(CheckPointReport.SEVERITY.ERROR))
			hasErrors = true;

		DataLocation location = new DataLocation(fileName, error.getLineNumber(), error.getColumnNumber());
		location.setName(key);

		validationReporter.updateCheckPointReportSeverity(context, XML_2, severity);
		validationReporter.addCheckPointReportError(context, XML_2, location, error.getMessage());
		return;
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {

		handleError(e, SEVERITY.WARNING);

	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		handleError(e, SEVERITY.ERROR);
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		handleError(e, SEVERITY.ERROR);
		throw e;
	}

}

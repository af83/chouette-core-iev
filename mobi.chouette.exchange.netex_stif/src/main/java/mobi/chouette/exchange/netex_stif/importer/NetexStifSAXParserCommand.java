package mobi.chouette.exchange.netex_stif.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import javax.naming.InitialContext;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netex_stif.validator.NetexCheckPoints;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.FILE_ERROR_CODE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

@Log4j
public class NetexStifSAXParserCommand implements Command {

	public static final String COMMAND = "NetexStifSAXParserCommand";

	public static final String SCHEMA_FILE = "/xsd/NeTEx_publication.xsd";

	@Getter
	@Setter
	private String fileURL;

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = Constant.ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);
		context.remove(Constant.VALIDATION_CONTEXT);

		ActionReporter reporter = ActionReporter.Factory.getInstance();

		String fileName = new File(new URL(fileURL).toURI()).getName();
		reporter.addFileReport(context, fileName, IO_TYPE.INPUT);
		log.info("check " + fileName + " for XML/XSD syntax");

		Schema schema = (Schema) context.get(Constant.SCHEMA);
		if (schema == null) {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schema = factory.newSchema(getClass().getResource(SCHEMA_FILE));
			context.put(Constant.SCHEMA, schema);
		}

		URL url = new URL(fileURL);

		NetexStifSAXErrorHandler handler = new NetexStifSAXErrorHandler(context, fileURL);
		try (Reader reader = new BufferedReader(CharSetChecker.getEncodedInputStreamReader(fileName, url.openStream()),
				8192 * 10);) {
			StreamSource file = new StreamSource(reader);

			Validator validator = schema.newValidator();
			validator.setErrorHandler(handler);
			validator.validate(file);
			if (handler.isHasErrors()) {
				addLineEntry(context, reporter, fileName, "XML/XSD errors");
				reporter.addFileErrorInReport(context, fileName, FILE_ERROR_CODE.INVALID_FORMAT, "XML/XSD errors");
				return result;
			}
			result = Constant.SUCCESS;
		} catch (SAXException e) {
			log.error(e.getMessage());
			addLineEntry(context, reporter, fileName, e.getMessage());
			reporter.addFileErrorInReport(context, fileName, FILE_ERROR_CODE.INVALID_FORMAT, e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			reportError(context, e, fileName, reporter);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	private void reportError(Context context, Exception e, String fileName, ActionReporter reporter) {
		addLineEntry(context, reporter, fileName, e.getMessage());
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		DataLocation location = new DataLocation(fileName, 1, 1);
		validationReporter.updateCheckPointReportSeverity(context, NetexCheckPoints.L1_NetexStif_2,
				CheckPointReport.SEVERITY.ERROR);
		validationReporter.addCheckPointReportError(context, null, NetexCheckPoints.L1_NetexStif_2, location,
				e.getMessage());
		reporter.addFileErrorInReport(context, fileName, FILE_ERROR_CODE.INVALID_FORMAT, e.getMessage());
	}

	private void addLineEntry(Context context, ActionReporter reporter, String fileName, String description) {
		if (fileName.startsWith("offre_")) {
			// insert dummy line entry in report
			String name = fileName.replace(".xml", "");
			String[] tokens = name.split("_");
			reporter.addObjectReport(context, "STIF:CODIFLIGNE:Line:" + tokens[1], OBJECT_TYPE.LINE, tokens[2],
					OBJECT_STATE.OK, IO_TYPE.INPUT);
			reporter.addErrorToObjectReport(context, "STIF:CODIFLIGNE:Line:" + tokens[1], OBJECT_TYPE.LINE,
					ERROR_CODE.INVALID_FORMAT, description);
		}
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			return new NetexStifSAXParserCommand();
		}
	}

	static {
		CommandFactory.register(NetexStifSAXParserCommand.class.getName(), new DefaultCommandFactory());
	}
}

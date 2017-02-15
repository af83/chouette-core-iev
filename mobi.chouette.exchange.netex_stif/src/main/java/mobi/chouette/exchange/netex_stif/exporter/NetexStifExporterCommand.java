package mobi.chouette.exchange.netex_stif.exporter;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.CommandCancelledException;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.ProgressionCommand;
import mobi.chouette.exchange.exporter.AbstractExporterCommand;
import mobi.chouette.exchange.netex_stif.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = NetexStifExporterCommand.COMMAND)
public class NetexStifExporterCommand extends AbstractExporterCommand implements Command, Constant, ReportConstant {

	public static final String COMMAND = "NetexExporterCommand";

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		// initialize reporting and progression
		ProgressionCommand progression = (ProgressionCommand) CommandFactory.create(initialContext,
				ProgressionCommand.class.getName());

		try {

			// read parameters
			Object configuration = context.get(CONFIGURATION);
			if (!(configuration instanceof NetexStifExportParameters)) {
				// fatal wrong parameters
				log.error("invalid parameters for netex export " + configuration.getClass().getName());
				reporter.setActionError(context, ERROR_CODE.INVALID_PARAMETERS,"invalid parameters for netex export " + configuration.getClass().getName());
				return ERROR;
			}

			NetexStifExportParameters parameters = (NetexStifExportParameters) configuration;
			if (parameters.getStartDate() != null && parameters.getEndDate() != null) {
				if (parameters.getStartDate().after(parameters.getEndDate())) {
					reporter.setActionError(context, ERROR_CODE.INVALID_PARAMETERS,"end date before start date");
					return ERROR;

				}
			}
			// no validation available for this export
			parameters.setValidateAfterExport(false);
			
			ProcessingCommands commands = ProcessingCommandsFactory.create(NetexStifExporterProcessingCommands.class
					.getName());

			result = process(context, commands, progression, true,Mode.line);

		} catch (CommandCancelledException e) {
			reporter.setActionError(context, ERROR_CODE.INTERNAL_ERROR, "Command cancelled");
			log.error(e.getMessage());
		} catch (Exception e) {
			if (!COMMAND_CANCELLED.equals(e.getMessage())) {
				reporter.setActionError(context, ERROR_CODE.INTERNAL_ERROR,"Fatal :" + e);
				log.error(e.getMessage(), e);
			}
		} finally {
			progression.dispose(context);
			log.info(Color.YELLOW + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.netex/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexStifExporterCommand.class.getName(), new DefaultCommandFactory());
	}
}
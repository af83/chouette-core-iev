package mobi.chouette.exchange.exporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import lombok.Getter;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.ProgressionCommand;
import mobi.chouette.exchange.DaoReader;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.parameters.AbstractExportParameter;
import mobi.chouette.exchange.report.ActionReporter;

public class AbstractExporterCommand {

	protected enum Mode {
		line, stopareas
	}

	@Getter
	@EJB
	DaoReader reader;

	public boolean process(Context context, ProcessingCommands commands, ProgressionCommand progression,
			boolean continueLineProcesingOnError, Mode mode) throws Exception {
		boolean result = Constant.ERROR;
		AbstractExportParameter parameters = (AbstractExportParameter) context.get(Constant.CONFIGURATION);
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		result = processInit(context, commands, progression, mode);

		if (mode.equals(Mode.line)) {
			// get lines
			String type = parameters.getReferencesType();
			// set default type
			if (type == null || type.isEmpty()) {
				// all lines
				type = "line";
				parameters.setIds(null);
			}
			type = type.toLowerCase();

			List<Long> ids = null;
			if (parameters.getIds() != null) {
				ids = new ArrayList<>(parameters.getIds());
			}

			Set<Long> lines = reader.loadLines(parameters,type, ids);
			if (lines.isEmpty()) {
				reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_FOUND, "no data selected");
				return Constant.ERROR;

			}
			progression.execute(context);

			// process lines
			result = processLines(context, commands, progression, continueLineProcesingOnError, lines);
		} else // stopareas
		{
			// get stop info
			List<? extends Command> stopProcessingCommands = commands.getStopAreaProcessingCommands(context, true);
			progression.start(context, stopProcessingCommands.size());
			for (Command command : stopProcessingCommands) {
				result = command.execute(context);
				if (!result) {
					return Constant.ERROR;
				}
				progression.execute(context);
			}
		}
		// post processing
		result = processTermination(context, commands, progression);
		return result;
	}

	protected boolean processTermination(Context context, ProcessingCommands commands, ProgressionCommand progression) throws Exception {
		boolean result = Constant.SUCCESS;
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		List<? extends Command> postProcessingCommands = commands.getPostProcessingCommands(context, true);
		progression.terminate(context, postProcessingCommands.size());
		for (Command exportCommand : postProcessingCommands) {
			result = exportCommand.execute(context);
			if (!result) {
				if (!reporter.hasActionError(context))
					reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_PROCEEDED, "no data exported");
				return Constant.ERROR;
			}
			progression.execute(context);
		}
		return result;
	}

	protected boolean processLines(Context context, ProcessingCommands commands, ProgressionCommand progression,
			boolean continueLineProcesingOnError, Set<Long> lines) throws Exception {
		boolean result = Constant.SUCCESS;
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		List<? extends Command> lineProcessingCommands = commands.getLineProcessingCommands(context, true);
		progression.start(context, lines.size());
		int lineCount = 0;
		// export each line
		for (Long line : lines) {
			context.put(Constant.LINE_ID, line);
			boolean exportFailed = false;
			for (Command exportCommand : lineProcessingCommands) {
				result = exportCommand.execute(context);
				if (!result) {
					exportFailed = true;
					break;
				}
			}
			progression.execute(context);
			if (!exportFailed) {
				lineCount++;
			} else if (!continueLineProcesingOnError) {
				reporter.setActionError(context, ActionReporter.ERROR_CODE.INVALID_DATA, "unable to export data");
				return Constant.ERROR;
			}
		}
		// check if data where exported
		if (lineCount == 0) {
			progression.terminate(context, 1);
			reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_PROCEEDED, "no data exported");
			progression.execute(context);
			return Constant.ERROR;
		}
		return result;
	}

	protected boolean processInit(Context context, ProcessingCommands commands, ProgressionCommand progression,
			Mode mode) throws Exception {
		// initialisation
		boolean result = Constant.ERROR;
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		List<? extends Command> preProcessingCommands = commands.getPreProcessingCommands(context, true);
		progression.initialize(context, preProcessingCommands.size() + (mode.equals(Mode.line) ? 1 : 0));
		for (Command exportCommand : preProcessingCommands) {
			result = exportCommand.execute(context);
			if (!result) {
				reporter.setActionError(context, ActionReporter.ERROR_CODE.NO_DATA_FOUND, "no data selected");
				progression.execute(context);
				break;
			}
			progression.execute(context);
		}
		return result;
	}

}

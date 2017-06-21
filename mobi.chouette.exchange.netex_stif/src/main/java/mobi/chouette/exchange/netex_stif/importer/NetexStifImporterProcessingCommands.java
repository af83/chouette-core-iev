package mobi.chouette.exchange.netex_stif.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Chain;
import mobi.chouette.common.chain.ChainCommand;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.ProcessingCommands;
import mobi.chouette.exchange.ProcessingCommandsFactory;
import mobi.chouette.exchange.importer.CleanRepositoryCommand;
import mobi.chouette.exchange.importer.FootnoteRegisterCommand;
import mobi.chouette.exchange.importer.RouteRegisterCommand;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.ERROR_CODE;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

@Data
@Log4j
public class NetexStifImporterProcessingCommands implements ProcessingCommands, Constant {

	public static class DefaultFactory extends ProcessingCommandsFactory {

		@Override
		protected ProcessingCommands create() throws IOException {
			ProcessingCommands result = new NetexStifImporterProcessingCommands();
			return result;
		}
	}

	static {
		ProcessingCommandsFactory.factories.put(NetexStifImporterProcessingCommands.class.getName(),
				new DefaultFactory());
	}

	@Override
	public List<? extends Command> getPreProcessingCommands(Context context, boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		NetexStifImportParameters parameters = (NetexStifImportParameters) context.get(CONFIGURATION);
		List<Command> commands = new ArrayList<>();
		try {
			if (withDao && parameters.isCleanRepository()) {
				commands.add(CommandFactory.create(initialContext, CleanRepositoryCommand.class.getName()));
			}
			commands.add(CommandFactory.create(initialContext, NetexStifUncompressCommand.class.getName()));
			commands.add(CommandFactory.create(initialContext, NetexStifInitImportCommand.class.getName()));
			commands.add(CommandFactory.create(initialContext, NetexStifLoadSharedDataCommand.class.getName()));
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException("unable to call factories");
		}

		return commands;
	}

	private Chain treatOneFile(Context context, String filename, JobData jobData, boolean mandatory)
			throws ClassNotFoundException, IOException {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		String path = jobData.getPathName();
		Chain chain = (Chain) CommandFactory.create(initialContext, ChainCommand.class.getName());
		File file = new File(path + "/" + INPUT + "/" + filename);
		if (!file.exists()) {
			if (mandatory) {
				ActionReporter reporter = ActionReporter.Factory.getInstance();
				reporter.setActionError(context, ERROR_CODE.INVALID_PARAMETERS, "no "+filename+" file");
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				String zipName = jobData.getInputFilename();
				validationReporter.addCheckPointReportError(context, NetexStifUncompressCommand.L1_NetexStif_1, "2",
						new DataLocation(zipName), filename);
				NetexStifImportParameters parameters = (NetexStifImportParameters) context.get(CONFIGURATION);
				parameters.setNoSave(true); // block save mode to check other
											// files
			}
			return chain;
		}

		// log.info(file);
		String url = file.toURI().toURL().toExternalForm();
		log.info("url : " + url);
		// validation schema
		NetexStifSAXParserCommand schema = (NetexStifSAXParserCommand) CommandFactory.create(initialContext,
				NetexStifSAXParserCommand.class.getName());
		schema.setFileURL(url);
		chain.add(schema);
		NetexStifParserCommand parser = (NetexStifParserCommand) CommandFactory.create(initialContext,
				NetexStifParserCommand.class.getName());
		parser.setFileURL(url);
		chain.add(parser);
		return chain;
	}

	@Override
	public List<? extends Command> getLineProcessingCommands(Context context, boolean withDao) {
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		NetexStifImportParameters parameters = (NetexStifImportParameters) context.get(CONFIGURATION);
		// boolean level3validation = context.get(VALIDATION) != null;
		List<Command> commands = new ArrayList<>();
		JobData jobData = (JobData) context.get(JOB_DATA);
		try {
			Chain tmp = treatOneFile(context, "calendrier.xml", jobData, true);
			commands.add(tmp);
			tmp = treatOneFile(context, "commun.xml", jobData, false);
			commands.add(tmp);

			// TODO a supprimer quand copycommand sera ok
			context.put(OPTIMIZED, Boolean.FALSE);

			Path path = Paths.get(jobData.getPathName(), INPUT);
			List<Path> stream = FileUtil.listFiles(path, "offre*.xml", "*metadata*");
			if (stream.isEmpty()) {
				ActionReporter reporter = ActionReporter.Factory.getInstance();
				reporter.setActionError(context, ERROR_CODE.INVALID_PARAMETERS, "no offer data");
				ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
				String fileName = jobData.getInputFilename();
				validationReporter.addCheckPointReportError(context, NetexStifUncompressCommand.L1_NetexStif_1, "3",
						new DataLocation(fileName));
			}
			for (Path file : stream) {
				Chain chain = (Chain) CommandFactory.create(initialContext, ChainCommand.class.getName());
				commands.add(chain);
				String url = file.toUri().toURL().toExternalForm();
				// validation schema
				NetexStifSAXParserCommand schema = (NetexStifSAXParserCommand) CommandFactory.create(initialContext,
						NetexStifSAXParserCommand.class.getName());
				schema.setFileURL(url);
				chain.add(schema);

				// parser
				NetexStifParserCommand parser = (NetexStifParserCommand) CommandFactory.create(initialContext,
						NetexStifParserCommand.class.getName());
				parser.setFileURL(url);
				chain.add(parser);

				// validation
				Command validation = CommandFactory.create(initialContext, NetexStifValidationCommand.class.getName());
				chain.add(validation);

				// log.debug("WithDao : " + withDao + " / "+
				// parameters.isNoSave());
				if (withDao && !parameters.isNoSave()) {

					// register
					Command register = CommandFactory.create(initialContext, FootnoteRegisterCommand.class.getName());
					chain.add(register);
					register = CommandFactory.create(initialContext, RouteRegisterCommand.class.getName());
					chain.add(register);

					// Command copy = CommandFactory.create(initialContext,
					// CopyCommand.class.getName());
					// chain.add(copy);
				}
				// if (level3validation) {
				// // add validation
				// Command validate = CommandFactory.create(initialContext,
				// ImportedLineValidatorCommand.class.getName());
				// chain.add(validate);
				// }

			}

		} catch (Exception e) {

		}

		return commands;
	}

	@Override
	public List<? extends Command> getPostProcessingCommands(Context context, boolean withDao) {
		// InitialContext initialContext = (InitialContext)
		// context.get(INITIAL_CONTEXT);
		// boolean level3validation = context.get(VALIDATION) != null;

		List<Command> commands = new ArrayList<>();
		// try {
		// if (level3validation) {
		// // add shared data validation
		// commands.add(CommandFactory.create(initialContext,
		// SharedDataValidatorCommand.class.getName()));
		// }
		//
		// } catch (Exception e) {
		// log.error(e, e);
		// throw new RuntimeException("unable to call factories");
		// }
		return commands;
	}

	@Override
	public List<? extends Command> getStopAreaProcessingCommands(Context context, boolean withDao) {
		return new ArrayList<>();
	}

	@Override
	public List<? extends Command> getDisposeCommands(Context context, boolean withDao) {
		List<Command> commands = new ArrayList<>();
		return commands;
	}

}

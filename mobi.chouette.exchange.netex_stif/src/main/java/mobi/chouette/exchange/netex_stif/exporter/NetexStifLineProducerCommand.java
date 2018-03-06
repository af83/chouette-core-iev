package mobi.chouette.exchange.netex_stif.exporter;

import java.io.IOException;
import java.sql.Date;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netex_stif.exporter.producer.NetexStifOffreProducer;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.LineLite;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.Referential;

@Log4j
public class NetexStifLineProducerCommand implements Command {
	public static final String COMMAND = "NetexStifLineProducerCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = Constant.ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter reporter = ActionReporter.Factory.getInstance();

		try {

			Long lineId = (Long) context.get(Constant.LINE_ID);
			Referential r = (Referential) context.get(Constant.REFERENTIAL);
			LineLite line = r.findLine(lineId);
			r.setCurrentLine(line);
			log.info("procesing line " + NamingUtil.getName(line));
			NetexStifExportParameters configuration = (NetexStifExportParameters) context.get(Constant.CONFIGURATION);

			ExportableData collection = (ExportableData) context.get(Constant.EXPORTABLE_DATA);
			collection.clearLine();

			Date startDate = null;
			if (configuration.getStartDate() != null) {
				startDate = new Date(configuration.getStartDate().getTime());
			}

			Date endDate = null;
			if (configuration.getEndDate() != null) {
				endDate = new Date(configuration.getEndDate().getTime());
			}

			log.info("global validity period before collect" + collection.getGlobalValidityPeriod());

			NetexStifDataCollector collector = new NetexStifDataCollector();
			boolean cont = collector.collect(context, collection, r, startDate, endDate);
			log.info("global validity period after collect" + collection.getGlobalValidityPeriod());
			reporter.addObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, NamingUtil.getName(line),
					OBJECT_STATE.OK, IO_TYPE.INPUT);
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.JOURNEY_PATTERN, collection.getJourneyPatterns().size());
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.ROUTE, collection.getRoutes().size());
			reporter.setStatToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, OBJECT_TYPE.VEHICLE_JOURNEY, collection.getVehicleJourneys().size());
			// if (cont) {

				NetexStifOffreProducer producer = new NetexStifOffreProducer();
				producer.produce(context);

				result = Constant.SUCCESS;
//			} else {
//				reporter.addErrorToObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE,
//						ActionReporter.ERROR_CODE.NO_DATA_ON_PERIOD, "no data on period");
//				result = Constant.ERROR;
//			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new NetexStifLineProducerCommand();
			return result;
		}
	}

	static {
		CommandFactory.register(NetexStifLineProducerCommand.class.getName(), new DefaultCommandFactory());
	}

}

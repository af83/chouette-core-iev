package mobi.chouette.exchange.validation.checkpoints;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.hibernate.Session;
import org.jboss.arquillian.testng.Arquillian;
import org.testng.Assert;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData.ACTION;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.LoadSharedDataCommand;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.parameters.AccessLinkParameters;
import mobi.chouette.exchange.validation.parameters.AccessPointParameters;
import mobi.chouette.exchange.validation.parameters.CompanyParameters;
import mobi.chouette.exchange.validation.parameters.ConnectionLinkParameters;
import mobi.chouette.exchange.validation.parameters.FieldParameters;
import mobi.chouette.exchange.validation.parameters.GroupOfLineParameters;
import mobi.chouette.exchange.validation.parameters.JourneyPatternParameters;
import mobi.chouette.exchange.validation.parameters.LineParameters;
import mobi.chouette.exchange.validation.parameters.NetworkParameters;
import mobi.chouette.exchange.validation.parameters.RouteParameters;
import mobi.chouette.exchange.validation.parameters.StopAreaParameters;
import mobi.chouette.exchange.validation.parameters.TimetableParameters;
import mobi.chouette.exchange.validation.parameters.TransportModeParameters;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.parameters.VehicleJourneyParameters;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validator.JobDataTest;
import mobi.chouette.exchange.validator.ValidateParameters;
import mobi.chouette.exchange.validator.checkpoints.CheckPointConstant;
import mobi.chouette.model.ChouetteLocalizedObject;
import mobi.chouette.model.util.Referential;
import mobi.chouette.persistence.hibernate.ChouetteTenantIdentifierGenerator;
import mobi.chouette.persistence.hibernate.ContextHolder;

@Log4j
public abstract class AbstractTestValidation extends Arquillian implements Constant, ReportConstant, CheckPointConstant {

	public static final String SCHEMA_NAME = "iev_check_points";

	protected InitialContext initialContext;

	@PersistenceContext(unitName = "referential")
	EntityManager em;

	@Inject
	UserTransaction utx;

	public void init() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Locale.setDefault(Locale.ENGLISH);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				log.error("initialContext",e);
			}

		}

	}

	protected void loadSharedData(Context context) throws Exception {
		Command command = CommandFactory.create(initialContext, LoadSharedDataCommand.class.getName());
		command.execute(context);
	}

	protected Context initValidatorContext() {
		init();
		ContextHolder.setContext(SCHEMA_NAME); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(REFERENTIAL, new Referential());
		context.put(VALIDATION_REPORT, new ValidationReport());
		ValidateParameters configuration = new ValidateParameters();
		context.put(CONFIGURATION, configuration);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setOrganisationName("organisation");
		
		configuration.setReferentialName("checkPoints");
		configuration.setLineReferentialId(1L);
		configuration.setStopAreaReferentialId(1L);
		configuration.setIds(Arrays.asList(new Long[] { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L }));
		JobDataTest test = new JobDataTest();
		context.put(JOB_DATA, test);
		test.setPathName("target/referential/test");
		File f = new File("target/referential/test");
		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		f.mkdirs();
		test.setReferential(SCHEMA_NAME);
		test.setAction(ACTION.validator);
		context.put("testng", "true");
		context.put(SOURCE, SOURCE_DATABASE);
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	protected static final String path = "src/test/data/checkpoints";

	public static void copyFile(String fileName) throws IOException {
		File srcFile = new File(path, fileName);
		File destFile = new File("target/referential/test", fileName);
		FileUtils.copyFile(srcFile, destFile);
	}

	public void initSchema() throws Exception {
		ContextHolder.setContext(SCHEMA_NAME);
		BufferedReader r = new BufferedReader(new FileReader("src/test/data/validation_data.sql"));
		ChouetteTenantIdentifierGenerator.deleteTenant(SCHEMA_NAME);
		utx.begin();
		em.joinTransaction();
		Session hibernateSession = em.unwrap(Session.class);
		//
		hibernateSession.doWork(connection -> {
			Statement statement = null;
			try {
				statement = connection.createStatement();
				String command = null;
				int i = 0;
				boolean batch = false;
				do {
					i++;
					command = nextSqlCommand(r);
					if (command != null) {
						if (command.startsWith(" SELECT")) {
							if (batch)
								statement.executeBatch();
							batch = false;
							statement.executeQuery(command);
						} else {
							batch = true;
							statement.addBatch(command);
						}
					}
					if (i % 20 == 0) {
						if (batch)
							statement.executeBatch();
						batch = false;
					}

				} while (command != null);
				if (batch)
					statement.executeBatch();
			} catch (Exception ex) {
				ex.printStackTrace();
				if (ex instanceof SQLException) {
					SQLException sqlex = (SQLException) ex;
					if (sqlex.getNextException() != null)
						sqlex.getNextException().printStackTrace();
				}
			} finally {
				try {
					r.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (statement != null)
					statement.close();
			}
			//
		});
		utx.commit();

	}

	private String nextSqlCommand(BufferedReader r) throws Exception {

		StringBuilder b = new StringBuilder();
		String line = null;

		do {
			line = r.readLine();
			if (line == null)
				break;
			line = line.trim();
			if (line.isEmpty())
				continue;
			if (line.startsWith("--"))
				continue;
			b.append(' ').append(line);
			if (line.endsWith(";"))
				break;
		} while (line != null);
		if (b.length() == 0)
			return null;

		return b.toString();
	}

	/**
	 * calculate distance on spheroid
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static double distance(ChouetteLocalizedObject obj1, ChouetteLocalizedObject obj2) {
		double long1rad = Math.toRadians(obj1.getLongitude().doubleValue());
		double lat1rad = Math.toRadians(obj1.getLatitude().doubleValue());
		double long2rad = Math.toRadians(obj2.getLongitude().doubleValue());
		double lat2rad = Math.toRadians(obj2.getLatitude().doubleValue());

		double alpha = Math.cos(lat1rad) * Math.cos(lat2rad) * Math.cos(long2rad - long1rad)
				+ Math.sin(lat1rad) * Math.sin(lat2rad);

		double distance = 6378. * Math.acos(alpha);

		return distance * 1000.;
	}

	public static long diffTime(Time first, Time last) {
		if (first == null || last == null)
			return Long.MIN_VALUE; // TODO
		long diff = last.getTime() / 1000L - first.getTime() / 1000L;
		if (diff < 0)
			diff += 86400L; // step upon midnight : add one day in seconds
		return diff;
	}

	/**
	 * check and return details for checkpoint
	 * 
	 * @param report
	 * @param key
	 * @param detailSize
	 *            (negative for not checked
	 * @return
	 */
	protected List<CheckPointErrorReport> checkReportForTest(ValidationReport report, String key, int detailSize) {
		Assert.assertFalse(report.getCheckPoints().isEmpty(), " report must have items");
		Assert.assertNotNull(report.findCheckPointReportByName(key), " report must have 1 item on key " + key);
		CheckPointReport checkPointReport = report.findCheckPointReportByName(key);
		if (detailSize >= 0)
			Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), detailSize,
					" checkpoint must have " + detailSize + " detail");

		List<CheckPointErrorReport> details = new ArrayList<>();
		for (Integer errorkey : checkPointReport.getCheckPointErrorKeys()) {
			details.add(report.getCheckPointErrors().get(errorkey));
		}
		return details;
	}

	protected ValidationParameters loadFullParameters() throws Exception {
		// String filename = "fullparameterset.json";
		// File f = new File(path,filename);
		//
		// return (ValidationParameters) JSONUtil.fromJSON(f.toPath(),
		// ValidationParameters.class);
		ValidationParameters ret = loadParameters();

		ret.setCheckNetwork(0);
		ret.setNetwork(new NetworkParameters());
		ret.getNetwork().setObjectId(new FieldParameters());
		ret.getNetwork().setName(new FieldParameters());
		ret.getNetwork().setRegistrationNumber(new FieldParameters());

		ret.setCheckCompany(0);
		ret.setCompany(new CompanyParameters());
		ret.getCompany().setObjectId(new FieldParameters());
		ret.getCompany().setName(new FieldParameters());
		ret.getCompany().setRegistrationNumber(new FieldParameters());

		ret.setCheckGroupOfLine(0);
		ret.setGroupOfLine(new GroupOfLineParameters());
		ret.getGroupOfLine().setObjectId(new FieldParameters());
		ret.getGroupOfLine().setName(new FieldParameters());
		ret.getGroupOfLine().setRegistrationNumber(new FieldParameters());

		ret.setCheckStopArea(0);
		ret.setStopArea(new StopAreaParameters());
		ret.getStopArea().setObjectId(new FieldParameters());
		ret.getStopArea().setName(new FieldParameters());
		ret.getStopArea().setRegistrationNumber(new FieldParameters());
		ret.getStopArea().setCityName(new FieldParameters());
		ret.getStopArea().setCountryCode(new FieldParameters());
		ret.getStopArea().setZipCode(new FieldParameters());

		ret.setCheckAccessPoint(0);
		ret.setAccessPoint(new AccessPointParameters());
		ret.getAccessPoint().setObjectId(new FieldParameters());
		ret.getAccessPoint().setName(new FieldParameters());
		ret.getAccessPoint().setCityName(new FieldParameters());
		ret.getAccessPoint().setCountryCode(new FieldParameters());

		ret.setCheckAccessLink(0);
		ret.setAccessLink(new AccessLinkParameters());
		ret.getAccessLink().setObjectId(new FieldParameters());
		ret.getAccessLink().setName(new FieldParameters());
		ret.getAccessLink().setLinkDistance(new FieldParameters());
		ret.getAccessLink().setDefaultDuration(new FieldParameters());

		ret.setCheckConnectionLink(0);
		ret.setConnectionLink(new ConnectionLinkParameters());
		ret.getConnectionLink().setObjectId(new FieldParameters());
		ret.getConnectionLink().setName(new FieldParameters());
		ret.getConnectionLink().setLinkDistance(new FieldParameters());
		ret.getConnectionLink().setDefaultDuration(new FieldParameters());

		ret.setCheckTimetable(0);
		ret.setTimetable(new TimetableParameters());
		ret.getTimetable().setObjectId(new FieldParameters());
		ret.getTimetable().setComment(new FieldParameters());
		ret.getTimetable().setVersion(new FieldParameters());

		ret.setCheckLine(0);
		ret.setLine(new LineParameters());
		ret.getLine().setObjectId(new FieldParameters());
		ret.getLine().setName(new FieldParameters());
		ret.getLine().setRegistrationNumber(new FieldParameters());
		ret.getLine().setNumber(new FieldParameters());
		ret.getLine().setPublishedName(new FieldParameters());

		ret.setCheckRoute(0);
		ret.setRoute(new RouteParameters());
		ret.getRoute().setObjectId(new FieldParameters());
		ret.getRoute().setName(new FieldParameters());
		ret.getRoute().setNumber(new FieldParameters());
		ret.getRoute().setPublishedName(new FieldParameters());

		ret.setCheckJourneyPattern(0);
		ret.setJourneyPattern(new JourneyPatternParameters());
		ret.getJourneyPattern().setObjectId(new FieldParameters());
		ret.getJourneyPattern().setName(new FieldParameters());
		ret.getJourneyPattern().setRegistrationNumber(new FieldParameters());
		ret.getJourneyPattern().setPublishedName(new FieldParameters());

		ret.setCheckVehicleJourney(0);
		ret.setVehicleJourney(new VehicleJourneyParameters());
		ret.getVehicleJourney().setObjectId(new FieldParameters());
		ret.getVehicleJourney().setPublishedJourneyName(new FieldParameters());
		ret.getVehicleJourney().setPublishedJourneyIdentifier(new FieldParameters());
		ret.getVehicleJourney().setNumber(new FieldParameters());
		return ret;

	}

	protected ValidationParameters loadParameters() throws Exception {
		// String filename = "parameterset.json";
		// File f = new File(path,filename);
		// byte[] bytes = Files.readAllBytes(f.toPath());
		// String text = new String(bytes, "UTF-8");
		// return (ValidationParameters) JSONUtil.fromJSON(text,
		// ValidationParameters.class);
		ValidationParameters ret = new ValidationParameters();
		ret.setStopAreasArea("[[-5.2,42.25],[-5.2,51.1],[8.23,51.1],[8.23,42.25],[-5.2,42.25]]");
		ret.setInterStopAreaDistanceMin(20);
		ret.setParentStopAreaDistanceMax(350);
		ret.setInterAccessPointDistanceMin(20);
		ret.setInterConnectionLinkDistanceMax(800);
		ret.setWalkDefaultSpeedMax(5);
		ret.setWalkOccasionalTravellerSpeedMax(4);
		ret.setWalkFrequentTravellerSpeedMax(6);
		ret.setWalkMobilityRestrictedTravellerSpeedMax(2);
		ret.setInterAccessLinkDistanceMax(300);
		ret.setInterStopDurationMax(180);
		ret.setFacilityStopAreaDistanceMax(300);
		ret.setCheckAllowedTransportModes(0);
		ret.setCheckLinesInGroups(0);
		ret.setCheckLineRoutes(0);
		ret.setCheckStopParent(0);
		ret.setCheckConnectionLinkOnPhysical(0);

		ret.setModeCoach(new TransportModeParameters(1, 500, 10000, 90, 40, 20, 20));
		ret.setModeAir(new TransportModeParameters(1, 200, 10000, 800, 700, 60, 1000));
		ret.setModeWaterborne(new TransportModeParameters(1, 200, 10000, 40, 5, 60, 20));
		ret.setModeBus(new TransportModeParameters(1, 100, 40000, 1000, 5, 2000, 20));
		ret.setModeFerry(new TransportModeParameters(1, 200, 10000, 40, 5, 60, 100));
		ret.setModeWalk(new TransportModeParameters(1, 1, 10000, 6, 1, 10, 20));
		ret.setModeMetro(new TransportModeParameters(1, 300, 20000, 500, 25, 2000, 100));
		ret.setModeShuttle(new TransportModeParameters(1, 500, 10000, 80, 20, 10, 20));
		ret.setModeRapidTransit(new TransportModeParameters(1, 2000, 500000, 300, 20, 60, 20));
		ret.setModeTaxi(new TransportModeParameters(1, 500, 300000, 130, 20, 60, 20));
		ret.setModeLocalTrain(new TransportModeParameters(1, 2000, 500000, 300, 20, 60, 20));
		ret.setModeTrain(new TransportModeParameters(1, 2000, 500000, 300, 20, 60, 100));
		ret.setModeLongDistanceTrain(new TransportModeParameters(1, 2000, 500000, 300, 20, 60, 100));
		ret.setModeTramway(new TransportModeParameters(1, 300, 2000, 50, 20, 30, 20));
		ret.setModeTrolleybus(new TransportModeParameters(1, 300, 2000, 50, 20, 30, 20));
		ret.setModePrivateVehicle(new TransportModeParameters(1, 500, 300000, 130, 20, 60, 20));
		ret.setModeBicycle(new TransportModeParameters(1, 300, 30000, 40, 10, 10, 20));
		ret.setModeOther(new TransportModeParameters(1, 300, 30000, 40, 10, 10, 20));

		return ret;
	}

}

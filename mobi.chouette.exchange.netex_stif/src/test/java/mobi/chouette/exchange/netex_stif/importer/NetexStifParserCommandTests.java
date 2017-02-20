package mobi.chouette.exchange.netex_stif.importer;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netex_stif.Constant;
import mobi.chouette.exchange.netex_stif.JobDataTest;
import mobi.chouette.exchange.netex_stif.model.NetexStifObjectFactory;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import mobi.chouette.persistence.hibernate.ContextHolder;

public class NetexStifParserCommandTests implements Constant, ReportConstant {

	private static final String path = "src/test/data/";

	protected static InitialContext initialContext;

	public void init() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Locale.setDefault(Locale.ENGLISH);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				e.printStackTrace();
			}

		}
	}

	protected Context initImportContext() {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(VALIDATION_REPORT, new ValidationReport());
		NetexStifImportParameters configuration = new NetexStifImportParameters();
		context.put(CONFIGURATION, configuration);
		context.put(REFERENTIAL, new Referential());
		context.put(NETEX_STIF_OBJECT_FACTORY, new NetexStifObjectFactory());
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setNoSave(true);
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
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
		test.setReferential("chouette_gui");
		test.setAction(IMPORTER);
		test.setType("netex_stif");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}

	@Test(groups = { "Nominal" }, description = "offre")
	public void verifiyOfferParser() throws Exception {
		Context context = initImportContext();

		NetexStifParserCommand parser = (NetexStifParserCommand) CommandFactory.create(initialContext,
				NetexStifParserCommand.class.getName());
		File f = new File(path, "offre.xml");
		parser.setFileURL("file://" + f.getAbsolutePath());
		parser.execute(context);

		/// Assert.assertXXX();
	}
	
	@Test(groups = { "Nominal" }, description = "commun")
	public void verifiyCommunParser() throws Exception {
		Context context = initImportContext();

		NetexStifParserCommand parser = (NetexStifParserCommand) CommandFactory.create(initialContext,
				NetexStifParserCommand.class.getName());
		File f = new File(path, "commun.xml");
		parser.setFileURL("file://" + f.getAbsolutePath());
		parser.execute(context);
		NetexStifObjectFactory factory = (NetexStifObjectFactory) context.get(NETEX_STIF_OBJECT_FACTORY);
		
		assertNotice(factory, "CITYWAY:Notice:1:LOC", "Notice 1", "1");
		assertNotice(factory, "CITYWAY:Notice:2:LOC", "Notice 2", "2");
		assertNotice(factory, "CITYWAY:Notice:3:LOC", "Notice 3", "3");
		
	}
	
	private void assertNotice (NetexStifObjectFactory nsof, String id, String text, String publicCode){
		Footnote fn = nsof.getFootnote(id);
		Assert.assertEquals(fn.getLabel(), text);
		Assert.assertEquals(fn.getCode(), publicCode);
			
	}

	@Test(groups = { "Nominal" }, description = "calendrier")
	public void verifiyCalendrierParser() throws Exception {
		Context context = initImportContext();

		NetexStifParserCommand parser = (NetexStifParserCommand) CommandFactory.create(initialContext,
				NetexStifParserCommand.class.getName());
		File f = new File(path, "calendrier.xml");
		parser.setFileURL("file://" + f.getAbsolutePath());
		parser.execute(context);

		/// Assert.assertXXX();
	}
}

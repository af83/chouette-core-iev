package mobi.chouette.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.JobData;

public class JobServiceManagerTest extends Arquillian {

	@EJB
	JobServiceManager jobServiceManager;

	@Deployment
	public static EnterpriseArchive createDeployment() {

		try {
			EnterpriseArchive result;
			File[] files = Maven.resolver().loadPomFromFile("pom.xml")
					.resolve("mobi.chouette:mobi.chouette.boiv.service").withTransitivity().asFile();
			List<File> jars = new ArrayList<>();
			List<JavaArchive> modules = new ArrayList<>();
			for (File file : files) {
				if (file.getName().startsWith("mobi.chouette.exchange")
						|| file.getName().startsWith("mobi.chouette.boiv.service")
						|| file.getName().startsWith("mobi.chouette.dao")) {
					String name = file.getName().split("\\-")[0] + ".jar";
					JavaArchive archive = ShrinkWrap.create(ZipImporter.class, name).importFrom(file)
							.as(JavaArchive.class);
					modules.add(archive);
				} else {
					jars.add(file);
				}
			}
			File[] filesDao = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.dao")
					.withTransitivity().asFile();
			if (filesDao.length == 0) {
				throw new NullPointerException("no dao");
			}
			for (File file : filesDao) {
				if (file.getName().startsWith("mobi.chouette.dao")) {
					String name = file.getName().split("\\-")[0] + ".jar";

					JavaArchive archive = ShrinkWrap.create(ZipImporter.class, name).importFrom(file)
							.as(JavaArchive.class);
					modules.add(archive);
					if (!modules.contains(archive))
						modules.add(archive);
				} else {
					if (!jars.contains(file))
						jars.add(file);
				}
			}

			final WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war")
					// .addAsResource("test-persistence.xml",
					// "META-INF/persistence.xml")
					.addAsWebInfResource("postgres-ds.xml").addClass(DummyChecker.class)
					.addClass(JobServiceManagerTest.class);

			result = ShrinkWrap.create(EnterpriseArchive.class, "test.ear").addAsLibraries(jars.toArray(new File[0]))
					.addAsModules(modules.toArray(new JavaArchive[0])).addAsModule(testWar)
					.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
			return result;
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			Throwable c = ex.getCause();
			while (c != null) {
				c.printStackTrace();
				c = c.getCause();
			}
			throw ex;
		}
	}

	@Test(groups = { "JobServiceManager" }, description = "Check wrong Id")
	public void createWrongJobId() {
		String action = JobData.ACTION.importer.name();
		try {
			jobServiceManager.create(action, -1L);
		} catch (RequestServiceException e) {
			Assert.assertEquals(e.getCode(), ServiceExceptionCode.INVALID_REQUEST.name(), "code expected");
			Assert.assertEquals(e.getRequestCode(), RequestExceptionCode.UNKNOWN_JOB.name(), "request code expected");
			return;

		} catch (Exception e) {
			Assert.assertTrue(false, "RequestServiceException required");
		}
		Assert.assertTrue(false, "exception required");

	}

	@Test(groups = { "JobServiceManager" }, description = "Check wrong action")
	public void createWrongJobAction() {
		String action = "action";
		try {
			jobServiceManager.create(action, 1L);
		} catch (RequestServiceException e) {
			Assert.assertEquals(e.getCode(), ServiceExceptionCode.INVALID_REQUEST.name(), "code expected");
			Assert.assertEquals(e.getRequestCode(), RequestExceptionCode.UNKNOWN_ACTION.name(),
					"request code expected");
			return;

		} catch (Exception e) {
			Assert.assertTrue(false, "RequestServiceException required," + e.getClass().getName() + " received");
		}
		Assert.assertTrue(false, "exception required");

	}

}

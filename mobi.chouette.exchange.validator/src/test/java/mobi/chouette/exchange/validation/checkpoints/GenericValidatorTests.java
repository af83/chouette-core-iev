package mobi.chouette.exchange.validation.checkpoints;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_STATE;
import mobi.chouette.exchange.report.ActionReporter.OBJECT_TYPE;
import mobi.chouette.exchange.validator.ValidateParameters;
import mobi.chouette.exchange.validator.checkpoints.CheckpointParameters;
import mobi.chouette.exchange.validator.checkpoints.GenericCheckpointParameters;
import mobi.chouette.exchange.validator.checkpoints.RouteValidator;
import mobi.chouette.model.LineLite;
import mobi.chouette.model.Route;
import mobi.chouette.model.util.Referential;

@Log4j
public class GenericValidatorTests extends AbstractTestValidation {

	@EJB
	RouteDAO dao;

	@Deployment
	public static EnterpriseArchive createDeployment() {

		return buildDeployment(GenericValidatorTests.class);
	}

	/**
	 * @throws Exception
	 */
	@Test(groups = { "route" }, description = "3_Generique_1", priority = 1)
	public void verifyTest_3_Route_1() throws Exception {
		log.info(Color.CYAN + " check " + L3_Generique_1 + Color.NORMAL);
		initSchema();
		Context context = initValidatorContext();
		loadSharedData(context);
		utx.begin();
		try {
			em.joinTransaction();
			Referential ref = (Referential) context.get(REFERENTIAL);
			Route route = dao.find(Long.valueOf(2));
			Assert.assertNotNull(route, "route id 2 not found");
			LineLite line = ref.findLine(route.getLineId());
			route.setLineLite(line);
			ActionReporter reporter = ActionReporter.Factory.getInstance();
			reporter.addObjectReport(context, line.getObjectId(), OBJECT_TYPE.LINE, line.getName(), OBJECT_STATE.OK,
					null);
			RouteValidator validator = new RouteValidator();
			ValidateParameters parameters = (ValidateParameters) context.get(CONFIGURATION);
			Collection<CheckpointParameters> checkPoints = new ArrayList<>();
			CheckpointParameters checkPoint = new GenericCheckpointParameters(L3_Generique_1, false, "^[\\w ]+$", null, "Route", "name");
			checkPoints.add(checkPoint);
			parameters.getControlParameters().getGlobalCheckPoints().put(L3_Generique_1, checkPoints);
			String transportMode = line.getTransportModeName();
			validator.validate(context, route, parameters, transportMode);

			checkNoReports(context, line.getObjectId());
			route.setName("ça va pas!");
			validator.validate(context, route, parameters, transportMode);
			checkReports(context, line.getObjectId(), L3_Generique_1, "3_generique_1", route.getName(), OBJECT_STATE.WARNING);
		} finally {
			utx.rollback();
		}

	}


}

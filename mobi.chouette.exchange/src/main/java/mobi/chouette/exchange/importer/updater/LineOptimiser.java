package mobi.chouette.exchange.importer.updater;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.dao.AccessLinkDAO;
import mobi.chouette.dao.AccessPointDAO;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.ConnectionLinkDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.JourneyPatternDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.NetworkDAO;
import mobi.chouette.dao.RouteDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.StopPointDAO;
import mobi.chouette.dao.TimebandDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.dao.VehicleJourneyDAO;
import mobi.chouette.exchange.parameters.AbstractParameter;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timeband;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Stateless
public class LineOptimiser {

	@EJB
	private ConnectionLinkDAO connectionLinkDAO;

	@EJB
	private AccessLinkDAO accessLinkDAO;

	@EJB
	private AccessPointDAO accessPointDAO;

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB
	private TimetableDAO timetableDAO;

	@EJB
	private NetworkDAO ptNetworkDAO;

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;

	@EJB
	private LineDAO lineDAO;

	@EJB
	private RouteDAO routeDAO;

	@EJB
	private JourneyPatternDAO journeyPatternDAO;

	@EJB
	private VehicleJourneyDAO vehicleJourneyDAO;

	@EJB
	private StopPointDAO stopPointDAO;

	@EJB
	private TimebandDAO timebandDAO;

	public void initialize(AbstractParameter params,Referential cache, Referential referential) {

//		Monitor monitor = MonitorFactory.start("LineOptimiser");
		initializeStopArea(params,cache, referential.getStopAreas().values());

		initializeConnectionLink(cache, referential.getConnectionLinks().values());
		initializeAccessLink(cache, referential.getAccessLinks().values());
		initializeAccessPoint(cache, referential.getAccessPoints().values());

		initializeTimetable(cache, referential.getTimetables().values());
		initializePTNetwork(params,cache, referential.getPtNetworks().values());
		initializeCompany(params,cache, referential.getCompanies().values());
		initializeGroupOfLine(params,cache, referential.getGroupOfLines().values());

		initializeLine(params,cache, referential.getLines().values());
		initializeRoute(cache, referential.getRoutes().values());
		initializeStopPoint(cache, referential.getStopPoints().values());
		initializeJourneyPattern(cache, referential.getJourneyPatterns().values());
		initializeVehicleJourney(cache, referential.getVehicleJourneys().values());

		initializeTimeband(cache, referential.getTimebands().values());
//		monitor.stop();
	}

	private void initializeStopArea(AbstractParameter params, Referential cache, Collection<StopArea> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<StopArea> objects = stopAreaDAO.findByObjectId(params.getStopAreaReferentialId(),objectIds);
			for (StopArea object : objects) {
				cache.getStopAreas().put(object.getObjectId(), object);
			}

			// check if stoparea really exists
			for (StopArea item : list) {
				if (!cache.getStopAreas().containsKey(item.getObjectId())) {
					ObjectFactory.getStopArea(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeConnectionLink(Referential cache, Collection<ConnectionLink> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<ConnectionLink> objects = connectionLinkDAO.findByObjectId(objectIds);
			for (ConnectionLink object : objects) {
				cache.getConnectionLinks().put(object.getObjectId(), object);
			}

			for (ConnectionLink item : list) {
				if (!cache.getConnectionLinks().containsKey(item.getObjectId())) {
					ObjectFactory.getConnectionLink(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeAccessLink(Referential cache, Collection<AccessLink> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<AccessLink> objects = accessLinkDAO.findByObjectId(objectIds);
			for (AccessLink object : objects) {
				cache.getAccessLinks().put(object.getObjectId(), object);
			}

			for (AccessLink item : list) {
				if (!cache.getAccessLinks().containsKey(item.getObjectId())) {
					ObjectFactory.getAccessLink(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeAccessPoint(Referential cache, Collection<AccessPoint> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<AccessPoint> objects = accessPointDAO.findByObjectId(objectIds);
			for (AccessPoint object : objects) {
				cache.getAccessPoints().put(object.getObjectId(), object);
			}

			for (AccessPoint item : list) {
				if (!cache.getAccessPoints().containsKey(item.getObjectId())) {
					ObjectFactory.getAccessPoint(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeTimetable(Referential cache, Collection<Timetable> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<Timetable> objects = timetableDAO.findByObjectId(objectIds);
			for (Timetable object : objects) {
				cache.getTimetables().put(object.getObjectId(), object);
			}

			for (Timetable item : list) {
				if (!cache.getTimetables().containsKey(item.getObjectId())) {
					ObjectFactory.getTimetable(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializePTNetwork(AbstractParameter params,Referential cache, Collection<Network> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<Network> objects = ptNetworkDAO.findByObjectId(params.getLineReferentialId(),objectIds);
			for (Network object : objects) {
				cache.getPtNetworks().put(object.getObjectId(), object);
			}

			for (Network item : list) {
				if (!cache.getPtNetworks().containsKey(item.getObjectId())) {
					ObjectFactory.getPTNetwork(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeCompany(AbstractParameter params, Referential cache, Collection<Company> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<Company> objects = companyDAO.findByObjectId(params.getLineReferentialId(),objectIds);
			for (Company object : objects) {
				cache.getCompanies().put(object.getObjectId(), object);
			}

			for (Company item : list) {
				if (!cache.getCompanies().containsKey(item.getObjectId())) {
					ObjectFactory.getCompany(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeGroupOfLine(AbstractParameter params,Referential cache, Collection<GroupOfLine> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<GroupOfLine> objects = groupOfLineDAO.findByObjectId(params.getLineReferentialId(),objectIds);
			for (GroupOfLine object : objects) {
				cache.getGroupOfLines().put(object.getObjectId(), object);
			}

			for (GroupOfLine item : list) {
				if (!cache.getGroupOfLines().containsKey(item.getObjectId())) {
					ObjectFactory.getGroupOfLine(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeLine(AbstractParameter params,Referential cache, Collection<Line> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<Line> objects = lineDAO.findByObjectId(params.getLineReferentialId(),objectIds);
			for (Line object : objects) {
				cache.getLines().put(object.getObjectId(), object);
			}

			for (Line item : list) {
				if (!cache.getLines().containsKey(item.getObjectId())) {
					ObjectFactory.getLine(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeRoute(Referential cache, Collection<Route> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<Route> objects = routeDAO.findByObjectId(objectIds);
			for (Route object : objects) {
				cache.getRoutes().put(object.getObjectId(), object);
			}

			for (Route item : list) {
				if (!cache.getRoutes().containsKey(item.getObjectId())) {
					ObjectFactory.getRoute(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeStopPoint(Referential cache, Collection<StopPoint> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<StopPoint> objects = stopPointDAO.findByObjectId(objectIds);
			for (StopPoint object : objects) {
				cache.getStopPoints().put(object.getObjectId(), object);
			}

			for (StopPoint item : list) {
				if (!cache.getStopPoints().containsKey(item.getObjectId())) {
					ObjectFactory.getStopPoint(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeJourneyPattern(Referential cache, Collection<JourneyPattern> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<JourneyPattern> objects = journeyPatternDAO.findByObjectId(objectIds);
			for (JourneyPattern object : objects) {
				cache.getJourneyPatterns().put(object.getObjectId(), object);
			}

			for (JourneyPattern item : list) {
				if (!cache.getJourneyPatterns().containsKey(item.getObjectId())) {
					ObjectFactory.getJourneyPattern(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeVehicleJourney(Referential cache, Collection<VehicleJourney> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<VehicleJourney> objects = vehicleJourneyDAO.findByObjectId(objectIds);
			for (VehicleJourney object : objects) {
				cache.getVehicleJourneys().put(object.getObjectId(), object);
			}

			for (VehicleJourney item : list) {
				if (!cache.getVehicleJourneys().containsKey(item.getObjectId())) {
					ObjectFactory.getVehicleJourney(cache, item.getObjectId());
				}
			}
		}
	}

	private void initializeTimeband(Referential cache, Collection<Timeband> list) {
		if (list != null && !list.isEmpty()) {
			Collection<String> objectIds = UpdaterUtils.getObjectIds(list);
			List<Timeband> objects = timebandDAO.findByObjectId(objectIds);
			for (Timeband object : objects) {
				cache.getTimebands().put(object.getObjectId(), object);
			}

			for (Timeband item : list) {
				if (!cache.getTimebands().containsKey(item.getObjectId())) {
					ObjectFactory.getTimeband(cache, item.getObjectId());
				}
			}
		}
	}
}

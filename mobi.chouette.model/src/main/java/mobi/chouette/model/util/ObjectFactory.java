package mobi.chouette.model.util;

import java.util.Calendar;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.LineNotice;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.RoutingConstraint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timeband;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.VehicleJourneyAtStop;

public class ObjectFactory {

	public static AccessLink getAccessLink(Referential referential,
			String objectId) {
		AccessLink result = referential.getSharedAccessLinks().get(objectId);
		if (result == null) {
			result = new AccessLink();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getSharedAccessLinks().put(objectId, result);
		}
		if (!referential.getAccessLinks().containsKey(objectId)) {
			referential.getAccessLinks().put(objectId, result);
		}

		return result;
	}


	public static AccessPoint getAccessPoint(Referential referential,
			String objectId) {
		AccessPoint result = referential.getSharedAccessPoints().get(objectId);
		if (result == null) {
			result = new AccessPoint();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getSharedAccessPoints().put(objectId, result);
		}
		if (!referential.getAccessPoints().containsKey(objectId)) {
			referential.getAccessPoints().put(objectId, result);
		}

		return result;
	}

	public static Timetable getTimetable(Referential referential,	String objectId) {
		Timetable result = referential.getSharedTimetables().get(objectId);
		if (result == null) {
			result = new Timetable();
			result.setDetached(true);
			result.setObjectId(objectId);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getSharedTimetables().put(objectId, result);
		}
		if (!referential.getTimetables().containsKey(objectId)) {
			referential.getTimetables().put(objectId, result);
		}

		return result;
	}

	public static VehicleJourneyAtStop getVehicleJourneyAtStop() {
		VehicleJourneyAtStop result = new VehicleJourneyAtStop();
		return result;
	}

	public static Network getPTNetwork(Referential referential, String objectId) {
		Network result = referential.getSharedPTNetworks().get(objectId);
		if (result == null) {
			result = new Network();
			result.setDetached(true);
			result.setObjectId(objectId);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getSharedPTNetworks().put(objectId, result);
		}
		if (!referential.getPtNetworks().containsKey(objectId)) {
			referential.getPtNetworks().put(objectId, result);
		}

		return result;
	}

	public static Company getCompany(Referential referential, String objectId) {
		Company result = referential.getSharedCompanies().get(objectId);
		if (result == null) {
			result = new Company();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getSharedCompanies().put(objectId, result);
		}
		if (!referential.getCompanies().containsKey(objectId)) {
			referential.getCompanies().put(objectId, result);
		}

		return result;
	}

	public static LineNotice getLineNotice (Referential referential, String objectId) {
		LineNotice result = referential.getSharedLineNotices().get(objectId);
		if (result == null) {
			result = new LineNotice();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getSharedLineNotices().put(objectId, result);
		}
		if (!referential.getSharedLineNotices().containsKey(objectId)) {
			referential.getSharedLineNotices().put(objectId, result);
		}

		return result;
	}

	public static Route getRoute(Referential referential, String objectId) {
		Route result = referential.getRoutes().get(objectId);
		if (result == null) {
			result = new Route();
			result.setDetached(true);
			result.setObjectId(objectId);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getRoutes().put(objectId, result);
		}
		return result;
	}

	public static RoutingConstraint getRoutingConstraint(Referential referential, String objectId) {
		RoutingConstraint result = referential.getRoutingConstraints().get(objectId);
		if (result == null) {
			result = new RoutingConstraint();
			result.setDetached(true);
			result.setObjectId(objectId);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getRoutingConstraints().put(objectId, result);
		}
		return result;
	}

	public static Line getLine(Referential referential, String objectId) {
		Line result = referential.getLines().get(objectId);
		if (result == null) {
			result = new Line();
			result.setDetached(true);
			result.setObjectId(objectId);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getLines().put(objectId, result);
		}
		return result;
	}

	public static JourneyPattern getJourneyPattern(Referential referential,
			String objectId) {
		JourneyPattern result = referential.getJourneyPatterns().get(objectId);
		if (result == null) {
			result = new JourneyPattern();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getJourneyPatterns().put(objectId, result);
		}
		return result;
	}

	public static ConnectionLink getConnectionLink(Referential referential,
			String objectId) {
		ConnectionLink result = referential.getSharedConnectionLinks().get(
				objectId);
		if (result == null) {
			result = new ConnectionLink();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getSharedConnectionLinks().put(objectId, result);
		}
		if (!referential.getConnectionLinks().containsKey(objectId)) {
			referential.getConnectionLinks().put(objectId, result);
		}

		return result;
	}

	public static StopArea getStopArea(Referential referential, String objectId) {
		StopArea result = referential.getSharedStopAreas().get(objectId);
		if (result == null) {
			result = new StopArea();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getSharedStopAreas().put(objectId, result);
		}
		if (!referential.getStopAreas().containsKey(objectId)) {
			referential.getStopAreas().put(objectId, result);
		}

		return result;
	}

	public static GroupOfLine getGroupOfLine(Referential referential,
			String objectId) {
		GroupOfLine result = referential.getSharedGroupOfLines().get(objectId);
		if (result == null) {
			result = new GroupOfLine();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getSharedGroupOfLines().put(objectId, result);
		}
		if (!referential.getGroupOfLines().containsKey(objectId)) {
			referential.getGroupOfLines().put(objectId, result);
		}

		return result;
	}

	public static StopPoint getStopPoint(Referential referential,
			String objectId) {
		StopPoint result = referential.getStopPoints().get(objectId);
		if (result == null) {
			result = new StopPoint();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());

			referential.getStopPoints().put(objectId, result);
		}
		return result;
	}

	public static VehicleJourney getVehicleJourney(Referential referential,
			String objectId) {
		VehicleJourney result = referential.getVehicleJourneys().get(objectId);
		if (result == null) {
			result = new VehicleJourney();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());
			referential.getVehicleJourneys().put(objectId, result);
		}
		return result;
	}

	public static Timeband getTimeband(Referential referential, String objectId) {
		Timeband result = referential.getTimebands().get(objectId);
		if (result == null) {
			result = new Timeband();
			result.setObjectId(objectId);
			result.setDetached(true);
			result.setCreationTime(Calendar.getInstance().getTime());
			referential.getTimebands().put(objectId, result);
		}
		return result;
	}



}

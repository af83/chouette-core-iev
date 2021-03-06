package mobi.chouette.exchange.validation.report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mobi.chouette.exchange.report.AbstractReport;
import mobi.chouette.exchange.validation.report.DataLocation.Path;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.ChouetteIdentifiedObject;
import mobi.chouette.model.Company;
import mobi.chouette.model.CompanyLite;
import mobi.chouette.model.LineNotice;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.Footnote;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.LineLite;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.RoutingConstraint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopAreaLite;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.util.NamingUtil;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "file", "objectId", "name", "objectRefs", "attribute" })
public class Location extends AbstractReport {

	private static final char PATH_SEP = '/';

	@XmlElement(name = "file")
	private FileLocation file;

	@XmlElement(name = "attribute")
	private String attribute;
	//
	@XmlElement(name = "objectid")
	private String objectId = "";

	@XmlElement(name = "label")
	private String name = "";

	@XmlElement(name = "object_path")
	private List<ObjectReference> objectRefs = new ArrayList<>();

	public Location(DataLocation dl) {
		if (dl.getObject() != null && dl.getObject().getId() != null) {
			init(dl.getObject());
		} else {
			this.name = dl.getName();
			this.objectId = dl.getObjectId();
			if (!dl.getPath().isEmpty()) {
				for (Path path : dl.getPath()) {
					if (ObjectReference.isEligible(path.getObjectClass(), path.getObjectId()))
						objectRefs.add(new ObjectReference(path.getObjectClass(), path.getObjectId()));
				}
			}
		}
		if (dl.getFilename() != null) {
			this.file = new FileLocation(dl);
		}
		this.attribute = dl.getAttribute();
	}

	public Location(String fileName) {
		this.file = new FileLocation(fileName);
	}

	public Location(String fileName, String locationName) {
		this.file = new FileLocation(fileName);
		this.name = locationName;
	}

	public Location(String fileName, String locationName, int lineNumber, String objectId) {
		this.file = new FileLocation(fileName, lineNumber, -1);
		this.objectId = objectId;
		this.name = locationName;
	}

	public Location(String fileName, String locationName, int lineNumber, int columnNumber, String objectId) {
		this.file = new FileLocation(fileName, lineNumber, columnNumber);
		this.objectId = objectId;
		this.name = locationName;
	}

	public Location(String fileName, String locationName, int lineNumber) {
		this.file = new FileLocation(fileName, lineNumber, -1);
		this.name = locationName;
	}

	public Location(String fileName, int lineNumber, int columnNumber) {
		this.file = new FileLocation(fileName, lineNumber, columnNumber);
	}

	public Location(String fileName, int lineNumber, int columnNumber, String objectId) {
		this.file = new FileLocation(fileName, lineNumber, columnNumber);
		this.objectId = objectId;
	}

	public Location(ChouetteIdentifiedObject chouetteObject) {
		init(chouetteObject);
	}

	public String getGuiPath(Long referentialId) {
		StringBuilder b = new StringBuilder();
		if (!objectRefs.isEmpty()) {
			if (referentialId != null) {
				b.append(PATH_SEP);
				b.append(ObjectReference.TYPE.REFERENTIAL.getGuiValue());
				b.append(PATH_SEP);
				b.append(referentialId);
			}
			for (int i = objectRefs.size() - 1; i >= 0; i--) {
				ObjectReference ref = objectRefs.get(i);
				if (!ref.getType().getGuiValue().isEmpty()) {
					b.append(PATH_SEP);
					b.append(ref.getType().getGuiValue());
					b.append(PATH_SEP);
					b.append(ref.getId());
				}
				else if (ref.getType().equals(ObjectReference.TYPE.JOURNEY_PATTERN) || ref.getType().equals(ObjectReference.TYPE.VEHICLE_JOURNEY))
				{
					b.append(PATH_SEP);
					b.append(ref.getType().getGuiShortCut());
				}
			}
		}
		return b.toString();
	}

	private void init(ChouetteIdentifiedObject chouetteObject) {
		this.objectId = chouetteObject.getObjectId();
		this.name = buildName(chouetteObject);
		if (chouetteObject instanceof VehicleJourney) {
			VehicleJourney object = (VehicleJourney) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			// objectRefs.add(new ObjectReference(object.getJourneyPattern()));
			objectRefs.add(new ObjectReference(object.getJourneyPattern().getRoute()));
			if (object.getJourneyPattern().getRoute().getLine() != null)
				objectRefs.add(new ObjectReference(object.getJourneyPattern().getRoute().getLine()));
			if (object.getJourneyPattern().getRoute().getLineLite() != null)
				objectRefs.add(new ObjectReference(object.getJourneyPattern().getRoute().getLineLite()));
		} else if (chouetteObject instanceof JourneyPattern) {
			JourneyPattern object = (JourneyPattern) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getRoute()));
			if (object.getRoute().getLine() != null)
				objectRefs.add(new ObjectReference(object.getRoute().getLine()));
			if (object.getRoute().getLineLite() != null)
				objectRefs.add(new ObjectReference(object.getRoute().getLineLite()));
		} else if (chouetteObject instanceof Route) {
			Route object = (Route) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			if (object.getLine() != null)
				objectRefs.add(new ObjectReference(object.getLine()));
			if (object.getLineLite() != null)
				objectRefs.add(new ObjectReference(object.getLineLite()));
		} else if (chouetteObject instanceof Footnote) {
			Footnote object = (Footnote) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			if (object.getLine() != null)
				objectRefs.add(new ObjectReference(object.getLine()));
			if (object.getLineLite() != null)
				objectRefs.add(new ObjectReference(object.getLineLite()));
		} else if (chouetteObject instanceof RoutingConstraint) {
			RoutingConstraint object = (RoutingConstraint) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			// SKIP route in path for gui
			if (object.getRoute().getLine() != null)
				objectRefs.add(new ObjectReference(object.getRoute().getLine()));
			if (object.getRoute().getLineLite() != null)
				objectRefs.add(new ObjectReference(object.getRoute().getLineLite()));
		} else if (chouetteObject instanceof Line) {
			Line object = (Line) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof LineLite) {
			LineLite object = (LineLite) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof AccessLink) {
			AccessLink object = (AccessLink) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getAccessPoint()));
			objectRefs.add(new ObjectReference(object.getAccessPoint().getContainedIn()));
		} else if (chouetteObject instanceof AccessPoint) {
			AccessPoint object = (AccessPoint) chouetteObject;
			objectRefs.add(new ObjectReference(object));
			objectRefs.add(new ObjectReference(object.getContainedIn()));
		} else if (chouetteObject instanceof StopArea) {
			StopArea object = (StopArea) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof StopAreaLite) {
			StopAreaLite object = (StopAreaLite) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof ConnectionLink) {
			ConnectionLink object = (ConnectionLink) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof Network) {
			Network object = (Network) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof Company) {
			Company object = (Company) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof LineNotice) {
			LineNotice object = (LineNotice) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof GroupOfLine) {
			GroupOfLine object = (GroupOfLine) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		} else if (chouetteObject instanceof Timetable) {
			Timetable object = (Timetable) chouetteObject;
			objectRefs.add(new ObjectReference(object));
		}

	}

	public static String buildName(ChouetteIdentifiedObject chouetteObject) {
		if (chouetteObject instanceof VehicleJourney) {
			VehicleJourney object = (VehicleJourney) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof JourneyPattern) {
			JourneyPattern object = (JourneyPattern) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof Route) {
			Route object = (Route) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof Line) {
			Line object = (Line) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof LineLite) {
			LineLite object = (LineLite) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof AccessLink) {
			AccessLink object = (AccessLink) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof AccessPoint) {
			AccessPoint object = (AccessPoint) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof StopArea) {
			StopArea object = (StopArea) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof StopAreaLite) {
			StopAreaLite object = (StopAreaLite) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof ConnectionLink) {
			ConnectionLink object = (ConnectionLink) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof Network) {
			Network object = (Network) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof Company) {
			Company object = (Company) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof CompanyLite) {
			CompanyLite object = (CompanyLite) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof LineNotice) {
			LineNotice object = (LineNotice) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof GroupOfLine) {
			GroupOfLine object = (GroupOfLine) chouetteObject;
			return NamingUtil.getName(object);
		} else if (chouetteObject instanceof Timetable) {
			Timetable object = (Timetable) chouetteObject;
			return NamingUtil.getName(object);
		}
		return "unnammed";
	}

	@Override
	public void print(PrintStream out, StringBuilder ret, int level, boolean initFirst) {
		ret.setLength(0);
		out.print(addLevel(ret, level).append('{'));
		boolean first = true;
		if (file != null) {
			printObject(out, ret, level + 1, "file", file, first);
			first = false;
		}
		if (objectId != null) {
			out.print(toJsonString(ret, level + 1, "objectid", objectId, first));
			first = false;
		}
		if (name != null) {
			out.print(toJsonString(ret, level + 1, "label", name, first));
			first = false;
		}
		if (!objectRefs.isEmpty()) {
			printArray(out, ret, level + 1, "object_path", objectRefs, first);
			first = false;
		}
		if (attribute != null) {
			out.print(toJsonString(ret, level + 1, "attribute", attribute, first));
			first = false;
		}
		ret.setLength(0);
		out.print(addLevel(ret.append('\n'), level).append('}'));

	}

	public void clear() {
		objectRefs.clear();
	}

}

package mobi.chouette.exchange.netex_stif;

public class NetexStifConstant {

	// Object ID Formats
	public static final String NETEX_LINE_ID_FORMAT = "FR1:Line:{ref}:";
	public static final String NETEX_OPERATOR_ID_FORMAT = "FR1:Operator:{ref}:LOC";



	// Tags NeTEx
	public static final String ARRIVAL_DAY_OFFSET = "ArrivalDayOffset";
	public static final String ARRIVAL_TIME = "ArrivalTime";
	public static final String CHANGED = "changed";
	public static final String COMPOSITE_FRAME = "CompositeFrame";
	public static final String DATA_OBJECTS = "dataObjects";
	public static final String DATE = "Date";
	public static final String DAY_TYPE = "DayType";
	public static final String DAY_TYPES = "dayTypes";
	public static final String DAY_TYPE_ASSIGNMENT = "DayTypeAssignment";
	public static final String DAY_TYPE_ASSIGNMENTS = "dayTypeAssignments";
	public static final String DAY_TYPE_REF = "DayTypeRef";
	public static final String DAYS_OF_WEEK = "DaysOfWeek";
	public static final String DEPARTURE_DAY_OFFSET = "DepartureDayOffset";
	public static final String DEPARTURE_TIME = "DepartureTime";
	public static final String DESTINATION_DISPLAY = "DestinationDisplay";
	public static final String DESTINATION_DISPLAYS = "destinationDisplays";
	public static final String DESTINATION_DISPLAY_REF = "DestinationDisplayRef";
	public static final String DIRECTION = "Direction";
	public static final String DIRECTIONS = "directions";
	public static final String DIRECTION_INBOUND = "inbound";
	public static final String DIRECTION_OUTBOUND = "outbound";
	public static final String DIRECTION_REF = "DirectionRef";
	public static final String DIRECTION_TYPE = "DirectionType";
	public static final String DISTANCE = "Distance";
	public static final String FOR_ALIGHTING = "ForAlighting";
	public static final String FOR_BOARDING = "ForBoarding";
	public static final String FRAMES = "frames";
	public static final String FROM_DATE = "FromDate";
	public static final String FRONT_TEXT = "FrontText";
	public static final String GENERAL_FRAME = "GeneralFrame";
	public static final String ID = "id";
	public static final String INVERSE_ROUTE_REF = "InverseRouteRef";
	public static final String IS_AVAILABLE = "isAvailable";
	public static final String JOURNEY_PATTERN_REF = "JourneyPatternRef";
	public static final String LINE_REF = "LineRef";
	public static final String MEMBERS = "members";
	public static final String MODIFICATION = "modification";
	public static final String NAME = "Name";
	public static final String NETEX_CALENDRIER = "FR1:TypeOfFrame:NETEX_CALENDRIER:";
	public static final String NETEX_COMMUN = "FR1:TypeOfFrame:NETEX_COMMUN:";
	public static final String NETEX_HORAIRE = "NETEX_HORAIRE";
	public static final String NETEX_OFFRE_LIGNE = "FR1:TypeOfFrame:NETEX_OFFRE_LIGNE:";
	public static final String NETEX_STRUCTURE = "FR1:TypeOfFrame:NETEX_STRUCTURE:";
	public static final String NETEX_ARRET_STIF = "NETEX_ARRET_STIF";
	public static final String NOTICE = "Notice";
	public static final String NOTICES = "notices";
	public static final String NOTICE_ASSIGNMENT = "NoticeAssignment";
	public static final String NOTICE_ASSIGNMENTS = "noticeAssignments";
	public static final String NOTICE_REF = "NoticeRef";
	public static final String OPERATING_DAY_REF = "OperatingDayRef";
	public static final String OPERATING_PERIOD = "OperatingPeriod";
	public static final String OPERATING_PERIODS = "operatingPeriods";
	public static final String OPERATING_PERIOD_REF = "OperatingPeriodRef";
	public static final String OPERATOR_REF = "OperatorRef";
	 // Attention le I est en majuscule dans la XSD
	public static final String OPPOSITE_DIRECTION_REF = "OppositeDIrectionRef";

	public static final String ORDER = "order";
	public static final String PARTICIPANT_REF = "ParticipantRef";
	public static final String PASSENGER_STOP_ASSIGNMENT = "PassengerStopAssignment";
	public static final String PASSENGER_STOP_ASSIGNMENTS = "passengerStopAssignments";
	public static final String PASSING_TIMES = "passingTimes";
	public static final String POINTS_IN_SEQUENCE = "pointsInSequence";
	public static final String PROPERTIES = "properties";
	public static final String PROPERTY_OF_DAY = "PropertyOfDay";
	public static final String PUBLICATION_DELIVERY = "PublicationDelivery";
	public static final String PUBLICATION_TIMESTAMP = "PublicationTimestamp";
	public static final String PUBLIC_CODE = "PublicCode";
	public static final String QUAY_REF = "QuayRef";
	public static final String REF = "ref";
	public static final String ROUTE = "Route";
	public static final String ROUTES = "routes";
	public static final String ROUTE_REF = "RouteRef";
	public static final String ROUTING_CONSTRAINT_ZONE = "RoutingConstraintZone";
	public static final String ROUTING_CONSTRAINT_ZONES = "routingConstraintZones";
	public static final String SCHEDULED_STOP_POINT = "ScheduledStopPoint";
	public static final String SCHEDULED_STOP_POINTS = "scheduledStopPoints";
	public static final String SCHEDULED_STOP_POINT_REF = "ScheduledStopPointRef";
	public static final String SERVICE_JOURNEY = "ServiceJourney";
	public static final String SERVICE_JOURNEYS = "serviceJourneys";
	public static final String SERVICE_JOURNEY_NOTICE = "ServiceJourneyNotice";
	public static final String SERVICE_JOURNEY_PATTERN = "ServiceJourneyPattern";
	public static final String SERVICE_JOURNEY_PATTERNS = "serviceJourneyPatterns";
	public static final String SERVICE_JOURNEY_PATTERN_TYPE = "ServiceJourneyPatternType";
	public static final String STOP_POINT = "StopPoint";
	public static final String STOP_POINT_IN_JOURNEY_PATTERN = "StopPointInJourneyPattern";
	public static final String TEXT = "Text";
	public static final String TIMETABLED_PASSING_TIME = "TimetabledPassingTime";
	public static final String TO_DATE = "ToDate";
	public static final String TRAIN_NUMBER = "TrainNumbers";
	public static final String TRAIN_NUMBERS = "trainNumbers";
	public static final String TRAIN_NUMBER_REF = "TrainNumberRef";
	public static final String TYPE_OF_FRAME_REF = "TypeOfFrameRef";
	public static final String TYPE_OF_NOTICE_REF = "TypeOfNoticeRef";
	public static final String VERSION = "version";
	public static final String ZONE_USE = "ZoneUse";

	// other constants
	public static final String COMPOSITE_FRAMES = "composite_frames";
	public static final String GENERAL_FRAMES = "general_frames";
	public static final String NETEX_STIF_OBJECT_FACTORY = "NetexStifObjectFactory";
	public static final String ROUTE_FROM_SERVICE_JOURNEY_PATTERN = "RouteFromServiceJourneyPattern";
	public static final String VALIDATORS = "validators";

	// file names
	public static final String CALENDRIER_FILE_NAME = "calendriers.xml";
	public static final String COMMUN_FILE_NAME = "commun.xml";
	public static final String OFFRE_FILE_PREFIX = "offre";
	public static final String LIGNES_FILE_NAME = "lignes.xml";
	public static final String LIGNE_FILE_NAME = "ligne.xml";
	public static final String ARRETS_FILE_NAME = "arrets.xml";

	// context constants
	public static final String IMPORT_DATA_SOURCE_REF = "import_data_source_ref";
	public static final String OUTPUT_SUB_PATH = "output_sub_path";
	public static final String OPERATOR_OBJECT_ID = "operator_object_id";
	public static final String SHARED_REPORT = "shared_report";
	public static final String LINE_OBJECT_ID = "line_object_id";

	private NetexStifConstant() {
	}
}

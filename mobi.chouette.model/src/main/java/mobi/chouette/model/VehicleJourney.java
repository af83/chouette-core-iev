package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.JourneyCategoryEnum;
import mobi.chouette.model.type.TransportModeNameEnum;

/**
 * Chouette VehicleJourney
 * <p>
 * <b>Note</b> VehicleJourney class contains method to manipulate
 * VehicleJourneyAtStop in logic with StopPoint's position on Route and
 * StopPoint list in JourneyPatterns <br>
 * it is mandatory to respect instruction on each of these methods
 * <p>
 * Neptune mapping : VehicleJourney <br>
 * Gtfs mapping : trip <br>
 */

@Entity
@Table(name = "vehicle_journeys")
@EqualsAndHashCode(of = { "objectId" }, callSuper = false)
@NoArgsConstructor
@ToString(callSuper = true, exclude = { "journeyPattern", "route", "timetables" })
public class VehicleJourney extends ChouetteIdentifiedObject implements SignedChouetteObject, DataSourceRefObject {

	private static final long serialVersionUID = 304336286208135064L;

	@Getter
	@Setter
	@GenericGenerator(name = "vehicle_journeys_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteTenantIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "vehicle_journeys_id_seq"),
			@Parameter(name = "increment_size", value = "100") })
	@GeneratedValue(generator = "vehicle_journeys_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	/**
	 * Neptune object id <br>
	 * composed of 3 items separated by a colon
	 * <ol>
	 * <li>prefix : an alphanumerical value (underscore accepted)</li>
	 * <li>type : a camelcase name describing object type</li>
	 * <li>technical id: an alphanumerical value (underscore and minus accepted)
	 * </li>
	 * </ol>
	 * This data must be unique in dataset
	 *
	 * @return The actual value
	 */
	@Getter
	@NaturalId(mutable = true)
	@Column(name = "objectid", nullable = false, unique = true)
	protected String objectId;

	public void setObjectId(String value) {
		objectId = StringUtils.abbreviate(value, 255);
	}

	/**
	 * object version
	 *
	 * @param objectVersion
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "object_version")
	protected Long objectVersion = 1L;

	@Getter
	@Setter
	@Column(name = "checksum")
	private String checksum;

	@Getter
	@Setter
	@Column(name = "checksum_source")
	private String checksumSource;

	/**
	 * comment
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "comment")
	private String comment;

	/**
	 * set comment <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setComment(String value) {
		comment = StringUtils.abbreviate(value, 255);
	}

	/**
	 * Transport mode when different from line transport mode
	 *
	 * @param transportMode
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "transport_mode")
	private TransportModeNameEnum transportMode;

	/**
	 * published journey name
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "published_journey_name")
	private String publishedJourneyName;

	/**
	 * set published journey name <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setPublishedJourneyName(String value) {
		publishedJourneyName = StringUtils.abbreviate(value, 255);

	}

	/**
	 * published journey identifier
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "published_journey_identifier")
	private String publishedJourneyIdentifier;

	/**
	 * set published journey identifier <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setPublishedJourneyIdentifier(String value) {
		publishedJourneyIdentifier = StringUtils.abbreviate(value, 255);

	}

	/**
	 * facility
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "facility")
	private String facility;

	/**
	 * set facility <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setFacility(String value) {
		facility = StringUtils.abbreviate(value, 255);
	}

	/**
	 * vehicle type identifier
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "vehicle_type_identifier")
	private String vehicleTypeIdentifier;

	/**
	 * set vehicle type identifier <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setVehicleTypeIdentifier(String value) {
		vehicleTypeIdentifier = StringUtils.abbreviate(value, 255);

	}

	/**
	 * number
	 *
	 * @param number
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "number")
	private Long number;
	/**
	 * data source ref
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "data_source_ref")
	private String dataSourceRef;

	/**
	 * set data source ref <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setDataSourceRef(String value) {
		dataSourceRef = StringUtils.abbreviate(value, 255);
	}

	/**
	 * mobility restriction indicator (such as wheel chairs) <br>
	 *
	 * <ul>
	 * <li>null if unknown
	 * <li>true if wheel chairs can use this line</li>
	 * <li>false if wheel chairs can't use this line</li>
	 * </ul>
	 *
	 * @param mobilityRestrictedSuitability
	 *            New state for mobility restriction indicator
	 * @return The actual mobility restriction indicator
	 */
	@Getter
	@Setter
	@Column(name = "mobility_restricted_suitability")
	private Boolean mobilityRestrictedSuitability;

	/**
	 * flexible service <br>
	 *
	 * <ul>
	 * <li>null if unknown or inherited from line
	 * <li>true for flexible service</li>
	 * <li>false for regular service</li>
	 * </ul>
	 *
	 * @param flexibleService
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "flexible_service")
	private Boolean flexibleService;

	/**
	 * route reference
	 *
	 * @param route
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	private Route route;

	/**
	 * journey pattern reference
	 *
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journey_pattern_id")
	private JourneyPattern journeyPattern;

	/**
	 * set journey pattern reference
	 *
	 * @param journeyPattern
	 */
	public void setJourneyPattern(JourneyPattern journeyPattern) {
		if (this.journeyPattern != null) {
			this.journeyPattern.getVehicleJourneys().remove(this);
		}
		this.journeyPattern = journeyPattern;
		if (journeyPattern != null) {
			journeyPattern.getVehicleJourneys().add(this);
		}
	}

	/**
	 * company reference<br>
	 * if different from line company
	 *
	 * @param company
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Transient
	private Company company;

	/**
	 * footnotes refs
	 *
	 * @param footnotes
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany
	@JoinTable(name = "footnotes_vehicle_journeys", joinColumns = {
			@JoinColumn(name = "vehicle_journey_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "footnote_id", nullable = false, updatable = false) })
	private List<Footnote> footnotes = new ArrayList<>(0);

	/**
	 * timetables
	 *
	 * @param timetables
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinTable(name = "time_tables_vehicle_journeys", joinColumns = {
			@JoinColumn(name = "vehicle_journey_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "time_table_id", nullable = false, updatable = false) })
	private List<Timetable> timetables = new ArrayList<Timetable>(0);

	/**
	 * vehicle journey at stops : passing times
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_journey_id", updatable = false)
	private List<VehicleJourneyAtStop> vehicleJourneyAtStops = new ArrayList<VehicleJourneyAtStop>(0);

	/**
	 * To distinguish the timesheets journeys and the frequencies ones. Defaults
	 * to Timesheet.
	 *
	 * @param journeyCategory
	 *            The new vehicle journey category
	 * @return The actual vehicle journey category
	 * @since 3.2.0
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "journey_category")
	private JourneyCategoryEnum journeyCategory = JourneyCategoryEnum.Timesheet;

	/**
	 * For frequencies journeys, applicable periods
	 *
	 * @param journeyFrequencies
	 *            The new vehicle journey frequencies
	 * @return The actual vehicle journey category
	 * @since 3.2.0
	 */
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "vehicle_journey_id", updatable = false)
	private List<JourneyFrequency> journeyFrequencies = new ArrayList<JourneyFrequency>(0);

	@Setter
	@Getter
	@Column(name = "company_id")
	private Long companyId;

	@Setter
	@Getter
	@Column(name = "line_notice_ids", columnDefinition = "bigint[]")
	@Type(type = "mobi.chouette.model.usertype.LongArrayUserType")
	private Long[] lineNoticeIds = new Long[0];

	@Getter
	@Setter
	@Transient
	private List<LineNotice> lineNotices = new ArrayList<LineNotice>();

	/**
	 * sort passing times against stop point position
	 */
	public void sortVjas() {
		getVehicleJourneyAtStops()
				.sort((v1, v2) -> v1.getStopPoint().getPosition().compareTo(v2.getStopPoint().getPosition()));
	}
}

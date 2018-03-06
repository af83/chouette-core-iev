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
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.type.SectionStatusEnum;

/**
 * Chouette Journey Pattern : pattern for vehicle journeys in a route
 * <p>
 * Neptune mapping : JourneyPattern <br>
 * Gtfs mapping : none
 * 
 */
@Entity
@Table(name = "journey_patterns")
@NoArgsConstructor
@EqualsAndHashCode(of = { "objectId" }, callSuper = false)
@ToString(callSuper=true, exclude = { "route" })
public class JourneyPattern extends ChouetteIdentifiedObject implements SignedChouetteObject {
	private static final long serialVersionUID = 7895941111990419404L;

	@Getter
	@Setter
	@GenericGenerator(name = "journey_patterns_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteTenantIdentifierGenerator", 
		parameters = {
			@Parameter(name = "sequence_name", value = "journey_patterns_id_seq"),
			@Parameter(name = "increment_size", value = "20") })
	@GeneratedValue(generator = "journey_patterns_id_seq")
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
	@NaturalId(mutable=true)
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
	private String checksum ;
	
	@Getter
	@Setter 
	@Column(name = "checksum_source")
	private String checksumSource;


	/**
	 * name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "name")
	private String name;

	/**
	 * set name <br>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setName(String value) {
		name = StringUtils.abbreviate(value, 255);
	}

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
	 * registration number
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "registration_number")
	private String registrationNumber;

	/**
	 * set registration number <br>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setRegistrationNumber(String value) {
		registrationNumber = StringUtils.abbreviate(value, 255);
	}

	/**
	 * published name
	 * 
	 * @return The actual value
	 */
	@Getter
	@Column(name = "published_name")
	private String publishedName;

	/**
	 * set published name <br>
	 * truncated to 255 characters if too long
	 * 
	 * @param value
	 *            New value
	 */
	public void setPublishedName(String value) {
		publishedName = StringUtils.abbreviate(value, 255);
	}

	
	/**
	 * Section status
	 * 
	 * @param sectionStatus
	 *            New value
	 * @return The actual value
	 * @since 3.2.0
	 */
	@Getter
	@Setter
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "section_status")
	private SectionStatusEnum sectionStatus = SectionStatusEnum.Todo;
	
	/**
	 * route reverse reference
	 * 
	 * @return The actual value
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "route_id")
	private Route route;

	/**
	 * set route reverse reference
	 * 
	 * @param route
	 */
	public void setRoute(Route route) {
		if (this.route != null) {
			this.route.getJourneyPatterns().remove(this);
		}
		this.route = route;
		if (route != null) {
			route.getJourneyPatterns().add(this);
		}
	}

	/**
	 * first stop
	 * 
	 * @param departureStopPoint
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "departure_stop_point_id")
	private StopPoint departureStopPoint;

	/**
	 * last stop
	 * 
	 * @param arrivalStopPoint
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "arrival_stop_point_id")
	private StopPoint arrivalStopPoint;

	/**
	 * stop list
	 * 
	 * @param stopPoints
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@ManyToMany
	@OrderBy(value="position")
	@JoinTable(name = "journey_patterns_stop_points", joinColumns = { @JoinColumn(name = "journey_pattern_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "stop_point_id", nullable = false, updatable = false) })
	private List<StopPoint> stopPoints = new ArrayList<StopPoint>(0);

	/**
	 * vehicle journeys
	 * 
	 * @param vehicleJourneys
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "journeyPattern", cascade = { CascadeType.PERSIST})
	private List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>(
			0);

	
	/**
	 * add a stop point if not already present
	 * 
	 * @param stopPoint
	 */
	public void addStopPoint(StopPoint stopPoint) {
		if (!stopPoints.contains(stopPoint)) {
			stopPoints.add(stopPoint);
		}
	}

	/**
	 * remove a stop point
	 * 
	 * @param stopPoint
	 */
	public void removeStopPoint(StopPoint stopPoint) {
		if (stopPoints.contains(stopPoint)) {
			stopPoints.remove(stopPoint);
		}
	}

}

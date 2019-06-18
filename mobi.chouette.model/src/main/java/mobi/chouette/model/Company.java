/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.NaturalId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Chouette Company : a company providing public transport services.
 * <p>
 * Neptune mapping : Company <br>
 * Gtfs mapping : Agency <br>
 */

@Entity
@Table(name = "companies",schema="public")
@Cacheable
@NoArgsConstructor
@EqualsAndHashCode(of = { "objectId" }, callSuper = false)
@ToString(callSuper=true, exclude = { "lines" })
public class Company extends ChouetteIdentifiedObject {

	private static final long serialVersionUID = -8086291270595894778L;

	@Getter
	@Setter
	@SequenceGenerator(name="companies_id_seq", sequenceName="public.companies_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="companies_id_seq")
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
	 * short name
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "short_name")
	private String shortName;

	/**
	 * set short name <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setShortName(String value) {
		shortName = StringUtils.abbreviate(value, 255);
	}

	/**
	 * organizational unit
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "default_contact_organizational_unit")
	private String organisationalUnit;

	/**
	 * set organizational unit <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setOrganisationalUnit(String value) {
		organisationalUnit = StringUtils.abbreviate(value, 255);

	}

	/**
	 * operating department name
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "default_contact_operating_department_name")
	private String operatingDepartmentName;

	/**
	 * set operating department name <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setOperatingDepartmentName(String value) {
		operatingDepartmentName = StringUtils.abbreviate(value, 255);

	}

	/**
	 * organization code <br>
	 * usually fixed by Transport Authority
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "code")
	private String code;

	/**
	 * set organization code <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setCode(String value) {
		code = StringUtils.abbreviate(value, 255);
	}

	/**
	 * phone number
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "default_contact_phone")
	private String phone;

	/**
	 * set phone number <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setPhone(String value) {
		phone = StringUtils.abbreviate(value, 255);
	}

	/**
	 * fax number
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "default_contact_fax")
	private String fax;

	/**
	 * set fax number <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setFax(String value) {
		fax = StringUtils.abbreviate(value, 255);
	}

	/**
	 * email
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "default_contact_email")
	private String email;

	/**
	 * set email <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setEmail(String value) {
		email = StringUtils.abbreviate(value, 255);
	}

	/**
	 * registration number
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "registration_number", unique = true)
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
	 * web site url
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "default_contact_url")
	private String url;

	/**
	 * set web site url <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setUrl(String value) {
		url = StringUtils.abbreviate(value, 255);
	}

	/**
	 * default timezone
	 *
	 * @return The actual value
	 */
	@Getter
	@Column(name = "time_zone")
	private String timeZone;

	/**
	 * set default timezone <br>
	 * truncated to 255 characters if too long
	 *
	 * @param value
	 *            New value
	 */
	public void setTimeZone(String value) {
		timeZone = StringUtils.abbreviate(value, 255);
	}

	/**
	 * xml data
	 */
	@Getter
	@Setter
	@Column(name="import_xml",columnDefinition = "text")
	private String importXml;


	/**
	 * line referential reference
	 *
	 * @return The actual value
	 */
	@Getter
	@Setter
	@Column(name = "line_referential_id")
	protected Long lineReferentialId;

	/**
	 * lines
	 *
	 * @param lines
	 *            New value
	 * @return The actual value
	 */
	@Getter
	@Setter
	@OneToMany(mappedBy = "company")
	private List<Line> lines = new ArrayList<>(0);

}

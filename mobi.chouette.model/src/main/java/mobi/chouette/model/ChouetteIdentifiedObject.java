/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package mobi.chouette.model;

import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.NaturalId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract object used for all Identified Chouette Object
 * <p>
 */
@SuppressWarnings("serial")
@MappedSuperclass
@EqualsAndHashCode(of = { "objectId" }, callSuper = false)
@ToString(callSuper = true)
public abstract class ChouetteIdentifiedObject extends ChouetteDatedObject {

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
	@Transient
	private boolean saved = false;

	@Getter
	@Setter
	@Transient
	private boolean isFilled = false;

	/**
	 * to be overrided; facility to check registration number on any object
	 * 
	 * @return null : when object has no registration number
	 */
	public String getRegistrationNumber() {
		return null;
	}

	/**
	 * check if an objectId is conform to Trident
	 * 
	 * @param oid
	 *            objectId to check
	 * @return true if valid, false otherwise
	 */
	public static boolean checkObjectId(String oid) {
		if (oid == null)
			return false;

		Pattern p = Pattern.compile("(\\w|_)+:\\w+:([0-9A-Za-z]|_|-)+");
		return p.matcher(oid).matches();
	}

	protected String[] objectIdArray() {
		return objectId.split(":");
	}

	/**
	 * return prefix of objectId
	 * 
	 * @return String
	 */
	public String objectIdPrefix() {
		String[] tokens = objectIdArray();
		if (tokens.length > 2) {
			return tokens[0].trim();
		} else
			return "";
	}

	/**
	 * return suffix of objectId
	 * 
	 * @return String
	 */
	public String objectIdSuffix() {
		String[] tokens = objectIdArray();
		if (tokens.length > 2)
			return tokens[2].trim();
		else
			return "";
	}
}

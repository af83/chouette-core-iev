package mobi.chouette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Chouette Footnote : a note for vehicle journeys
 * <p>
 * Neptune mapping : non (extension in comments <br>
 * Gtfs mapping : none <br>
 * Hub mapping :
 *
 * @since 2.5.3
 */

@Entity
@Table(name = "footnotes")
@EqualsAndHashCode(of = { "objectId" }, callSuper = false)
@ToString(callSuper = true)
@NoArgsConstructor
public class Footnote extends ChouetteIdentifiedObject implements SignedChouetteObject, DataSourceRefObject {

	private static final long serialVersionUID = -6223882293500225313L;

	@Getter
	@Setter
	@GenericGenerator(name = "footnotes_id_seq", strategy = "mobi.chouette.persistence.hibernate.ChouetteTenantIdentifierGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "footnotes_id_seq"),
			@Parameter(name = "increment_size", value = "10") })
	@GeneratedValue(generator = "footnotes_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	@Getter
	@Setter
	@Column(name = "checksum")
	private String checksum;

	@Getter
	@Setter
	@Column(name = "checksum_source")
	private String checksumSource;

	@Setter
	@Transient
	private String objectId;

	public String getObjectId() {
		if (objectId == null && lineLite != null && id != null) {
			if (dataSourceRef != null) {
				objectId = dataSourceRef + ":Notice:" + lineLite.objectIdSuffix() + "_" + id + ":LOC";
			} else {
				objectId = "null:Notice:" + lineLite.objectIdSuffix() + "_" + id + ":LOC";
			}
		}
		return objectId;
	}

	@Getter
	@Setter
	@Transient
	private Long objectVersion;


	@Getter
	@Setter
	@Transient
	private Line line;

	@Getter
	@Setter
	@Transient
	private LineLite lineLite;

	@Getter
	@Setter
	@Column(name = "line_id")
	private Long lineId;

	@Getter
	@Column(name = "label")
	private String label;

	public void setLabel(String value) {
		label = StringUtils.abbreviate(value, 255);
	}

	@Getter
	@Column(name = "code")
	private String code;

	public void setCode(String value) {
		code = StringUtils.abbreviate(value, 255);
	}

	@Getter
	@Column(name = "data_source_ref")
	private String dataSourceRef;

	public void setDataSourceRef(String value) {
		dataSourceRef = StringUtils.abbreviate(value, 255);
	}

	@Getter
	@Setter
	@Transient
	private String key;
}

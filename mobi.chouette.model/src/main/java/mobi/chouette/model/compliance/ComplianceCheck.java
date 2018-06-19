package mobi.chouette.model.compliance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jettison.json.JSONObject;
import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.model.ChouetteDatedObject;

@Entity
@Table(name = "compliance_checks")
@NoArgsConstructor
@ToString(callSuper = true, exclude = { "task", "block" })
public class ComplianceCheck extends ChouetteDatedObject {

	private static final long serialVersionUID = -6471679594238033395L;

	// @formatter:off
	/**
	 * compliance_checks -> classe ComplianceCheck 
     *  attributs : 
     *     id : 
     *     name : pour les logs ? 
     *     type : ??? (type d'objet concerné? ) 
     *     code : vérifier avec AF si c'est le code des cartes Redmine ou le code redéfini ; dans le second cas, demander le champs du code Redmine
     *     attributes : hstore , demander les clés à AF 
     *     criticity : enum
     *     comment : inutile
     *
     * relations : ComplianceCheckTask propriétaire
     *            optionnel ComplianceCheckBlock propriétaire
	 */
	// @formatter:on

	public enum CRITICITY {
		warning, error
	}

	@Getter
	@Setter
	@SequenceGenerator(name = "compliance_checks_id_seq", sequenceName = "compliance_checks_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compliance_checks_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compliance_check_set_id")
	private ComplianceCheckTask task;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "compliance_check_block_id")
	private ComplianceCheckBlock block;

	@Getter
	@Setter
	@Column(name = "type", nullable = false)
	protected String type;

	@Getter
	@Setter
	@Column(name = "control_attributes",columnDefinition="json")
	@Type(type = "mobi.chouette.model.usertype.JsonUserType")
	private JSONObject controlAttributes = new JSONObject();

	@Getter
	@Setter
	@Column(name = "name")
	protected String name;

	@Getter
	@Setter
	@Column(name = "code", nullable = false)
	protected String specificCode;

	@Getter
	@Setter
	@Column(name = "origin_code", nullable = false)
	protected String originCode;

	@Getter
	@Setter
	@Column(name = "criticity")
	@Enumerated(EnumType.STRING)
	private CRITICITY criticity;

	@Getter
	@Setter
	@Column(name = "comment", nullable = false)
	protected String comment;
	
	@Getter
	@Setter
	@Column(name = "iev_enabled_check", nullable = false)
	protected Boolean ievEnabledCheck;

}

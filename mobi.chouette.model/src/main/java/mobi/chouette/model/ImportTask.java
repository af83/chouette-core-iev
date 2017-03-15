package mobi.chouette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.common.JobData;

@Entity
@Table(name = "imports")
@NoArgsConstructor
@ToString(callSuper = true)
public class ImportTask extends ActionTask {
	private static final long serialVersionUID = 9177879600144123687L;
	
	public JobData.ACTION getAction()
	{
		return JobData.ACTION.importer;
	}
	
	@Getter
	@Setter
	@SequenceGenerator(name="imports_id_seq", sequenceName="imports_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="imports_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;
	
	
	@Getter
	@Setter
	@Transient
	private String format = "netex_stif";
	

	@Getter
	@Setter
	@Column(name="file")
	private String file;



}

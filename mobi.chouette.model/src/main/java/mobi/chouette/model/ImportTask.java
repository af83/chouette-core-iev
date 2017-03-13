package mobi.chouette.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "imports")
@NoArgsConstructor
@ToString(callSuper = true)
public class ImportTask {
	@Getter
	@Setter
	@SequenceGenerator(name="imports_id_seq", sequenceName="imports_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="imports_id_seq")
	@Id
	@Column(name = "id", nullable = false)
	protected Long id;
	
	@Getter
	@Setter
	@Column(name = "name")
	private String name;
	
	@Getter
	@Setter
	@Column(name = "status")
	private String status;
	
	@Getter
	@Setter
	@Column(name = "current_step_id")
	private String currentStepId;
	
	@Getter
	@Setter
	@Column(name="current_step_progress")
	private double currentStepProgress;
	
	@Getter
	@Setter
	@Column(name="workbench_id")
	private Integer workbenchId;
	
	@Getter
	@Setter
	@Column(name="referential_id")
	private Integer referentialId;
	
	@Getter
	@Setter
	@Column(name="created_at")
	private java.sql.Timestamp createdAt;
	
	@Getter
	@Setter
	@Column(name="updated_at")
	private java.sql.Timestamp updatedAt;
	
	@Getter
	@Setter
	@Column(name="file")
	private String file;
	

}
package mobi.chouette.exchange.netex_stif;

import lombok.Data;
import mobi.chouette.common.JobData;

@Data
public class JobDataTest implements JobData {

	private Long id;

	private String inputFilename;

	private String outputFilename;

	private JobData.ACTION action;
	
	private String type;
	
	private String referential;
	
	private String pathName;
}

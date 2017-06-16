package mobi.chouette.dao;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.JobData;
import mobi.chouette.model.ActionResource;
import mobi.chouette.model.ImportResource;

@Stateless  (name="ActionResourceDAO")
public class ActionResourceDAOImpl implements ActionResourceDAO {

	@EJB
	ImportResourceDAO importResourceDAO;
	
	@Override
	public ActionResource createResource(JobData job) {
		ActionResource resource = null;
		switch (job.getAction()) {
		case importer:
			resource = new ImportResource(job.getId());
			break;
		case exporter:
//			resource = new ExportResource(job.getId());
			break;
		case validator:
//			resource = new ValidationResource(job.getId());
			break;
		}
		return resource;
	}

	@Override
	public void saveResource(ActionResource resource) {
		switch (resource.getAction())
		{
		case exporter:
			break;
		case importer:
			ImportResource importResource = (ImportResource) resource;
			importResourceDAO.create(importResource);
			break;
		case validator:
			break;
		
		}
		
	}

}

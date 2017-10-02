package mobi.chouette.dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import mobi.chouette.common.JobData;
import mobi.chouette.model.ActionTask;
import mobi.chouette.model.ImportTask;
import mobi.chouette.model.compliance.ComplianceCheckTask;

@Stateless(name = "ActionDAO")
public class ActionDAOImpl implements ActionDAO {

	@EJB
	ImportTaskDAO importTaskDAO;

	// @EJB
	// ExportTaskDAO exportTaskDAO;
	//
	@EJB
	ComplianceCheckTaskDAO complianceCheckTaskDAO;

	@Override
	public ActionTask getTask(JobData job) {
		ActionTask task = null;
		switch (job.getAction()) {
		case importer:
			task = importTaskDAO.find(job.getId());
			break;
		case exporter:
			// task = exportTaskDAO.find(job.getId());
			break;
		case validator:
			// task = validationTaskDAO.find(job.getId());
			break;
		}
		return task;
	}

	@Override
	public void saveTask(ActionTask task) {
		switch (task.getAction()) {
		case exporter:
			break;
		case importer:
			ImportTask iTask = (ImportTask) task;
			importTaskDAO.update(iTask);
			break;
		case validator:
			break;

		}
	}

	@Override
	public ActionTask find(JobData.ACTION actionType, Long id) {
		switch (actionType) {
		case exporter:
			break;
		case importer:
			return importTaskDAO.find(id);
		case validator:
			return complianceCheckTaskDAO.find(id);
		}
		return null;
	}

	@Override
	public void update(ActionTask task) {
		switch (task.getAction()) {
		case exporter:
			break;
		case importer:
			ImportTask iTask = (ImportTask) task;
			importTaskDAO.update(iTask);
			break;
		case validator:
			ComplianceCheckTask vTask = (ComplianceCheckTask) task;
			complianceCheckTaskDAO.update(vTask);
			break;
		}
	}

	@Override
	public List<ActionTask> getTasks(String status) {
        List<ActionTask> result = new ArrayList<>();
        result.addAll(importTaskDAO.getTasks(status));
        // result.addAll(complianceCheckTaskDAO.getTasks(status));
        // result.addAll(publicationTaskDAO.getTasks(status));
        
        result.sort(new Comparator<ActionTask>() 
        {
			@Override
			public int compare(ActionTask o1, ActionTask o2) {
				return o1.getCreatedAt().compareTo(o2.getCreatedAt());
			}
		});
		return result;
	}
}

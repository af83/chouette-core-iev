package mobi.chouette.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedTaskListener;
import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.persistence.hibernate.ContextHolder;
import mobi.chouette.service.JobService;
import mobi.chouette.service.JobServiceManager;

/**
 * @author michel
 *
 */
@Singleton(name = Scheduler.BEAN_NAME)
@Startup
@Log4j
public class Scheduler {

	public static final String BEAN_NAME = "Scheduler";

	@EJB
	JobServiceManager jobManager;

	@Resource(lookup = "java:comp/DefaultManagedExecutorService")
	ManagedExecutorService executor;

	Map<Long, Future<JobService.STATUS>> startedFutures = new ConcurrentHashMap<>();

	public int getActiveJobsCount() {
		return startedFutures.size();
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean schedule() {
		String context = "boiv";
		String maxJobStr = System.getProperty(context + PropertyNames.MAX_STARTED_JOBS);

		int maxJob = Integer.parseInt(maxJobStr);

		if (getActiveJobsCount() >= maxJob) {
			log.debug("to many active jobs, schedule later");
			return false;
		}
		log.debug("try to schedule");

		JobService jobService = jobManager.getNextJob();
		if (jobService != null) {
			log.info("start a new job " + jobService.getId());
			jobManager.start(jobService);

			Map<String, String> properties = new HashMap<>();
			Task task = new Task(jobService, properties, new TaskListener());
			Future<JobService.STATUS> future = executor.submit(task);
			startedFutures.put(jobService.getId(), future);
			return true;
		} else {
			log.info("nothing to schedule ");
			return false;
		}

	}

	@PostConstruct
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private void initialize() {

		try {
			List<JobService> scheduled = jobManager.findAll(JobService.STATUS.RUNNING);

			for (JobService jobService : scheduled) {
				jobManager.abort(jobService);

			}

			// manage new imported data
			List<JobService> newJobs = jobManager.findAll(JobService.STATUS.NEW);
			for (JobService job : newJobs) {
				createJob(job);
			}

			while (schedule()) {
				log.info("schedule pending tasks");
			}
		} catch (RuntimeException ex) {
			log.fatal("cannot start scheduler", ex);
			throw ex;
		}
	}

	private void createJob(JobService job) {
		try {
			jobManager.createJob(job.getAction().name(), job.getId());
		} catch (Exception ex) {
			log.error("cannot manage new job : " + job.getAction().name() + " " + job.getId());
		}
	}

	/**
	 * cancel task
	 * 
	 * @param job
	 * @return
	 */
	public boolean cancel(JobService jobService) {

		// remove prevents for multiple calls
		log.info("try to cancel " + jobService.getId());
		Future<JobService.STATUS> future = startedFutures.remove(jobService.getId());
		if (future != null) {
			log.info("cancel future");
			future.cancel(false);
		}

		return true;
	}

	class TaskListener implements ManagedTaskListener {

		@Override
		public void taskAborted(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
			log.info(Color.FAILURE + "task aborted : " + ContextHolder.getContext() + " -> " + task + Color.NORMAL);
			if (task != null && task instanceof Task) {
				log.info("cancel task");
				((Task) task).cancel();
				schedule((Task) task);
			}
		}

		@Override
		public void taskDone(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
			log.info(Color.SUCCESS + "task done : " + ContextHolder.getContext() + " -> " + task + Color.NORMAL);
			schedule((Task) task);
		}

		@Override
		public void taskStarting(Future<?> future, ManagedExecutorService executor, Object task) {
			log.info(Color.SUCCESS + "task starting : " + task + Color.NORMAL);

		}

		@Override
		public void taskSubmitted(Future<?> future, ManagedExecutorService executor, Object task) {
			log.info(Color.SUCCESS + "task submitted : " + task + Color.NORMAL);
		}

		/**
		 * launch next task if exists
		 * 
		 * @param task
		 */
		private void schedule(final Task task) {
			// remove task from stated map
			startedFutures.remove(task.getJob().getId());
			// launch next task
			Runnable runnable = () -> {
				ContextHolder.setContext(null);
				try {
					InitialContext initialContext = new InitialContext();
					Scheduler scheduler = (Scheduler) initialContext
							.lookup("java:app/mobi.chouette.boiv.service/" + BEAN_NAME);

					scheduler.schedule();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			};
			executor.execute(runnable);
		}

	}

}

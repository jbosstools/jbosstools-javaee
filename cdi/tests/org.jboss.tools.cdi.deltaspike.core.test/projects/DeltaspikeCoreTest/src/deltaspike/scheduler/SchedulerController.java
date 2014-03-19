package deltaspike.scheduler;

import javax.inject.Inject;

import org.apache.deltaspike.scheduler.impl.QuartzScheduler;
import org.apache.deltaspike.scheduler.spi.Scheduler;

public class SchedulerController {
	
	@Inject Scheduler scheduler;
	
}

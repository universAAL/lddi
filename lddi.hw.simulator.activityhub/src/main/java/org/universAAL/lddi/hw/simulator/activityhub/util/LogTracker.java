package org.universAAL.lddi.hw.simulator.activityhub.util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class LogTracker extends ServiceTracker implements LogService {

	@Override
	public Object addingService(ServiceReference reference) {
		Object ret = super.addingService(reference);
		log(LOG_DEBUG,"LogService added!");
		return ret;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		super.modifiedService(reference, service);
		log(LOG_DEBUG,"LogService modified!");
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
		log(LOG_DEBUG,"LogService removed!");
	}

	public LogTracker(BundleContext context) {
		super(context, LogService.class.getName(), null);
	}

	public void log(int level, String message) {
		log(null, level, message, null);
	}

	public void log(int level, String message, Throwable exception) {
		log(null, level, message, exception);
	}

	public void log(ServiceReference sr, int level, String message) {
		log(sr, level, message, null);
	}

	public void log(ServiceReference sr, int level, String message,
			Throwable exception) {
		LogService log = (LogService) getService(); //obtain optional, unary LogService
		if (log != null) {
			log.log(sr, level, message, exception);
		}
	}

}

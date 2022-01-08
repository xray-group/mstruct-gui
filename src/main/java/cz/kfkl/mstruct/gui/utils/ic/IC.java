package cz.kfkl.mstruct.gui.utils.ic;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kfkl.mstruct.gui.utils.JvStringUtils;
import cz.kfkl.mstruct.gui.utils.validation.UnexpectedException;

/**
 * Invocation Counter is an utility class for debugging and performance
 * measuring. Should not be used only for development testing. Usage:
 * 
 * <pre>
 * Count c0 = IC.start(&quot;outer counter&quot;);
 * for (String key : allKeys)
 * {
 * 	Count c1 = IC.start(&quot;findEntity&quot;);
 * 	Entity e = findEntity(key);
 * 	c1.stop()
 * 
 * 	c1 = IC.start(&quot;processEntity&quot;);
 * 	e.prepare();
 * 	processEntity(e);
 * 	c1.stop();
 * }
 * c0.stop();
 * 
 * LOG.info(IC.reportAndRestart); //typically in the end of calling resource method, will restart only if the last count was stopped.
 * </pre>
 * 
 * Counter keep tracks of call hierarchy, number of invocation of each counter
 * name (provided in the start method), elapsed time and maximum time. Average
 * elapsed time is also printed. The counter calls with same name but different
 * calling parent are reported separately.
 * <p>
 * For safety and performance reason on released software the thread local
 * caching and the whole Count hierarchy is enabled only if the {@link IC} class
 * logger is set at least to DEBUG. If set to TRACE all counts are printed,
 * DEBUG prints only counts with total time larger then 10.0 milliseconds. The
 * INFO level only counts elapsed time of individual count (returned by the
 * count.stop() method).
 */
public class IC {
	private static final String HAS_RUNNING_COUNTS = "Has running counts, last is: ";
	private static final String NOT_ENABLED_MESSAGE = "IC not enabled, set IC logger to DEBUG or TRACE.";
	private static final String CGLIB_PROXY_SUFFIX_SEPARATOR = "$$";

	private static final Logger LOG = LoggerFactory.getLogger(IC.class);

	private static double DEFAULT_REPORTING_TRESHOLD = 10.0;
	private TreeCount rootCount = new TreeCount();
	TreeCount parentCount = rootCount;

	private boolean logWhenAllDone = false;

	private IC() {
		super();
	}

	private static ThreadLocal<IC> INSTANCES = new ThreadLocal<IC>() {
		@Override
		protected IC initialValue() {
			return new IC();
		}
	};

	private static IC getInstance() {
		return INSTANCES.get();
	}

	/**
	 * Forces thread local cash to be cleared. Dangerous to call within code as
	 * there could be an started count which will then fail when its stop is called.
	 */
	public static void restart() {
		if (LOG.isDebugEnabled()) {
			INSTANCES.remove();
			INSTANCES.set(new IC());
		}
	}

	public static Count start(String name) {
		Count c;
		if (LOG.isDebugEnabled()) {
			IC ic = getInstance();

			TreeCount tc = ic.getStatCount(ic.parentCount, name);
			ic.parentCount = tc;
			tc.start();
			c = tc;
		} else {
			c = new SimpleCount();
		}

		return c;
	}

	public static Count start(Object object, String suffix) {
		Count count;
		if (LOG.isDebugEnabled()) {
			String name = getSimpleName(object) + "." + suffix;
			count = start(name);
		} else {
			count = new SimpleCount();
		}

		return count;
	}

	public static Count start(String prefix, Object object, Method method) {
		Count count;
		if (LOG.isDebugEnabled()) {
			String name = prefix + getSimpleName(object) + "." + method.getName();
			count = start(name);
		} else {
			count = new SimpleCount();
		}

		return count;
	}

	private static String getSimpleName(Object object) {
		if (object != null) {
			return getClassNameWithoutProxy(object.getClass());
		}
		return "";
	}

	private static String getClassNameWithoutProxy(Class<? extends Object> clazz) {
		return JvStringUtils.substringBefore(clazz.getSimpleName(), CGLIB_PROXY_SUFFIX_SEPARATOR);
	}

	public static void stopLast() {
		if (LOG.isDebugEnabled()) {
			IC ic = getInstance();
			try {
				ic.parentCount.stop();
			} catch (UnexpectedException e) {
				LOG.error("Failed to stop last counter. Probably a restart() method was called unexpectedly.", e);
			}
		}
	}

	public static boolean isAllStopped() {
		boolean isFirst = false;
		if (LOG.isDebugEnabled()) {
			IC ic = getInstance();
			isFirst = ic.allStopped();
		}
		return isFirst;
	}

	private boolean allStopped() {
		return parentCount == rootCount;
	}

	public static int getNumberOfCounts() {
		int totalNumberOfCounts = 0;
		if (LOG.isDebugEnabled()) {
			IC ic = getInstance();
			totalNumberOfCounts = ic.rootCount.getNumberOfCounts() - 1;
		}
		return totalNumberOfCounts;
	}

	private TreeCount getStatCount(TreeCount parent, String name) {
		TreeCount count = parent.children.get(name);

		if (count == null) {
			count = new TreeCount(this, parent, name);
		}
		return count;
	}

	public static String report() {
		String reportMsg;
		if (LOG.isDebugEnabled()) {
			reportMsg = getInstance().formatReport(-1);
		} else {
			reportMsg = NOT_ENABLED_MESSAGE;
		}

		return reportMsg;
	}

	/**
	 * If all counters are stopped counter a report of all IC count is formatted and
	 * returned. The IC counts are restarted. If however there is some not stopped
	 * counter only information message is returned, the current thread's IC is
	 * marked as {@link #logWhenAllDone}. Then it is up to the running counter
	 * creator to report and restart all counts. Typically it would be some internal
	 * counting code, like TransactionManagerLoggingWrapper or {@link IcAspect}
	 * which calls {@link #ifAllStoppedLogAndRestart()} which reads the
	 * logWhenAllDone flag and log the report if it is set to true.
	 */
	public static String reportAndRestart() {
		String reportMsg;
		if (LOG.isDebugEnabled()) {
			IC ic = getInstance();
			boolean allStopped = ic.allStopped();
			if (allStopped) {
				reportMsg = formatReportMessage(ic);
				restart();
			} else {
				ic.logWhenAllDone = true;
				reportMsg = HAS_RUNNING_COUNTS + ic.parentCount.getPrefixedName();
			}
		} else {
			reportMsg = NOT_ENABLED_MESSAGE;
		}
		return reportMsg;
	}

	public static void ifAllStoppedLogAndRestart() {
		if (LOG.isDebugEnabled()) {
			IC ic = getInstance();
			if (ic.allStopped()) {
				String reportMsg = formatReportMessage(ic);
				if (ic.logWhenAllDone) {
					LOG.debug(reportMsg);
				} else {
					// LOG.trace(reportMsg);
				}

				restart();
			}
		}
	}

	private static String formatReportMessage(IC ic) {
		double treshold = LOG.isTraceEnabled() ? -1 : DEFAULT_REPORTING_TRESHOLD;
		return "Report for [" + getNumberOfCounts() + "] IC counts, restarted\n" + ic.formatReport(treshold);
	}

	private String formatReport(double logTreshold) {
		TableReportBuilder trb = TableReportBuilder.create(-70, 11, 15);

		trb.appendLine("Counter Name", "Count", "Time (ms)", "Max (ms)", "Avg. (ms)");
		formatLines(trb, rootCount, logTreshold);

		return trb.format();
	}

	private void formatLines(TableReportBuilder trb, TreeCount parentCounter, double logTreshold) {
		for (TreeCount count : parentCounter.children.values()) {
			formatLine(trb, count, logTreshold);
			formatLines(trb, count, logTreshold);
		}
	}

	private void formatLine(TableReportBuilder trb, TreeCount sc, double logTreshold) {
		int count = sc.count;
		double totalTime = ((double) sc.elapsed) / 1000000;
		double maxTime = ((double) sc.maxElapsed) / 1000000;
		double avgTime = totalTime / count;
		if (logTreshold <= 0 || totalTime >= logTreshold) {
			trb.appendLine(sc.getPrefixedName(), count, String.format("%.1f", totalTime), String.format("%.1f", maxTime),
					String.format("%.3f", avgTime));
		}
	}
}

package de.akquinet.android.androlog.reporter.converter;

import de.akquinet.android.androlog.reporter.Report;

/**
 * Converts a report to a string. This string will be sent by the configured
 * reporter.
 * 
 * @author Marcell_Kovacs
 * 
 */
public interface ReportConverter {
	/**
	 * Converts the report to string.
	 * 
	 * @param report
	 *            The report to convert. Must not be null.
	 * @return The resulting string.
	 */
	String convertReport(Report report);
}

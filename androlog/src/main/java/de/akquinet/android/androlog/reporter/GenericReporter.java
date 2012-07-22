package de.akquinet.android.androlog.reporter;

import java.net.URL;
import java.util.Properties;

import de.akquinet.android.androlog.reporter.converter.ReportConverter;

/**
 * Generic reporter provides the base functionality to select a converter to
 * convert the system's current state to a string representation.
 * 
 * @author Marcell_Kovacs
 * 
 */
public abstract class GenericReporter implements Reporter {
	/**
	 * Mandatory Property to set the <tt>converter class</tt> for converting the
	 * report to string.
	 */
	final static String DEFAULT_CONVERTER_KEY = "androlog.reporter.converter";
	/**
	 * The default ReportConverter.
	 */
	final static String DEFAULT_CONVERTER = "de.akquinet.android.androlog.reporter.converter.SimpleJsonConverter";
	private ReportConverter converter;
	private String defaultConverterName;

	public GenericReporter() {
		defaultConverterName = DEFAULT_CONVERTER;
	}

	/**
	 * Default converter name can be overridden.
	 * Must be called before configure runs.
	 * @param defaultConverterName The default converter name.
	 */
	protected void setDefaultConverterName(String defaultConverterName) {
		this.defaultConverterName = defaultConverterName;
	}

	/**
	 * Configures the POST Reporter. The given configuration <b>must</b> contain
	 * the {@link PostReporter#ANDROLOG_REPORTER_POST_URL} property and it must
	 * be a valid {@link URL}.
	 * 
	 * @see de.akquinet.android.androlog.reporter.Reporter#configure(java.util.Properties)
	 */
	public void configure(Properties configuration) {
		configureConverter(configuration);
	}

	private void configureConverter(Properties configuration) {
		String converterNameProperty = configuration
				.getProperty(DEFAULT_CONVERTER_KEY);

		if (converterNameProperty != null) {
			defaultConverterName = converterNameProperty;
		}

		converter = (ReportConverter) GenericFactory
				.newInstance(defaultConverterName);
	}

	/**
	 * Returns the current {@link ReportConverter}.
	 * 
	 * @return The converter.
	 */
	protected ReportConverter getConverter() {
		return converter;
	}
}
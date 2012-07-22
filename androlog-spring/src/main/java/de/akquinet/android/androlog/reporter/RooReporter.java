package de.akquinet.android.androlog.reporter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import de.akquinet.android.androlog.Log;

/**
 * Sends a Spring Roo Entity compatible POST using {@link PostReporter}'s post
 * method, which sends the report JSON String in Request body.
 * 
 * @author Marcell_Kovacs
 * 
 */
public class RooReporter implements Reporter {
	private static final String DEFAULT_ROO_CONVERTER = "de.akquinet.android.androlog.reporter.converter.RooCompatibleJsonConverter";
	private PostReporter reporter;
	private RestTemplate template;

	/**
	 * Initializes the reporter.
	 */
	public RooReporter() {
		template = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = createConverters();
		template.setMessageConverters(messageConverters);
		reporter = new PostReporter();
		reporter.setDefaultConverterName(DEFAULT_ROO_CONVERTER);
	}

	private List<HttpMessageConverter<?>> createConverters() {
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>(1);
		converters.add(new StringHttpMessageConverter());
		return converters ;
	}

	@Override
	public void configure(Properties configuration) {
		reporter.configure(configuration);
	}

	@Override
	public boolean send(Context context, String message, Throwable error) {
		boolean result = sendReport(new Report(context, message, error));
		return result;
	}

	private boolean sendReport(Report report) {
		boolean result = true;
		String convertReport = reporter.getConverter().convertReport(report);
		try {
			
			template.postForObject(reporter.getUrl().toURI(), convertReport, String.class);
		} catch (RestClientException e) {
			result = false;
			Log.d(this, "Can not send message, " + convertReport, e);
		} catch (URISyntaxException e) {
			result = false;
			Log.d(this, "Can not send message, " + convertReport, e);
		}
		return result;
	}
}

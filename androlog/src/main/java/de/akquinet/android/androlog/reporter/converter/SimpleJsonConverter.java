package de.akquinet.android.androlog.reporter.converter;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;
import de.akquinet.android.androlog.Log;
import de.akquinet.android.androlog.LogHelper;
import de.akquinet.android.androlog.reporter.Report;

public class SimpleJsonConverter implements ReportConverter {

	@Override
	public String convertReport(Report report) {
		return getReportAsJSON(report).toString();
	}

	/**
	 * Creates the report as a JSON Object.
	 * 
	 * @return the json object containing the report.
	 */
	public JSONObject getReportAsJSON(Report report) {
		JSONObject reportJson = new JSONObject();

		try {
			addReportHeader(reportJson, report);
			addApplicationData(reportJson, report);
			addDeviceData(reportJson, report);
			addLog(reportJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return reportJson;
	}

	/**
	 * Adds the log entries to the report.
	 * 
	 * @param report
	 *            the report
	 * @throws JSONException
	 *             if the log entries cannot be added
	 */
	private void addLog(JSONObject report) throws JSONException {
		JSONObject logs = new JSONObject();
		List<String> list = Log.getReportedEntries();
		if (list != null) {
			logs.put("numberOfEntry", list.size());
			JSONArray array = new JSONArray();
			for (String s : list) {
				array.put(s);
			}
			logs.put("log", array);
		}
		report.put("log", logs);
	}

	/**
	 * Adds the device data to the report.
	 * 
	 * @param reportJson
	 *            the report
	 * @throws JSONException
	 *             if the device data cannot be added
	 */
	private void addDeviceData(JSONObject reportJson, Report report)
			throws JSONException {
		JSONObject device = new JSONObject();
		device.put("device", Build.DEVICE);
		device.put("brand", Build.BRAND);

		Object windowService = report.getContext().getSystemService(
				Context.WINDOW_SERVICE);
		if (windowService instanceof WindowManager) {
			Display display = ((WindowManager) windowService)
					.getDefaultDisplay();
			device.put("resolution",
					display.getWidth() + "x" + display.getHeight());
			device.put("orientation", display.getOrientation());
		}
		device.put("display", Build.DISPLAY);
		device.put("manufacturer", Build.MANUFACTURER);
		device.put("model", Build.MODEL);
		device.put("product", Build.PRODUCT);
		device.put("build.type", Build.TYPE);
		device.put("android.version", Build.VERSION.SDK_INT);
		reportJson.put("device", device);
	}

	/**
	 * Adds the application data to the report.
	 * 
	 * @param reportJson
	 *            the report
	 * @throws JSONException
	 *             if the application data cannot be added
	 */
	private void addApplicationData(JSONObject reportJson, Report report)
			throws JSONException {
		JSONObject app = new JSONObject();
		Context context = report.getContext();
		app.put("package", context.getPackageName());
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			app.put("versionCode", info.versionCode);
			app.put("versionName", info.versionName);
			// TODO firstInstallTime and lastUpdate
		} catch (NameNotFoundException e) {
			// Cannot happen as we're checking a know package.
		}
		reportJson.put("application", app);
	}

	/**
	 * Adds the report header to the report (dates, locale, message, errors
	 * ...).
	 * 
	 * @param reportJson
	 *            the report
	 * @throws JSONException
	 *             if the data cannot be added
	 */
	private void addReportHeader(JSONObject reportJson, Report report)
			throws JSONException {
		JSONObject dates = new JSONObject();
		dates.put("date.system", System.currentTimeMillis());
		dates.put("date", new Date().toString());
		dates.put("locale", Locale.getDefault());
		reportJson.put("dates", dates);
		String message = report.getMessage();
		if (message != null) {
			reportJson.put("message", message);
		}
		Throwable err = report.getErr();
		if (err != null) {
			reportJson.put("error", err.getMessage());
			reportJson.put("stackTrace", LogHelper.getStackTraceString(err));
			if (err.getCause() != null) {
				reportJson.put("cause", err.getCause().getMessage());
				reportJson.put("cause.stackTrace",
						LogHelper.getStackTraceString(err.getCause()));
			}

		}
	}
}

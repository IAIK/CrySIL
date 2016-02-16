package org.crysil.communications.http;

import java.io.IOException;

import org.crysil.communications.json.GSonHelper;
import org.crysil.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.Gson;

/**
 * encapsulates tools to read, validate and write JSON
 */
public class JsonUtils {

	/**
	 * Checks if is valid json.
	 *
	 * @param jsonString the json string
	 * @param jsonSchema the json schema
	 * @return true, if is valid json
	 */
	public static boolean isValidJSON(String jsonString, JsonNode jsonSchema) {
		JsonNode data;

		try {
			data = JsonLoader.fromString(jsonString);
		} catch (IOException e) {
			Logger.error("isValidJSON exception:", e);
			return false;
		}

		ProcessingReport report = JsonSchemaFactory.byDefault().getValidator().validateUnchecked(jsonSchema, data);

		if (!report.isSuccess()) {
			Logger.debug("incoming request: {}", jsonString);
			Logger.debug("report: {}", report.toString());
		}

		return report.isSuccess();
	}

	/**
	 * Parses the string to object.
	 *
	 * @param <T> the generic type
	 * @param jsonString the json string
	 * @param objectType the object type
	 * @return the t
	 */
	public static <T> T fromJson(String jsonString, Class<T> objectType) {
		return GSonHelper.fromJson(jsonString, objectType);
	}

	/**
	 * Parses the object to string.
	 *
	 * @param jsonObject the json object
	 * @return the string
	 */
	public static String toJson(Object jsonObject) {
		return new Gson().toJson(jsonObject);
	}
}

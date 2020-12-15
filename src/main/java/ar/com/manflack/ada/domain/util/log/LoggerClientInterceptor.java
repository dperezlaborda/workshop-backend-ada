package ar.com.manflack.ada.domain.util.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ar.com.manflack.ada.domain.util.security.SecurityUtil;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application")
public class LoggerClientInterceptor implements ClientHttpRequestInterceptor
{

	static final Logger log = LoggerFactory.getLogger(LoggerClientInterceptor.class);

	private String[] keys = { "password", "accessToken", "clave", "pass", "secret", "newPassword", "Connection-string",
			"connection-string", "access_token", "apiKey", "Authorization" };
	private String[] protectedKeys = { "numero", "card_number", "security_code", "number" };

	@Value("${logging.wiped.keys: ''}")
	private String[] customKeys;

	@Value("${logging.protected.keys: ''}")
	private String[] customProtectedKeys;

	private static final AtomicInteger ID = new AtomicInteger();

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException
	{
		String id = Integer.toString(ID.incrementAndGet());
		traceRequest(request, body, id);
		ClientHttpResponse response = execution.execute(request, body);
		traceResponse(response, id);
		return response;
	}

	private void traceRequest(HttpRequest request, byte[] body, String id) throws IOException
	{
		ClientLogEntry logEntry = new ClientLogEntry();
		logEntry.setType("Outbound Message");
		logEntry.setId(id);
		logEntry.setAddress(request.getURI().toString());
		logEntry.setMethod(request.getMethod().name());
		HttpHeaders headers = request.getHeaders();
		HttpHeaders headersString = new HttpHeaders();
		for (String key : headers.keySet())

		{
			List<String> value = new ArrayList<>();
			value.add(wipeHeader(key, headers.get(key).get(0)));
			headersString.put(key, value);
		}

		logEntry.setHeaders(headersString.toString());
		logEntry.setPayload(wipeData(new String(body, StandardCharsets.UTF_8)));

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		log.info(ow.writeValueAsString(logEntry));
	}

	private void traceResponse(ClientHttpResponse response, String id) throws IOException
	{
		StringBuilder inputStringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
		String line = bufferedReader.readLine();
		while (line != null)
		{
			inputStringBuilder.append(line);
			line = bufferedReader.readLine();
		}

		ClientLogEntry logEntry = new ClientLogEntry();
		logEntry.setType("Inbound Message");
		logEntry.setId(id);
		logEntry.setResponseCode(response.getStatusCode().toString());
		if (StringUtils.isNotBlank(response.getStatusText()))
			logEntry.setResponseText(response.getStatusText());

		HttpHeaders headers = response.getHeaders();
		HttpHeaders headersString = new HttpHeaders();
		for (String key : headers.keySet())
		{
			List<String> value = new ArrayList<>();
			value.add(wipeHeader(key, headers.get(key).get(0)));
			headersString.put(key, value);
		}

		logEntry.setHeaders(headersString.toString());

		logEntry.setPayload(wipeData(inputStringBuilder.toString()));

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		log.info(ow.writeValueAsString(logEntry));
	}

	@SuppressWarnings("unchecked")
	private String wipeHeader(String key, String header)
	{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(key.toLowerCase(), header);
		wipe(jsonObj);
		return jsonObj.get(key.toLowerCase()).toString();
	}

	private String wipeData(String json)
	{
		Object aux = null;
		try
		{
			aux = new JSONParser().parse(json);

			if (aux instanceof JSONObject)
			{
				JSONObject jsonObj = (JSONObject) aux;
				wipe(jsonObj);

				return jsonObj.toJSONString();
			}
			else if (aux instanceof JSONArray)
			{
				wipeArray((JSONArray) aux);
				return ((JSONArray) aux).toJSONString();
			}
		}
		catch (ParseException e)
		{
			return json;
		}
		return json;
	}

	private void wipeArray(JSONArray jsonArray)
	{
		for (int i = 0; i < jsonArray.size(); i++)
		{
			if (jsonArray.get(i) instanceof JSONObject)
			{
				wipe((JSONObject) jsonArray.get(i));
			}
			else
			{
				if (jsonArray.get(i) instanceof JSONArray)
				{
					wipeArray((JSONArray) jsonArray.get(i));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void wipe(JSONObject jsonObj)
	{
		if (keys != null && keys.length > 0)
		{
			for (String clave : keys)
			{
				if (jsonObj.containsKey(clave.toLowerCase()))
				{
					jsonObj.put(clave.toLowerCase(), "[WIPED]");
				}
			}
		}
		if (protectedKeys != null && protectedKeys.length > 0)
		{
			for (String clave : protectedKeys)
			{
				if (jsonObj.containsKey(clave.toLowerCase()))
				{
					jsonObj.put(clave.toLowerCase(), SecurityUtil.protect(Objects.toString(jsonObj.get(clave))));
				}
			}
		}
		if (customKeys != null && customKeys.length > 0)
		{
			for (String clave : customKeys)
			{
				if (jsonObj.containsKey(clave.toLowerCase()))
				{
					jsonObj.put(clave.toLowerCase(), "[WIPED]");
				}
			}
		}
		if (customProtectedKeys != null && customProtectedKeys.length > 0)
		{
			for (String clave : customProtectedKeys)
			{
				if (jsonObj.containsKey(clave.toLowerCase()))
				{
					jsonObj.put(clave.toLowerCase(), SecurityUtil.protect(Objects.toString(jsonObj.get(clave))));
				}
			}
		}
		for (Object key : jsonObj.keySet())
		{
			String keyStr = (String) key;
			if (jsonObj.get(keyStr) instanceof JSONObject)
			{
				wipe((JSONObject) jsonObj.get(keyStr));
			}
			if (jsonObj.get(keyStr) instanceof JSONArray)
			{
				wipeArray((JSONArray) jsonObj.get(keyStr));
			}
		}
	}

}

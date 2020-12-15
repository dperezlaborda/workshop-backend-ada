package ar.com.manflack.ada.domain.util.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ar.com.manflack.ada.domain.util.log.Record;
import ar.com.manflack.ada.domain.util.security.SecurityUtil;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application")
public class LoggingFilter implements Filter
{

	private String[] keys = { "password", "accessToken", "clave", "pass", "secret", "newPassword", "Connection-string",
			"connection-string", "access_token", "apiKey", "Authorization" };
	private String[] protectedKeys = { "numero", "card_number", "security_code", "number" };
	private String[] urls = { "health", "swagger", "favicon", "api-docs", "actuator" };

	@Value("${logging.wiped.keys: ''}")
	private String[] customKeys;

	@Value("${logging.protected.keys: ''}")
	private String[] customProtectedKeys;

	@Value("${logging.skip.urls: ''}")
	private String[] customUrls;

	private final Log log = LogFactory.getLog(this.getClass());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		Record record = new Record();
		try
		{
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;

			record.setInDate(new Date());

			record.setRemoteAddress(httpServletRequest.getRemoteAddr());
			record.setPath(httpServletRequest.getRequestURI());
			record.setMethod(httpServletRequest.getMethod());

			StringBuilder headers = new StringBuilder("");
			Enumeration headerNames = httpServletRequest.getHeaderNames();
			while (headerNames.hasMoreElements())
			{
				String key = (String) headerNames.nextElement();
				headers.append("[");
				headers.append(key);
				headers.append(":");
				headers.append(wipeHeader(key, httpServletRequest.getHeader(key)));
				headers.append("]");
			}
			record.setHeaders(headers.toString());

			StringBuilder queryParameters = new StringBuilder("");
			Enumeration queryParametersNames = httpServletRequest.getParameterNames();
			while (queryParametersNames.hasMoreElements())
			{
				String key = (String) queryParametersNames.nextElement();
				queryParameters.append("[");
				queryParameters.append(key);
				queryParameters.append(":");
				queryParameters.append(request.getParameter(key));
				queryParameters.append("]");
			}
			record.setQueryParameters(queryParameters.toString());

			BufferedRequestWrapper bufferedRequest = new BufferedRequestWrapper(httpServletRequest);
			if (bufferedRequest.getRequestBody() != null && StringUtils.isNotBlank(bufferedRequest.getRequestBody()))
			{
				String data = wipeData(bufferedRequest.getRequestBody(), bufferedRequest.getContentType());
				record.setRequest(data);
			}
			else
			{
				record.setRequest("");
			}

			BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(httpServletResponse);
			chain.doFilter(bufferedRequest, bufferedResponse);

			record.setOutDate(new Date());
			record.setStatus(httpServletResponse.getStatus());

			try
			{
				if (bufferedResponse.getContent() != null && StringUtils.isNotBlank(bufferedResponse.getContent()))
				{
					String data = wipeData(bufferedResponse.getContent(), bufferedResponse.getContentType());
					record.setResponse(data);
				}
				else
				{
					record.setResponse("");
				}
			}
			catch (Exception e)
			{
				record.setResponse("");

			}
			bufferedResponse.getHeaderNames();
			if (validUrl(httpServletRequest.getRequestURI()))
			{
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				log.info(ow.writeValueAsString(record));
			}
		}
		catch (Throwable a)
		{
		}

	}

	@SuppressWarnings("unchecked")
	private Object wipeHeader(String key, String header)
	{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(key, header);
		wipe(jsonObj);
		return jsonObj.get(key);
	}

	private boolean validUrl(String path)
	{
		if (urls != null && urls.length > 0)
		{
			for (String url : urls)
			{
				if (path.contains(url))
				{
					return false;
				}
			}
		}
		if (customUrls != null && customUrls.length > 0)
		{
			for (String url : customUrls)
			{
				if (path.contains(url))
				{
					return false;
				}
			}
		}
		return true;
	}

	private String wipeData(String content, String contentType) throws ParseException
	{
		Object aux = null;
		switch (contentType)
		{
			case "application/json;charset=UTF-8":
			case "application/json;":
			case "application/json":
				aux = new JSONParser().parse(content);

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
				break;

			default:
				break;
		}
		return content;
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
				if (jsonObj.containsKey(clave))
				{
					jsonObj.put(clave, "[WIPED]");
				}
			}
		}
		if (protectedKeys != null && protectedKeys.length > 0)
		{
			for (String clave : protectedKeys)
			{
				if (jsonObj.containsKey(clave))
				{
					jsonObj.put(clave, SecurityUtil.protect(Objects.toString(jsonObj.get(clave))));
				}
			}
		}
		if (customKeys != null && customKeys.length > 0)
		{
			for (String clave : customKeys)
			{
				if (jsonObj.containsKey(clave))
				{
					jsonObj.put(clave, "[WIPED]");
				}
			}
		}
		if (customProtectedKeys != null && customProtectedKeys.length > 0)
		{
			for (String clave : customProtectedKeys)
			{
				if (jsonObj.containsKey(clave))
				{
					jsonObj.put(clave, SecurityUtil.protect(Objects.toString(jsonObj.get(clave))));
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

	@Override
	public void destroy()
	{
	}

	private static final class BufferedRequestWrapper extends HttpServletRequestWrapper
	{

		private ByteArrayOutputStream baos = null;
		private byte[] buffer = null;

		public BufferedRequestWrapper(HttpServletRequest req) throws IOException
		{
			super(req);
			// Read InputStream and store its content in a buffer.
			InputStream is = req.getInputStream();
			this.baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int read;
			while ((read = is.read(buf)) > 0)
			{
				this.baos.write(buf, 0, read);
			}
			this.buffer = this.baos.toByteArray();
		}

		@Override
		public ServletInputStream getInputStream()
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(this.buffer);
			return new BufferedServletInputStream(bais);
		}

		String getRequestBody() throws IOException
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
			String line = null;
			StringBuilder inputBuffer = new StringBuilder();
			do
			{
				line = reader.readLine();
				if (null != line)
				{
					inputBuffer.append(line.trim());
				}
			} while (line != null);
			reader.close();
			return inputBuffer.toString().trim();
		}

	}

	private static final class BufferedServletInputStream extends ServletInputStream
	{

		private ByteArrayInputStream bais;

		public BufferedServletInputStream(ByteArrayInputStream bais)
		{
			this.bais = bais;
		}

		@Override
		public int available()
		{
			return this.bais.available();
		}

		@Override
		public int read()
		{
			return this.bais.read();
		}

		@Override
		public int read(byte[] buf, int off, int len)
		{
			return this.bais.read(buf, off, len);
		}

		@Override
		public boolean isFinished()
		{
			return false;
		}

		@Override
		public boolean isReady()
		{
			return true;
		}

		@Override
		public void setReadListener(ReadListener readListener)
		{

		}
	}

	public class TeeServletOutputStream extends ServletOutputStream
	{

		private final TeeOutputStream targetStream;

		public TeeServletOutputStream(OutputStream one, OutputStream two)
		{
			targetStream = new TeeOutputStream(one, two);
		}

		@Override
		public void write(int arg0) throws IOException
		{
			this.targetStream.write(arg0);
		}

		@Override
		public void flush() throws IOException
		{
			super.flush();
			this.targetStream.flush();
		}

		@Override
		public void close() throws IOException
		{
			super.close();
			this.targetStream.close();
		}

		@Override
		public boolean isReady()
		{
			return false;
		}

		@Override
		public void setWriteListener(WriteListener writeListener)
		{

		}
	}

	public class BufferedResponseWrapper implements HttpServletResponse
	{

		HttpServletResponse original;
		TeeServletOutputStream tee;
		ByteArrayOutputStream bos;

		public BufferedResponseWrapper(HttpServletResponse response)
		{
			original = response;
		}

		public String getContent()
		{
			return bos.toString();
		}

		public PrintWriter getWriter() throws IOException
		{
			return original.getWriter();
		}

		public ServletOutputStream getOutputStream() throws IOException
		{
			if (tee == null)
			{
				bos = new ByteArrayOutputStream();
				tee = new TeeServletOutputStream(original.getOutputStream(), bos);
			}
			return tee;

		}

		@Override
		public String getCharacterEncoding()
		{
			return original.getCharacterEncoding();
		}

		@Override
		public String getContentType()
		{
			return original.getContentType();
		}

		@Override
		public void setCharacterEncoding(String charset)
		{
			original.setCharacterEncoding(charset);
		}

		@Override
		public void setContentLength(int len)
		{
			original.setContentLength(len);
		}

		@Override
		public void setContentLengthLong(long l)
		{
			original.setContentLengthLong(l);
		}

		@Override
		public void setContentType(String type)
		{
			original.setContentType(type);
		}

		@Override
		public void setBufferSize(int size)
		{
			original.setBufferSize(size);
		}

		@Override
		public int getBufferSize()
		{
			return original.getBufferSize();
		}

		@Override
		public void flushBuffer() throws IOException
		{
			tee.flush();
		}

		@Override
		public void resetBuffer()
		{
			original.resetBuffer();
		}

		@Override
		public boolean isCommitted()
		{
			return original.isCommitted();
		}

		@Override
		public void reset()
		{
			original.reset();
		}

		@Override
		public void setLocale(Locale loc)
		{
			original.setLocale(loc);
		}

		@Override
		public Locale getLocale()
		{
			return original.getLocale();
		}

		@Override
		public void addCookie(Cookie cookie)
		{
			original.addCookie(cookie);
		}

		@Override
		public boolean containsHeader(String name)
		{
			return original.containsHeader(name);
		}

		@Override
		public String encodeURL(String url)
		{
			return original.encodeURL(url);
		}

		@Override
		public String encodeRedirectURL(String url)
		{
			return original.encodeRedirectURL(url);
		}

		@Override
		public String encodeUrl(String url)
		{
			return original.encodeURL(url);
		}

		@Override
		public String encodeRedirectUrl(String url)
		{
			return original.encodeRedirectURL(url);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException
		{
			original.sendError(sc, msg);
		}

		@Override
		public void sendError(int sc) throws IOException
		{
			original.sendError(sc);
		}

		@Override
		public void sendRedirect(String location) throws IOException
		{
			original.sendRedirect(location);
		}

		@Override
		public void setDateHeader(String name, long date)
		{
			original.setDateHeader(name, date);
		}

		@Override
		public void addDateHeader(String name, long date)
		{
			original.addDateHeader(name, date);
		}

		@Override
		public void setHeader(String name, String value)
		{
			original.setHeader(name, value);
		}

		@Override
		public void addHeader(String name, String value)
		{
			original.addHeader(name, value);
		}

		@Override
		public void setIntHeader(String name, int value)
		{
			original.setIntHeader(name, value);
		}

		@Override
		public void addIntHeader(String name, int value)
		{
			original.addIntHeader(name, value);
		}

		@Override
		public void setStatus(int sc)
		{
			original.setStatus(sc);
		}

		@Override
		public void setStatus(int sc, String sm)
		{
			original.setStatus(sc);
		}

		@Override
		public String getHeader(String arg0)
		{
			return original.getHeader(arg0);
		}

		@Override
		public Collection<String> getHeaderNames()
		{
			return original.getHeaderNames();
		}

		@Override
		public Collection<String> getHeaders(String arg0)
		{
			return original.getHeaders(arg0);
		}

		@Override
		public int getStatus()
		{
			return original.getStatus();
		}

	}
}

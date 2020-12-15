package ar.com.manflack.ada.domain.util.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class ApiOriginFilter extends OncePerRequestFilter
{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException
	{
		FilterConfig config = getFilterConfig();
		String allowOrigin = config.getInitParameter("allowOrigin");
		String maxAge = config.getInitParameter("maxAge");

		if ("OPTIONS".equalsIgnoreCase(request.getMethod()))
		{
			String requestedMethod = request.getHeader("Access-Control-Request-Method");
			String requestedHeaders = request.getHeader("Access-Control-Request-Headers");

			response.reset();
			response.setHeader("Access-Control-Allow-Origin", allowOrigin);
			response.setHeader("Access-Control-Allow-Methods", requestedMethod);
			response.setHeader("Access-Control-Allow-Headers", requestedHeaders);
			response.setHeader("Access-Control-Max-Age", maxAge);
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		else
		{
			response.setHeader("Access-Control-Allow-Origin", allowOrigin);
			response.setHeader("Access-Control-Allow-Methods", "*");
			response.setHeader("Access-Control-Allow-Headers", "*");
			response.setHeader("Access-Control-Max-Age", maxAge);
		}

		filterChain.doFilter(request, response);
	}
}
package com.joseluisestevez.msa.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class PostElapsedTimeFilter extends ZuulFilter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PostElapsedTimeFilter.class);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		LOGGER.info("Entering the post filter");
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		Long startTime = (Long) request.getAttribute("startTime");
		Long endTime = System.currentTimeMillis();

		Long elapsedTime = endTime - startTime;
		LOGGER.info("elapsed time in ms [{}]", elapsedTime);
		LOGGER.info("elapsed time in seconds [{}]",
				elapsedTime.doubleValue() / 1000);
		return null;
	}

	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

}

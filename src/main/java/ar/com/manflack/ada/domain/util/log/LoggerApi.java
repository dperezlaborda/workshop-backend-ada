package ar.com.manflack.ada.domain.util.log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ar.com.manflack.ada.domain.exception.GenericException;
import ar.com.manflack.ada.domain.util.security.SecurityUtil;

@Component
@Aspect
public class LoggerApi
{
    private final Log log = LogFactory.getLog(this.getClass());

	@Around("execution(* ar.com.manflack..*app.rest..*(..))")
	public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable
	{
		GenericException ex = null;
		Object retVal = null;
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

        try
        {
            retVal = joinPoint.proceed();
        }
        catch (GenericException e)
        {
            ex = e;
        }

        stopWatch.stop();

        LogEntry logEntry = new LogEntry();
        logEntry.setClassName(joinPoint.getTarget().getClass().getSimpleName());
        logEntry.setMethod(joinPoint.getSignature().getName());

        MethodSignature methodSignature = (MethodSignature) joinPoint.getStaticPart().getSignature();
        Method method = methodSignature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        // append args
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++)
        {
            if (parameterAnnotations[i].length > 0 && parameterAnnotations[i][0] instanceof Protected)
            {
            	logEntry.getParameters().add(SecurityUtil.protect(Objects.toString(args[i])));
            }
            else if (parameterAnnotations[i].length > 0 && parameterAnnotations[i][0] instanceof Ignored)
            {
            	logEntry.getParameters().add("");
            }
            else
            {
            	logEntry.getParameters().add(Objects.toString(args[i]));
            }
        }

        if (ex != null)
        {
            logEntry.setErrorCode(ex.getErrorCode());
        }
        logEntry.setDuration(stopWatch.getTotalTimeMillis());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        if (ex != null)
        {
        	log.error(ow.writeValueAsString(logEntry));
            throw ex;
        }
        else
        {
        	log.info(ow.writeValueAsString(logEntry));

        }
            return retVal;
    }
}

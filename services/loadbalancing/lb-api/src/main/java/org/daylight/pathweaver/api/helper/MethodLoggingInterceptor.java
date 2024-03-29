package org.daylight.pathweaver.api.helper;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MethodLoggingInterceptor {
    private final Logger logger = Logger.getLogger(MethodLoggingInterceptor.class);

    @Pointcut("within(org.daylight.pathweaver.api.resource..*)")
    public void resources() {
    }

    @Pointcut("within(org.daylight.pathweaver.service.domain.service..*)")
    public void services() {
    }

    @Pointcut("within(org.daylight.pathweaver.plugin..*)")
    public void plugins() {
    }

    @Around("execution(!private * org.daylight.pathweaver.api.resource.*.*(..))")
    public Object profileResource(ProceedingJoinPoint pjp) throws Throwable {
        return timerLog(pjp);
    }

    @Around("execution(!private * org.daylight.pathweaver.service.domain.service.*.*(..))")
    public Object profileService(ProceedingJoinPoint pjp) throws Throwable {
        return timerLog(pjp);
    }

    @Around("execution(!private * org.daylight.pathweaver.plugin.*.*(..))")
    public Object profilePlugin(ProceedingJoinPoint pjp) throws Throwable {
        return timerLog(pjp);
    }

    private Object timerLog(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        String className = pjp.getTarget().getClass().getName();

        // retrieve the runtime method arguments (dynamic)
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }

            String fullParameterClassName = arg.getClass().getName();
            String parameterClassName = fullParameterClassName.substring(fullParameterClassName.lastIndexOf('.') +1, fullParameterClassName.length());
            sb.append(parameterClassName).append(" ").append(arg);
            sb.append(", ");
        }
        String sbString = sb.toString();
        String arguments = null;
        if(sb.toString().length() >= 3) {
            arguments = sbString.substring(0, sbString.length()-2);
            arguments = arguments + ")";
        } else {
            arguments = sbString + ")";
        }

        long start = System.currentTimeMillis();
        logger.debug("Entering: " + className + "." + methodName + arguments);

        // retrieve the methods parameter types (static):
        final Signature signature = pjp.getStaticPart().getSignature();
        if (signature instanceof MethodSignature) {
            final MethodSignature ms = (MethodSignature) signature;
            final Class<?>[] parameterTypes = ms.getParameterTypes();
            for (final Class<?> pt : parameterTypes) {
                logger.debug("Parameter type:" + pt);
            }
        }


        Object output = pjp.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        logger.debug("Leaving: " + className + "." + methodName + arguments + " Total Time taken in ms: " + elapsedTime);
        return output;
    }


}

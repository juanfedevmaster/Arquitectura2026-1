package com.example.middleware.aspect;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Audit Log Service — AOP (@Aspect)
 * Componente transversal que intercepta TODOS los métodos de la capa service.
 * Registra:
 *   - Entrada y salida de cada método (SLF4J/Logback)
 *   - Tiempo de ejecución
 *   - Errores
 *   - Métricas de latencia via Micrometer (expuestas en /actuator/prometheus)
 */
@Aspect
@Component
public class AuditLogAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    private final MeterRegistry meterRegistry;

    public AuditLogAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Intercepta todos los métodos públicos de la capa service y channel.
     */
    @Around("execution(* com.example.middleware.service..*(..)) || " +
            "execution(* com.example.middleware.channel..*(..))")
    public Object auditAndMeasure(ProceedingJoinPoint pjp) throws Throwable {
        String className  = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        String metricKey  = "middleware." + className.toLowerCase() + "." + methodName.toLowerCase();

        log.debug("[AUDIT] → {}.{}()", className, methodName);

        Timer.Sample sample = Timer.start(meterRegistry);
        long start = System.currentTimeMillis();

        try {
            Object result = pjp.proceed();
            long elapsed = System.currentTimeMillis() - start;

            log.info("[AUDIT] ✓ {}.{}() — {}ms", className, methodName, elapsed);
            sample.stop(meterRegistry.timer(metricKey, "status", "success"));

            return result;

        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[AUDIT] ✗ {}.{}() — {}ms — error: {}",
                      className, methodName, elapsed, ex.getMessage());
            sample.stop(meterRegistry.timer(metricKey, "status", "error"));
            throw ex;
        }
    }
}

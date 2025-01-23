package com.server.concert_reservation.support.api.common.aop;

import com.server.concert_reservation.support.api.common.aop.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DistributedLockAspect {
    private static final String LOCK_PREFIX = "LOCK";
    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.server.concert_reservation.support.api.common.aop.annotation.DistributedLock)")
    public Object applyLock(ProceedingJoinPoint joinPoint) throws Throwable {
        // 메서드 및 어노테이션 정보 추출
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock lockAnnotation = method.getAnnotation(DistributedLock.class);

        // key List 생성
        List<String> lockKeys = getLockKeys(signature.getParameterNames(), joinPoint.getArgs(), lockAnnotation);
        if (lockKeys.isEmpty()) { // key가 없다면 Lock을 걸지 않고 메서드 실행 (불필요 작업 방지)
            log.warn("Lock keys are empty.");
            return joinPoint.proceed();
        }

        RLock lock = createLock(lockKeys);

        try {
            boolean lockAcquired = lock.tryLock(lockAnnotation.waitTime(), lockAnnotation.leaseTime(), TimeUnit.MILLISECONDS);
            if (!lockAcquired) { // 락 획득 실패 했다면
                log.warn("lock acquisition failed keys : {}", lockKeys);
                return false;
            }

            log.info("Lock acquired successfully: {}", lockKeys);
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupted during lock acquisition", e);
        } finally {
            releaseLock(lock, lockKeys);
        }
    }

    private List<String> getLockKeys(String[] parameterNames, Object[] args, DistributedLock lockAnnotation) {
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        List<Object> lockKeys = parser.parseExpression(lockAnnotation.key()).getValue(context, List.class);

        return lockKeys.stream()
                .map(key -> String.format("%s:%s:%s", LOCK_PREFIX, lockAnnotation.prefix(), key.toString()))
                .collect(Collectors.toList());
    }

    private RLock createLock(List<String> lockKeys) {
        if (lockKeys.size() == 1) { // key가 1개면 단일락 적용
            return redissonClient.getLock(lockKeys.get(0));
        }

        // key가 여러개인 경우 멀티락 적용
        List<RLock> locks = lockKeys.stream()
                .map(redissonClient::getLock)
                .collect(Collectors.toList());

        return new RedissonMultiLock(locks.toArray(new RLock[0]));
    }

    private void releaseLock(RLock lock, List<String> lockKeys) {
        try {
            lock.unlock();
            log.info("unlock successfully: {}", lockKeys);
        } catch (IllegalMonitorStateException e) {
            log.warn("already unlock or not held : {}", lockKeys);
        }
    }
}

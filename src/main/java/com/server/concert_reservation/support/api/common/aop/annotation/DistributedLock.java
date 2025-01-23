package com.server.concert_reservation.support.api.common.aop.annotation;

import java.util.concurrent.TimeUnit;

public @interface DistributedLock {

    // Lock 고유 식별 접두사
    String prefix();

    // Lock Key 값
    String key();

    // Lock의 시간 단위 (default ms)
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    // Lock 최대 대기시간 (default 5000ms)
    long waitTime() default 5000;

    // Lock 최대 점유시간 (default 3000ms)
    // 해당 시간이 지나면 자동으로 Lock 해제
    long leaseTime() default 3000;

}

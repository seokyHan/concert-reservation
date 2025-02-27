import http from 'k6/http';
import {check, fail, sleep} from 'k6';

export const options = {
    scenarios: {
        booking_scenario: {
            executor: 'constant-arrival-rate',
            rate: 3000, // 초당 요청 속도 (TPS)
            timeUnit: '1s',
            duration: '5m', // 테스트 지속 시간: 5분
            preAllocatedVUs: 2000, // 최대 가상 사용자 수
            maxVUs: 2400, // 최대 VUs (20% 여유)
        },
    },
};


// 0.5초 ~ 3초 사이의 값을 반환
function getRandomWaitTime(min = 0.5, max = 3) {
    return Math.random() * (max - min) + min;
}

export default function () {
    const concertId = 1;
    const token = 'test-token';
    const userId = Math.floor(Math.random() * 1000) + 1;

    // Step 1: 예약 가능 콘서트 스케쥴 조회
    const scheduleResponse = http.get(
        `http://host.docker.internal:8080/api/v1/concert/${concertId}/available-schedules`,
        {
            headers: {
                'Content-Type': 'application/json',
                'X-Waiting-Token': token
            },
        }
    );

    if (!check(scheduleResponse, {'Schedule list status is 200': (r) => r.status === 200})) {
        fail(`Schedule list request failed with status: ${scheduleResponse.status}`);
        return;
    }

    sleep(getRandomWaitTime());

    const schedules = scheduleResponse.json().concertSchedules;
    if (!schedules || schedules.length === 0) {
        fail('No schedules available for booking.');
        return;
    }

    let randomIndex = Math.floor(Math.random() * schedules.length);
    const concertScheduleId = schedules[randomIndex].id;

    // Step 2: 예약 가능한 좌석 조회
    const seatResponse = http.get(
        `http://host.docker.internal:8080/api/v1/concert/${concertScheduleId}/available-seats`,
        {
            headers: {
                'Content-Type': 'application/json',
                'X-Waiting-Token': token
            },
        }
    );

    if (!check(seatResponse, {'Seat list status is 200': (r) => r.status === 200})) {
        fail(`Seat list request failed with status: ${seatResponse.status}`);
    }
    sleep(getRandomWaitTime()); // 랜덤 대기

    const seats = seatResponse.json().concertSeatInfo;
    if (!seats) {
        fail('No seats available for booking.');
        return;
    }

    if (seats.length === 0) {
        return; // 예약 가능한 좌석이 없으면 종료
    }


    const randomCount = Math.floor(Math.random() * 2) + 1;
    const shuffledSeats = seats.concertSeat.sort(() => 0.5 - Math.random());
    const selectedSeats = shuffledSeats.slice(0, Math.min(randomCount, shuffledSeats.length));
    const seatIds = selectedSeats.map(seat => seat.id);

    // Step 3: 좌석 선택 및 예약 요청
    const reserveResponse = http.post(
        `http://host.docker.internal:8080/api/v1/concert/reservation`,
        JSON.stringify({
            userId: userId,
            concertScheduleId: concertScheduleId,
            seatIds: seatIds,
            dateTime: new Date()
        }),
        {
            headers: {
                'Content-Type': 'application/json',
                'X-Waiting-Token': token
            },
        }
    );

    if (!check(reserveResponse, {'Reservation status is 201': (r) => r.status === 201})) {
        fail(`Reservation request failed with status: ${reserveResponse.status}`);
        return;
    }
    sleep(getRandomWaitTime());

    const reservation = reserveResponse.json();
    if (!reservation) {
        fail('Reservation response does not contain reservation details.');
        return;
    }

    const reservationId = reservation.id;

    // Step 4: 사용자 잔액 조회 및 충전 요청
    const UserWalletResponse = http.get(
        `http://host.docker.internal:8080/api/v1/users/${userId}/wallet`,
        {
            headers: {
                'Content-Type': 'application/json',
            },
        }
    );

    if (!check(UserWalletResponse, {'Wallet status is 200': (r) => r.status === 200})) {
        fail(`Wallet request failed with status: ${UserWalletResponse.status}`);
        return;
    }


    sleep(getRandomWaitTime());

    const chargeResponse = http.patch(
        `http://host.docker.internal:8080/api/v1/users/charge/point`,
        JSON.stringify({userId: userId, point: 100}),
        {
            headers: {
                'Content-Type': 'application/json',
                'X-Waiting-Token': token
            },
        }
    );

    if (!check(chargeResponse, {'Charge status is 200': (r) => r.status === 200})) {
        fail(`Charge request failed with status: ${chargeResponse.status}`);
        return;
    }
    sleep(getRandomWaitTime());

    // Step 5: 결제 완료 요청
    const paymentResponse = http.post(
        `http://host.docker.internal:8080/api/v1/payment`,
        JSON.stringify({userId: userId, reservationId: reservationId, token: token}),
        {
            headers: {
                'Content-Type': 'application/json',
                'X-Waiting-Token': token
            },
        }
    );

    if (!check(paymentResponse, {'Payment status is 200': (r) => r.status === 200})) {
        fail(`Payment request failed with status: ${paymentResponse.status}`);
        return;
    }
    sleep(getRandomWaitTime());
}
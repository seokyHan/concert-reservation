package com.server.concert_reservation.interfaces.batch;

import com.server.concert_reservation.infrastructure.db.concert.entity.types.ReservationStatus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DummyDataInsertBatch {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3307/concert?characterEncoding=UTF-8&serverTimezone=UTC";
        String user = "application1";
        String password = "application1";

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            // 1. 데이터베이스 연결
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false); // 자동 커밋 비활성화

            // 2. SQL 준비
            String sql = "INSERT INTO reservation (user_id, concert_schedule_id, seat_ids, status, total_price, reservation_at) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sql);

            int batchSize = 1000;  // 한 번에 처리할 배치 크기
            int totalData = 3000000;  // 삽입할 총 데이터 수
            Random random = new Random();

            for (int i = 1; i <= totalData; i++) {
                long userId = random.nextInt(100000) + 1; // 1부터 100000까지의 랜덤 user_id
                long concertScheduleId = random.nextInt(100) + 1; // 1부터 100까지의 랜덤 concert_schedule_id
                String seatIds = generateRandomSeatIds(random); // 랜덤 좌석 ID 생성
                ReservationStatus[] statuses = ReservationStatus.values();
                int randomIndex = ThreadLocalRandom.current().nextInt(statuses.length);
                pstmt.setString(4, statuses[randomIndex].name());
                int totalPrice = random.nextInt(100000) + 1; // 1부터 100000까지의 랜덤 총 가격
                long reservationStartAtMillis = System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(0, 604800000); // 현재 시간부터 1주일 이내의 랜덤한 예약 시작 시간
                java.sql.Timestamp reservationStartAt = new java.sql.Timestamp(reservationStartAtMillis);

                // 3. 데이터 설정
                pstmt.setLong(1, userId);
                pstmt.setLong(2, concertScheduleId);
                pstmt.setString(3, seatIds);
                pstmt.setString(4, statuses[randomIndex].name());
                pstmt.setInt(5, totalPrice);
                pstmt.setTimestamp(6, reservationStartAt);

                // 4. 배치에 추가
                pstmt.addBatch();

                // 5. 배치 실행 조건 체크
                if (i % batchSize == 0) {
                    pstmt.executeBatch();  // 배치 실행
                    connection.commit();  // 커밋
                    System.out.println(i + " records inserted.");
                }
            }

            // 6. 남은 배치 처리
            pstmt.executeBatch();
            connection.commit();

            System.out.println("All records inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();  // 오류 발생 시 롤백
                    System.out.println("Transaction rolled back.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            // 7. 자원 해제
            try {
                if (pstmt != null) pstmt.close();
                if (connection != null) connection.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    private static String generateRandomSeatIds(Random random) {
        int numberOfSeats = random.nextInt(5) + 1; // 1부터 5개까지의 랜덤 좌석 수
        StringBuilder seatIds = new StringBuilder();
        for (int j = 0; j < numberOfSeats; j++) {
            if (j > 0) {
                seatIds.append(","); // 좌석 ID를 쉼표로 구분
            }
            seatIds.append(random.nextInt(1000) + 1); // 1부터 1000까지의 랜덤 좌석 ID
        }
        return seatIds.toString();
    }

}

package com.server.concert_reservation.interfaces;


import java.sql.*;
import java.util.Random;

public class DataBatch {

    private static final String URL = "jdbc:mysql://localhost:3306/concert?characterEncoding=UTF-8&serverTimezone=UTC";
    private static final String USER = "application";
    private static final String PASSWORD = "application";
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            connection.setAutoCommit(false); // 트랜잭션 시작

            // 데이터 초기화
            truncateTables(connection);

            // 더미 데이터 삽입
            insertUsers(connection);
            insertUserPoints(connection);
            int concertId = insertConcert(connection);
            insertConcertSchedule(connection, concertId);
            insertConcertSeats(connection);

            connection.commit(); // 트랜잭션 커밋
            System.out.println("더미 데이터 삽입 완료");

        } catch (SQLException e) {
            System.out.println("에러 발생: " + e.getMessage());
        }
    }

    private static void truncateTables(Connection connection) throws SQLException {
        String[] tables = {"user", "wallet", "concert", "concert_seat", "concert_schedule", "reservation", "payment"};
        for (String table : tables) {
            try (PreparedStatement pstmt = connection.prepareStatement("TRUNCATE " + table)) {
                pstmt.executeUpdate();
            }
        }
    }

    private static void insertUsers(Connection connection) throws SQLException {
        System.out.println("insertUsers");
        String sql = "INSERT INTO user (created_at, updated_at, name) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < 1000; i++) {
                pstmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
                pstmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
                pstmt.setString(3, "User " + (i + 1));
                pstmt.executeUpdate();
            }
        }
    }

    private static void insertUserPoints(Connection connection) throws SQLException {
        System.out.println("insertUserPoints");
        String sql = "INSERT INTO wallet (created_at, updated_at, amount, user_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int userId = 1; userId <= 1000; userId++) {
                pstmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
                pstmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
                pstmt.setInt(3, RANDOM.nextInt(100000, 200000));
                pstmt.setInt(4, userId);
                pstmt.executeUpdate();
            }
        }
    }

    private static int insertConcert(Connection connection) throws SQLException {
        System.out.println("insertConcert");
        String sql = "INSERT INTO concert (created_at, updated_at, title, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setString(3, "Concert 1");
            pstmt.setString(4, "Concert 1 test");
            pstmt.executeUpdate();

            // 생성된 키 반환
            try (var generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return -1; // 실패 시 -1 반환
    }

    private static void insertConcertSchedule(Connection connection, int concertId) throws SQLException {
        System.out.println("insertConcertSchedule");
        String sql = "INSERT INTO concert_schedule (created_at, updated_at, reservation_start_at, concert_id, remain_ticket) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < 5; i++) {
                pstmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
                pstmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
                pstmt.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now().minusDays(RANDOM.nextInt(1, 8))));
                pstmt.setInt(4, concertId);
                pstmt.setInt(5, 1000);
                pstmt.executeUpdate();
            }
        }
    }

    private static void insertConcertSeats(Connection connection) throws SQLException {
        System.out.println("insertConcertSeats");
        String sql = "INSERT INTO concert_seat (created_at, updated_at, concert_schedule_id, price, number, status, version) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int concertScheduleId = 1; concertScheduleId <= 5; concertScheduleId++) { // 예시로 5개의 세션
                for (int seatNumber = 1; seatNumber <= 1000; seatNumber++) {
                    pstmt.setTimestamp(1, Timestamp.valueOf(java.time.LocalDateTime.now()));
                    pstmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
                    pstmt.setInt(3, concertScheduleId);
                    pstmt.setInt(4, RANDOM.nextInt(1000, 2000));
                    pstmt.setInt(5, seatNumber);
                    pstmt.setString(6, "AVAILABLE");
                    pstmt.setInt(7, 1);
                    pstmt.executeUpdate();
                }
            }
        }
    }
}


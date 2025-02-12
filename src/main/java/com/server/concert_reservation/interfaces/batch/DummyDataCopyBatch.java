package com.server.concert_reservation.interfaces.batch;

import java.sql.*;

public class DummyDataCopyBatch {
    public static void main(String[] args) {
        String sourceUrl = "jdbc:mysql://localhost:3307/concert?characterEncoding=UTF-8&serverTimezone=UTC";
        String destUrl = "jdbc:mysql://localhost:3308/concert2?characterEncoding=UTF-8&serverTimezone=UTC";
        String user1 = "application1";
        String password1 = "application1";
        String user2 = "application2";
        String password2 = "application2";

        Connection sourceConnection = null;
        Connection destConnection = null;
        PreparedStatement selectStmt = null;
        PreparedStatement insertStmt = null;

        try {
            // 1. 소스 데이터베이스 연결
            sourceConnection = DriverManager.getConnection(sourceUrl, user1, password1);
            // 2. 대상 데이터베이스 연결
            destConnection = DriverManager.getConnection(destUrl, user2, password2);

            // 3. 자동 커밋 비활성화 (트랜잭션 관리)
            destConnection.setAutoCommit(false);

            // 4. SQL 준비
            String selectSQL = "SELECT concert_schedule_id, number, price, status, version  FROM concert_seat";
            String insertSQL = "INSERT INTO concert_seat (concert_schedule_id, number, price, status, version) VALUES (?, ?, ?, ?, ?)";

            selectStmt = sourceConnection.prepareStatement(selectSQL);
            insertStmt = destConnection.prepareStatement(insertSQL);

            // 5. 데이터 선택
            ResultSet resultSet = selectStmt.executeQuery();
            int batchSize = 1000;  // 한 번에 처리할 배치 크기
            int count = 0;

            while (resultSet.next()) {
                // 6. 데이터 설정
                insertStmt.setLong(1, resultSet.getLong("concert_schedule_id"));
                insertStmt.setInt(2, resultSet.getInt("number"));
                insertStmt.setInt(3, resultSet.getInt("price"));
                insertStmt.setString(4, resultSet.getString("status"));
                insertStmt.setLong(5, resultSet.getLong("version"));
//                insertStmt.setLong(3, resultSet.getLong("price"));
//                insertStmt.setString(4, resultSet.getString("status"));
//                insertStmt.setLong(5, resultSet.getLong("version"));

                // 7. 배치에 추가
                insertStmt.addBatch();
                count++;

                // 8. 배치 실행 조건 체크
                if (count % batchSize == 0) {
                    insertStmt.executeBatch();  // 배치 실행
                    destConnection.commit();  // 커밋
                    System.out.println(count + " records copied.");
                }
            }

            // 9. 남은 배치 처리
            insertStmt.executeBatch();
            destConnection.commit();
            System.out.println("All records copied successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            if (destConnection != null) {
                try {
                    destConnection.rollback();  // 오류 발생 시 롤백
                    System.out.println("Transaction rolled back.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            // 10. 자원 해제
            try {
                if (selectStmt != null) selectStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (sourceConnection != null) sourceConnection.close();
                if (destConnection != null) destConnection.close();
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }
}

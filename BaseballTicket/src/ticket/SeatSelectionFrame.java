package ticket;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import login.ConnectDB;
import login.UserSession;
import booking.BookingInfo;

public class SeatSelectionFrame extends JFrame {
	private static final int ROWS = 20; // 좌석 줄 수
    private static final int COLS = 15; // 좌석 열 수
    private JButton[][] seatButtons = new JButton[ROWS][COLS]; // 좌석 버튼 배열
    private boolean[][] isSelected = new boolean[ROWS][COLS];  // 선택 여부 배열
    
    private int seatInfoId; // 좌석 정보 ID (좌석 유형 식별자)
    private BookingInfo bookingInfo; // 예매 정보 저장 객체

    public SeatSelectionFrame(BookingInfo bookingInfo, int seatInfoId) {
        this.bookingInfo = bookingInfo; // 예약 정보 저장
        this.seatInfoId = seatInfoId; // 좌석 정보 ID 저장
        
        setTitle("좌석 선택"+ bookingInfo.getSeatType()); // 창 제목 설정
        setSize(1200, 800); // 고정 크기
        setLocationRelativeTo(null); // 화면 중앙 정렬
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 닫으면 창만 종료
        getContentPane().setLayout(null); // 수동 배치

        JPanel seatPanel = new JPanel(new GridBagLayout()); // 좌석 배치 패널 (그리드 스타일)
        seatPanel.setBackground(Color.WHITE); // 배경색 흰색
        seatPanel.setPreferredSize(new Dimension(450, 600)); // 크기 설정
        GridBagConstraints gbc = new GridBagConstraints(); // 컴포넌트 제어 옵션
        gbc.insets = new Insets(5, 5, 5, 5); // 간격 설정

        // 8분 초과 TEMPORARY 좌석 AVAILABLE로 해제
        cleanupTemporarySeats();
        
        Map<Integer, String> seatStatusMap = loadSeatStatusFromDB(); // 현재 좌석 상태 불러오기

        for (int r = 0; r < ROWS; r++) { // 각 줄 반복
            for (int c = 0; c < COLS; c++) { // 각 열 반복
                int seatNumber = r * COLS + c + 1; // 좌석 번호 계산 (1부터 시작)
                JButton btn = new JButton(String.valueOf(""));  // 좌석 버튼 생성
                btn.setPreferredSize(new Dimension(20, 20)); // 크기 설정
                btn.setActionCommand(String.valueOf(seatNumber)); // 좌석 번호 저장
                String status = seatStatusMap.getOrDefault(seatNumber, "AVAILABLE"); // 상태 가져오기

                if ("BOOKED".equals(status)) {  // 예매 완료 좌석
                    btn.setEnabled(false);
                    btn.setBackground(Color.WHITE);
                    isSelected[r][c] = false;
                } else if ("TEMPORARY".equals(status)) {  // 임시 보류 좌석
                    btn.setEnabled(false);
                    btn.setBackground(Color.ORANGE);
                    isSelected[r][c] = false;
                } else { // 예매 가능 좌석
                    btn.setBackground(Color.GREEN);
                    isSelected[r][c] = false;
                    final int row = r, col = c; // 이벤트 핸들링을 위한 지역 변수
                    btn.addActionListener(e -> { // 클릭 시 이벤트 처리
                        if (!isSelected[row][col]) {
                            // 선택된 좌석 개수 체크
                            int selectedCount = 0; // 현재 선택된 좌석 수 계산
                            for (int i = 0; i < ROWS; i++) {
                                for (int j = 0; j < COLS; j++) {
                                    if (isSelected[i][j]) selectedCount++;
                                }
                            }
                            if (selectedCount >= 4) { // 최대 4개 제한
                                JOptionPane.showMessageDialog(this, "최대 4개의 좌석만 선택할 수 있습니다.");
                                return;
                            }
                            btn.setBackground(Color.YELLOW);  // 선택하면 노란색
                            isSelected[row][col] = true;
                        } else {
                            btn.setBackground(Color.GREEN);  // 다시 클릭하면 취소 (초록색)
                            isSelected[row][col] = false;
                        }
                    });
                }
                gbc.gridx = c; // 열 좌표
                gbc.gridy = r; // 행 좌표
                seatPanel.add(btn, gbc); // 버튼 추가
                seatButtons[r][c] = btn; // 배열에 저장
            }
        }

        JPanel outerPanel = new JPanel(new BorderLayout()); // 바깥 패널 (좌석 포함)
        outerPanel.setBackground(Color.WHITE);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 20));
        outerPanel.add(seatPanel, BorderLayout.CENTER); // 좌석 패널 추가
        outerPanel.setBounds(10, 10, 630, 630); // 위치 및 크기 설정
        getContentPane().add(outerPanel); // 창에 추가

        JButton confirmButton = new JButton("선택 완료");  // 완료 버튼
        confirmButton.setBounds(800, 30, 200, 50); // 위치와 크기 설정
        confirmButton.addActionListener(e -> {
            int userId = UserSession.getInstance().getUserId(); // 로그인 확인
            if (userId == 0) {
                JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
                return;
            }

            List<Integer> selectedSeatNumbers = new ArrayList<>(); // 선택된 좌석 목록
            
            StringBuilder selectedSeats = new StringBuilder();  // 표시용 텍스트
            boolean hasSelected = false; // 선택 여부 확인

            try (Connection conn = ConnectDB.getConnection()) { // DB 연결
                String findSeatIdSql = "SELECT id FROM seat_status WHERE seat_info_id = ? AND seat_number = ?";
                String tempSql = "UPDATE seat_status SET status = 'TEMPORARY', temp_reserved_at = NOW() WHERE id = ? AND status = 'AVAILABLE'";
                PreparedStatement findSeatIdPstmt = conn.prepareStatement(findSeatIdSql);
                PreparedStatement tempPstmt = conn.prepareStatement(tempSql);

                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        if (isSelected[r][c]) {
                            int seatNumber = Integer.parseInt(seatButtons[r][c].getActionCommand());

                            findSeatIdPstmt.setInt(1, seatInfoId); // 조건: 좌석 유형 ID
                            findSeatIdPstmt.setInt(2, seatNumber); // 조건: 좌석 번호
                            ResultSet seatIdRs = findSeatIdPstmt.executeQuery();

                            if (seatIdRs.next()) {
                                int seatStatusId = seatIdRs.getInt("id");  
                                
                                tempPstmt.setInt(1, seatStatusId); // TEMPORARY 로 업데이트
                                int tempCount = tempPstmt.executeUpdate();  // 업데이트 성공 시
                                if (tempCount > 0) {
                                    selectedSeats.append(seatNumber).append(", "); // 문자열에 추가
                                    hasSelected = true;
                                } 
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "예매 처리 중 오류가 발생했습니다.");
                return;
            }

            if (hasSelected) {
                String seatStr = selectedSeats.toString();
                if (seatStr.endsWith(", ")) { // 마지막 쉼표 제거
                    seatStr = seatStr.substring(0, seatStr.length() - 2);
                }
                bookingInfo.setSeatNumber(seatStr);
                JOptionPane.showMessageDialog(this, "결제 페이지로 넘어갑니다.\n선택된 좌석: " + seatStr); // 예매 정보에 좌석 번호 저장
                new PaymentFrame(bookingInfo, seatInfoId); // 결제 창 호출
                dispose(); // 창 닫기
            } else {
                JOptionPane.showMessageDialog(this, "좌석을 선택해주세요.");
            }
        });
        getContentPane().add(confirmButton); // 버튼 추가

        setVisible(true); // 창 표시
    }

    // 8분 초과 TEMPORARY 좌석을 AVAILABLE로 해제
    private void cleanupTemporarySeats() {
        try (Connection conn = ConnectDB.getConnection()) { // DB 연결
            String sql = "UPDATE seat_status SET status = 'AVAILABLE', temp_reserved_at = NULL " +
                         "WHERE status = 'TEMPORARY' AND temp_reserved_at < (NOW() - INTERVAL 8 MINUTE)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private Map<Integer, String> loadSeatStatusFromDB() {
        Map<Integer, String> seatStatusMap = new HashMap<>();
        String sql = "SELECT seat_number, status FROM seat_status WHERE seat_info_id = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seatInfoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                seatStatusMap.put(rs.getInt("seat_number"), rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seatStatusMap;
    }
}
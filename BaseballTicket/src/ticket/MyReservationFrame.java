package ticket;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

import login.ConnectDB;

//내 예매 내역 프레임 클래스 선언
public class MyReservationFrame extends JFrame {
    public MyReservationFrame(int userId) { // 생성자 (사용자 ID를 매개변수로 받음)
    	setTitle("내 예매 내역"); // 창 제목 설정
        setSize(1200, 800); // 창 크기 설정
        setLocationRelativeTo(null); // 화면 중앙에 위치
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫을 때 프레임만 종료

        // 테이블 컬럼명 배열 선언
        String[] columnNames = {"예매ID", "경기일", "시간", "팀", "상대", "경기장", "좌석유형", "좌석번호", "수령", "이름", "금액", "취소"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) { // 테이블 모델 생성 
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 11; // '취소' 버튼만 편집 가능
            }
        };
        JTable table = new JTable(model); // 테이블 생성

        // 폰트 및 스타일
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 18)); // 테이블 내용 폰트 설정
        table.setRowHeight(36); // 행 높이 설정
        JTableHeader header = table.getTableHeader(); // 테이블 헤더 가져오기
        header.setFont(new Font("맑은 고딕", Font.BOLD, 20)); // 헤더 폰트 설정

        // DB에서 예매 내역 불러오기
        try (Connection conn = ConnectDB.getConnection(); // DB 연결
             PreparedStatement pstmt = conn.prepareStatement(
                "SELECT id, game_date, game_time, team, opponent_team, stadium, seat_type, seat_number, receive_method, name, total_price " +
                "FROM user_reservations WHERE user_id = ?")) {
        	pstmt.setInt(1, userId); // 쿼리 파라미터 설정(사용자 ID)
            ResultSet rs = pstmt.executeQuery(); // 쿼리 실행 및 결과 받기
            while (rs.next()) { // 결과 행이 있는 동안 반복
                Object[] row = new Object[12]; // 12개 컬럼을 가진 행 배열 생성
                for (int i = 0; i < 11; i++) row[i] = rs.getObject(i+1); // DB 결과를 행 배열에 저장
                row[11] = "취소"; // 마지막 컬럼에 "취소" 텍스트 추가
                model.addRow(row); // 테이블 모델에 행 추가
            }
        } catch (Exception ex) { // 예외 발생 시
            ex.printStackTrace(); // 예외 내용 출력
        }

        // 취소 버튼 렌더러/에디터 적용
        table.getColumn("취소").setCellRenderer(new ButtonRenderer());
        table.getColumn("취소").setCellEditor(new ButtonEditor(new JCheckBox(), model, table));

        JScrollPane scrollPane = new JScrollPane(table); // 테이블을 스크롤 패널에 추가
        add(scrollPane, BorderLayout.CENTER); // 스크롤 패널을 프레임 중앙에 추가

        setVisible(true); // 창을 화면에 표시
    }
}

// 취소 버튼을 위한 ButtonRenderer
class ButtonRenderer extends JButton implements TableCellRenderer { // 버튼 렌더러 클래스 선언
    public ButtonRenderer() { // 생성자
        setOpaque(true); // 배경 불투명 설정
        setFont(new Font("맑은 고딕", Font.BOLD, 16)); // 폰트 설정
    }
 // 테이블 셀 렌더링 메서드 구현
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "취소" : value.toString()); // 버튼 텍스트 설정
        setBackground(new Color(255, 100, 100)); // 배경색 설정(빨간색)
        setForeground(Color.WHITE); // 글자색 설정(흰색)
        return this; // 버튼 반환
    }
}

// 취소 버튼을 위한 ButtonEditor (invokeLater 사용)
class ButtonEditor extends DefaultCellEditor {
	private JButton button; // 버튼 컴포넌트
    private String label; // 버튼 라벨
    private boolean isPushed; // 버튼 클릭 여부
    private DefaultTableModel model; // 테이블 모델
    private JTable table; // 테이블 참조

    public ButtonEditor(JCheckBox checkBox, DefaultTableModel model, JTable table) { // 생성자
    	super(checkBox); // 부모 생성자 호출
        this.model = model; // 테이블 모델 저장
        this.table = table; // 테이블 참조 저장
        button = new JButton(); // 버튼 생성
        button.setOpaque(true); // 배경 불투명 설정
        button.setFont(new Font("맑은 고딕", Font.BOLD, 16)); // 폰트 설정
        button.setBackground(new Color(255, 100, 100)); // 배경색 설정(빨간색)
        button.setForeground(Color.WHITE); // 글자색 설정(흰색)
        button.addActionListener(e -> fireEditingStopped()); // 버튼 클릭 시 편집 종료 이벤트 발생
    }

    @Override // 테이블 셀 에디터 컴포넌트 반환 메서드
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    	label = (value == null) ? "취소" : value.toString(); // 버튼 라벨 설정
        button.setText(label); // 버튼 텍스트 설정
        isPushed = true; // 버튼 클릭 상태 설정
        return button; // 버튼 반환
    }

    @Override // 셀 에디터 값 반환 메서드 (버튼 클릭 시 실행)
    public Object getCellEditorValue() {
        if (isPushed) {
        	int row = table.getSelectedRow(); // 선택된 행 인덱스 가져오기
            if (row < 0) return label; // 선택된 행이 없으면 라벨만 반환

            // 선택된 행에서 필요한 정보 가져오기
            int reservationId = (int) model.getValueAt(row, 0);
            String seatType = (String) model.getValueAt(row, 6);
            String seatNumber = (String) model.getValueAt(row, 7);
            String stadium = (String) model.getValueAt(row, 5);

            // 예매 취소 처리 메서드 호출
            cancelReservation(reservationId, seatType, seatNumber, stadium);
            JOptionPane.showMessageDialog(button, "예매가 취소되었습니다.");

            // 테이블에서 해당 행 제거 (EDT에서 안전하게 실행)
            SwingUtilities.invokeLater(() -> {
                if (row < model.getRowCount()) {
                    model.removeRow(row);
                }
            });
        }
        isPushed = false; // 버튼 클릭 상태 초기화
        return label; // 라벨 반환
    }

    // 예매 취소 처리 메서드
    private void cancelReservation(int reservationId, String seatType, String seatNumber, String stadium) {
        try (Connection conn = ConnectDB.getConnection()) { // DB 연결
            // 1. user_reservations 테이블에서 예매 내역 삭제
            String delSql = "DELETE FROM user_reservations WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(delSql)) {
                pstmt.setInt(1, reservationId); // 예매ID 설정
                pstmt.executeUpdate(); // 쿼리 실행
            }
            // 2. seat_info 테이블에서 좌석 정보 ID 조회
            int seatInfoId = -1;
            String seatInfoSql = "SELECT id FROM seat_info WHERE seat_type = ? AND stadium = ? LIMIT 1";
            try (PreparedStatement pstmt = conn.prepareStatement(seatInfoSql)) {
                pstmt.setString(1, seatType); // 좌석유형 설정
                pstmt.setString(2, stadium); // 경기장 설정
                ResultSet rs = pstmt.executeQuery(); // 쿼리 실행
                if (rs.next()) {
                    seatInfoId = rs.getInt("id"); // 좌석 정보 ID 가져오기
                }
            }
            // 3. seat_status 테이블에서 좌석 상태 'AVAILABLE'로 복구
            if (seatInfoId != -1) { // 좌석 정보 ID가 유효하면
                String updateSql = "UPDATE seat_status SET status = 'AVAILABLE', temp_reserved_at = NULL " +
                                   "WHERE seat_number = ? AND seat_info_id = ?";
                try (PreparedStatement pstmt2 = conn.prepareStatement(updateSql)) {
                    pstmt2.setInt(1, Integer.parseInt(seatNumber.trim())); // 좌석번호 설정
                    pstmt2.setInt(2, seatInfoId); // 좌석 정보 ID 설정
                    pstmt2.executeUpdate(); // 쿼리 실행
                }
            }
        } catch (Exception ex) { // 예외 발생 시
            ex.printStackTrace(); // 예외 내용 출력
        }
    }
}
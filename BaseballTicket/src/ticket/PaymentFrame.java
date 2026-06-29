package ticket;

import booking.BookingInfo;
import login.UserSession;
import login.ConnectDB;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PaymentFrame extends JFrame { // 결제 정보 확인 창 클래스 선언
    private BookingInfo bookingInfo; // 예매 정보 객체
    private int totalPrice; // 결제 금액

    public PaymentFrame(BookingInfo bookingInfo, int seatInfoId) { // 생성자
        this.bookingInfo = bookingInfo; // 예매 정보 저장

        setTitle("결제 정보 확인"); // 창 제목 설정
        setSize(1200, 800); // 창 크기 설정
        setLocationRelativeTo(null); // 화면 중앙에 위치
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫기 동작 설정
        setLayout(new GridBagLayout()); // GridBagLayout 사용

        Font font18 = new Font("한컴산뜻돋움", Font.PLAIN, 18); // 폰트 설정
        GridBagConstraints gbc = new GridBagConstraints(); // 레이아웃 제약 조건 객체
        gbc.insets = new Insets(8, 10, 8, 10); // 컴포넌트 간 여백
        gbc.fill = GridBagConstraints.BOTH; // 셀 전체 채우기

        // 왼쪽 패널 (수령방식, 예약자 정보)
        JPanel leftPanel = new JPanel(); // 왼쪽 패널 생성
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS)); // 세로 정렬

        JPanel receivePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 수령 방식 패널(왼쪽 정렬)
        receivePanel.setLayout(new BoxLayout(receivePanel, BoxLayout.Y_AXIS)); // 세로 정렬
        receivePanel.setBorder(BorderFactory.createTitledBorder(
        	    BorderFactory.createLineBorder(Color.GRAY), "수령 방식", 0, 0, font18)); // 테두리 및 제목

        ButtonGroup receiveGroup = new ButtonGroup(); // 라디오버튼 그룹
        JRadioButton mobileRadio = new JRadioButton("모바일 티켓"); // 모바일 티켓 라디오버튼
        JRadioButton onSiteRadio = new JRadioButton("현장 수령"); // 현장 수령 라디오버튼
        
        // 폰트 적용
        mobileRadio.setFont(font18);
        onSiteRadio.setFont(font18);
        
        receiveGroup.add(mobileRadio); // 그룹에 추가
        receiveGroup.add(onSiteRadio);
        receivePanel.add(mobileRadio); // 패널에 추가
        receivePanel.add(onSiteRadio);
        receivePanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 왼쪽 정렬

        // 예매자 정보 입력 패널
        JPanel infoPanel = new JPanel(new GridBagLayout()); // 그리드백 레이아웃 사용
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "예약자 정보", 0, 0, font18)); // 테두리 및 제목
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 왼쪽 정렬

        GridBagConstraints infoGbc = new GridBagConstraints(); // 예매자 정보용 제약조건
        infoGbc.insets = new Insets(5, 5, 5, 5); // 여백
        infoGbc.fill = GridBagConstraints.HORIZONTAL; // 수평 채우기
        infoGbc.weightx = 1.0; // 가로 가중치
        infoGbc.weighty = 0;
        infoGbc.anchor = GridBagConstraints.NORTHWEST; // 좌상단 정렬

        JLabel nameLabel = new JLabel("이름:"); // 이름 라벨
        JLabel phoneLabel = new JLabel("휴대전화:"); // 휴대전화 라벨
        JLabel emailLabel = new JLabel("이메일:"); // 이메일 라벨
        nameLabel.setFont(font18);
        phoneLabel.setFont(font18);
        emailLabel.setFont(font18);

        JTextField nameField = new JTextField(); // 이름 입력 필드
        JTextField phoneField = new JTextField(); // 전화번호 입력 필드
        JTextField emailField = new JTextField(); // 이메일 입력 필드
        nameField.setFont(font18);
        phoneField.setFont(font18);
        emailField.setFont(font18);

        Dimension fieldSize = new Dimension(200, 28); // 입력 필드 크기 지정
        nameField.setPreferredSize(fieldSize);
        phoneField.setPreferredSize(fieldSize);
        emailField.setPreferredSize(fieldSize);

        // 입력 라벨/필드 배치
        infoGbc.gridx = 0; infoGbc.gridy = 0; infoPanel.add(nameLabel, infoGbc);
        infoGbc.gridx = 1; infoPanel.add(nameField, infoGbc);
        infoGbc.gridx = 0; infoGbc.gridy = 1; infoPanel.add(phoneLabel, infoGbc);
        infoGbc.gridx = 1; infoPanel.add(phoneField, infoGbc);
        infoGbc.gridx = 0; infoGbc.gridy = 2; infoPanel.add(emailLabel, infoGbc);
        infoGbc.gridx = 1; infoPanel.add(emailField, infoGbc);
        
        // 패널 크기 제한
        infoPanel.setMaximumSize(new Dimension(400, 150));
        
        // 왼쪽 패널에 정보 패널, 수령 패널 추가
        leftPanel.add(infoPanel);
        leftPanel.add(Box.createVerticalStrut(10)); // 간격 추가
        leftPanel.add(receivePanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        add(leftPanel, gbc); // 왼쪽 패널을 프레임에 추가
        
        // 오른쪽 패널 (예매 정보 요약 + 결제 버튼)
        JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // 오른쪽 정렬 래퍼 패널
        rightWrapper.setPreferredSize(new Dimension(600, 800));  // 전체 창의 오른쪽 절반

        JPanel rightPanel = new JPanel(new BorderLayout()); // 오른쪽 내용 패널
        rightPanel.setPreferredSize(new Dimension(500, 400));  // 세로 길이 제한
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 테두리

        JPanel summaryPanel = new JPanel(); // 예매 정보 요약 패널
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS)); // 세로 정렬
        summaryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "예매 정보", 0, 0, font18)); // 테두리 및 제목

        JLabel[] infoLabels = { // 예매 정보 요약 라벨 배열
            //new JLabel("유저 ID: " + UserSession.getInstance().getUserId()),
            new JLabel("선택 팀: " + bookingInfo.getTeam()),
            new JLabel("상대 팀: " + bookingInfo.getOpponentTeam()),
            new JLabel("날짜: " + bookingInfo.getGameDate()),
            new JLabel("시간: " + bookingInfo.getGameTime()),
            new JLabel("경기장: " + bookingInfo.getStadium()),
            new JLabel("좌석 유형: " + bookingInfo.getSeatType()),
            new JLabel("좌석 번호: " + bookingInfo.getSeatNumber())
        };

        for (JLabel label : infoLabels) { // 각 요약 라벨 스타일 적용 및 패널 추가
            label.setFont(font18);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            summaryPanel.add(label);
            summaryPanel.add(Box.createVerticalStrut(5)); // 간격 추가
        }

        
     // 좌석 개수 계산
        String[] seatNumbers = bookingInfo.getSeatNumber().split(","); // 좌석 번호 문자열을 쉼표로 분리
        int seatCount = seatNumbers.length; // 좌석 개수 계산

        int pricePerSeat = 0; // 1석 가격 변수
        try {
            pricePerSeat = getPricePerSeat(bookingInfo.getSeatType(), bookingInfo.getGameDate()); // 좌석 유형과 날짜에 따라 가격 조회
        } catch (Exception e) {
            e.printStackTrace(); // 예외 출력
            JOptionPane.showMessageDialog(this, "좌석 가격 정보를 가져오는 중 오류가 발생했습니다."); // 오류 메시지
        }
        totalPrice = pricePerSeat * seatCount; // 총 결제 금액 계산

        JLabel priceLabel = new JLabel("1석 가격: " + pricePerSeat + "원"); // 1석 가격 라벨
        JLabel countLabel = new JLabel("예매 매수: " + seatCount + "석"); // 예매 매수 라벨
        JLabel totalLabel = new JLabel("최종 금액: " + totalPrice + "원"); // 최종 금액 라벨

        for (JLabel label : new JLabel[]{priceLabel, countLabel, totalLabel}) { // 금액 관련 라벨 스타일 지정
            label.setFont(font18);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            summaryPanel.add(label); // 요약 패널에 추가
            summaryPanel.add(Box.createVerticalStrut(5)); // 간격 추가
        }

        JButton payButton = new JButton("결제하기"); // 결제 버튼 생성
        payButton.setBackground(new Color(0, 123, 255)); // 배경색 지정
        payButton.setForeground(Color.WHITE); // 글자색 지정
        payButton.setPreferredSize(new Dimension(100, 50)); // 버튼 크기 지정

        // 결제 버튼 클릭 이벤트
        payButton.addActionListener(e -> {
            if (!mobileRadio.isSelected() && !onSiteRadio.isSelected()) { // 수령 방식 미선택 시
                JOptionPane.showMessageDialog(this, "수령 방식을 선택해주세요.");
                return;
            }
            // 예약자 정보 미입력 시
            if (nameField.getText().isBlank() || phoneField.getText().isBlank() || emailField.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "예약자 정보를 모두 입력해주세요.");
                return;
            }

            String receiveType = mobileRadio.isSelected() ? "모바일" : "현장 수령"; // 수령 방식 결정
            JOptionPane.showMessageDialog(this,
                    "결제가 완료되었습니다!\n\n"
                            + "수령 방식: " + receiveType + "\n"
                            + "예약자: " + nameField.getText()); // 결제 완료 안내

            try {
                // 1. 좌석 상태를 BOOKED로 변경
                updateSeatsToBooked(bookingInfo, seatInfoId);

                // 2. 예매 내역 저장
                saveReservationToDB(nameField.getText(), phoneField.getText(), emailField.getText(), receiveType);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "예약 저장 중 오류가 발생했습니다.");
            }

            this.dispose(); // 결제 창 닫기
        });

        rightPanel.add(summaryPanel, BorderLayout.CENTER); // 요약 패널을 오른쪽 패널 중앙에 추가
        rightPanel.add(payButton, BorderLayout.SOUTH); // 결제 버튼을 오른쪽 패널 하단에 추가

        // 감싸는 rightWrapper에 rightPanel 추가
        rightWrapper.add(rightPanel);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH; // 위쪽 정렬
        gbc.fill = GridBagConstraints.NONE; // 자동 확장 방지

        add(rightPanel, gbc); // 오른쪽 패널을 프레임에 추가

        setVisible(true); // 창 표시
    }

 // ----- 1석 가격 계산 메서드 -----
    private int getPricePerSeat(String seatType, String gameDate) throws Exception {
        int price = 0;
        String sql = "SELECT price, price_week FROM seat_info WHERE seat_type = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, seatType);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int weekdayPrice = rs.getInt("price"); // 평일 가격
                int weekendPrice = rs.getInt("price_week"); // 주말 가격

                LocalDate date = LocalDate.parse(gameDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (date.getDayOfWeek().getValue() >= 6) { // 토(6), 일(7) 주말
                    price = weekendPrice;
                } else {
                    price = weekdayPrice;
                }
            }
        }
        return price;
    }

 // ----- 결제 시 TEMPORARY → BOOKED로 변경 -----
    private void updateSeatsToBooked(BookingInfo bookingInfo, int seatInfoId) throws Exception {
        String[] seatNumbers = bookingInfo.getSeatNumber().split(",");
        try (Connection conn = ConnectDB.getConnection()) {
            String updateSql = "UPDATE seat_status SET status = 'BOOKED', temp_reserved_at = NULL " +
                               "WHERE seat_info_id = ? AND seat_number = ? AND status = 'TEMPORARY'";
            PreparedStatement updatePstmt = conn.prepareStatement(updateSql);

            for (String seatNum : seatNumbers) {
                int seatNumber = Integer.parseInt(seatNum.trim());
                updatePstmt.setInt(1, seatInfoId);
                updatePstmt.setInt(2, seatNumber);
                updatePstmt.executeUpdate();
            }
        }
    }
    
 // ----- 예매 내역 DB 저장 -----
    private void saveReservationToDB(String name, String phone, String email, String receiveMethod) throws Exception {
        String sql = "INSERT INTO user_reservations (user_id, game_date, game_time, team, opponent_team, stadium, seat_type, seat_number, receive_method, name, phone, email, total_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String[] seatNumbers = bookingInfo.getSeatNumber().split(",");
            int pricePerSeat = getPricePerSeat(bookingInfo.getSeatType(), bookingInfo.getGameDate());

            for (String seatNum : seatNumbers) {
                String seat = seatNum.trim();

                pstmt.setInt(1, UserSession.getInstance().getUserId());
                pstmt.setDate(2, Date.valueOf(bookingInfo.getGameDate()));
                pstmt.setTime(3, Time.valueOf(bookingInfo.getGameTime()));
                pstmt.setString(4, bookingInfo.getTeam());
                pstmt.setString(5, bookingInfo.getOpponentTeam());
                pstmt.setString(6, bookingInfo.getStadium());
                pstmt.setString(7, bookingInfo.getSeatType());
                pstmt.setString(8, seat);
                pstmt.setString(9, receiveMethod);
                pstmt.setString(10, name);
                pstmt.setString(11, phone);
                pstmt.setString(12, email);
                pstmt.setInt(13, pricePerSeat);

                pstmt.addBatch(); // 일괄 실행을 위해 addBatch 사용
            }
            pstmt.executeBatch(); // 일괄 실행
        }
    }
} 

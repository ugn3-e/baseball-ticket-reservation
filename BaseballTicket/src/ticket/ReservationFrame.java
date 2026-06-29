package ticket;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import login.ConnectDB;
import booking.BookingInfo;

//JFrame 상속
public class ReservationFrame extends JFrame {
	private BookingInfo bookingInfo;
	
	// 홈팀별 좌석 배치 이미지 경로 매핑
    private static final Map<String, String> homeTeamSeatMap = new HashMap<>();
    static {
        homeTeamSeatMap.put("두산", "images/doosanhome.png");
        homeTeamSeatMap.put("LG", "images/lghome.png");
        homeTeamSeatMap.put("롯데", "images/lottehome.png");
        homeTeamSeatMap.put("삼성", "images/samsunghome.png");
        homeTeamSeatMap.put("KIA", "images/kiahome.png");
        homeTeamSeatMap.put("키움", "images/kiwoomhome.png");
        homeTeamSeatMap.put("SSG", "images/ssghome.png");
        homeTeamSeatMap.put("KT", "images/kthome.png");
        homeTeamSeatMap.put("한화", "images/hanhwahome.png");
        homeTeamSeatMap.put("NC", "images/nchome.png");
    }

    // 이미지 크기 조정 메서드
    public ImageIcon imageSetSize(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();  // 원본 이미지
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg); // 새 아이콘 반환
    }
    
    public ReservationFrame(BookingInfo bookingInfo) {
    	this.bookingInfo = bookingInfo; // 전달받은 예매 정보 저장
    	String HomeTeam = bookingInfo.getHomeTeam(); // 홈팀 명
        String stadium = bookingInfo.getStadium();   // 경기장명
        
        setTitle("좌석 예매 - " + HomeTeam + " 홈경기"); // 프레임 제목 설정
        setSize(1200, 800);                            // 창 크기 설정
        setLocationRelativeTo(null);                   // 화면 중앙 배치
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 닫기 동작 설정

        JPanel backgroundPanel = new JPanel(new BorderLayout()); // 전체 배경 패널
        backgroundPanel.setOpaque(true);

        JPanel mainPanel = new JPanel(); // 중앙 패널
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setOpaque(false);

        // 좌석도 이미지 영역
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        imagePanel.setOpaque(true);
        imagePanel.setAlignmentY(Component.TOP_ALIGNMENT);

        String seatMapPath = homeTeamSeatMap.getOrDefault(HomeTeam, "images/default.png"); // 홈팀에 맞는 이미지 경로
        ImageIcon seatMapIcon = new ImageIcon(seatMapPath);
        if (!new File(seatMapPath).exists()) {
            seatMapIcon = new ImageIcon("images/default.png"); // 없을 경우 기본 이미지
        }
        seatMapIcon = imageSetSize(seatMapIcon, 600, 600); // 크기 조절
        JLabel seatMapLabel = new JLabel(seatMapIcon);     // 라벨에 이미지 표시
        seatMapLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imagePanel.add(seatMapLabel); // 패널에 추가

        mainPanel.add(imagePanel); // 메인 패널에 추가
        mainPanel.add(Box.createHorizontalStrut(30)); // 간격 추가

        // 2. 좌석 유형 라디오 버튼
        JPanel radioPanel = new JPanel(); // 좌석 유형 선택 영역 생성
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS)); // 세로 정렬
        radioPanel.setBorder(BorderFactory.createTitledBorder("좌석 유형 선택")); // 테두리 제목
        radioPanel.setOpaque(true);  // 배경 투명하지 않게
        radioPanel.setAlignmentY(Component.TOP_ALIGNMENT);  // 상단 정렬

        ButtonGroup seatTypeGroup = new ButtonGroup(); // 좌석 유형 라디오 버튼 그룹
        Map<Integer, String> seatInfoIdNameMap = new HashMap<>(); // seat_info_id ↔ seat_type 맵

        try (Connection conn = ConnectDB.getConnection()) { // DB 연결
        	String sql = "SELECT id, seat_type FROM seat_info WHERE stadium = ?"; // 경기장 기준 조회
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stadium); // 쿼리 조건: 경기장 이름
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
            	int id = rs.getInt("id"); // seat_info_id
                String seatType = rs.getString("seat_type"); // 좌석 유형
                seatInfoIdNameMap.put(id, seatType); // 맵 저장

                JRadioButton rb = new JRadioButton(seatType); // 버튼 생성
                rb.setActionCommand(String.valueOf(id)); // 선택 시 seat_info_id 반환
                rb.setForeground(Color.BLACK); // 텍스트 색상
                seatTypeGroup.add(rb); // 그룹에 추가
                radioPanel.add(rb);    // 패널에 추가
            }
        } catch (Exception e) {
            e.printStackTrace(); // 오류 출력
        }
        mainPanel.add(radioPanel); // 메인 패널에 추가
        mainPanel.add(Box.createHorizontalStrut(30)); // 간격 추가

        // 3. 예매 방식 패널
        JPanel methodPanel = new JPanel();
        methodPanel.setLayout(new BoxLayout(methodPanel, BoxLayout.Y_AXIS));
        methodPanel.setBorder(BorderFactory.createTitledBorder("예매 방법 선택"));
        methodPanel.setOpaque(false);
        methodPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        ButtonGroup methodGroup = new ButtonGroup(); // 예매 방식 그룹
        JRadioButton manualRadio = new JRadioButton("직접 좌석 선택");
        JRadioButton autoRadio = new JRadioButton("자동 좌석 배정");
        methodGroup.add(manualRadio);
        methodGroup.add(autoRadio);
        methodPanel.add(manualRadio);
        methodPanel.add(autoRadio);

        // 예매 버튼
        JButton bookButton = new JButton("예매하기");
        bookButton.setFont(new Font("한컴산뜻돋움", Font.BOLD, 20));
        bookButton.setBackground(new Color(220, 53, 69));
        bookButton.setForeground(Color.WHITE);
        methodPanel.add(Box.createVerticalStrut(20));
        methodPanel.add(bookButton);

        mainPanel.add(methodPanel); // 메인 패널에 추가

        backgroundPanel.add(mainPanel, BorderLayout.NORTH); // 메인 패널을 배경 패널의 위쪽
        setContentPane(backgroundPanel); // 배경 패널을 프레임의 컨텐트로 설정

        setVisible(true);  // 창을 화면에 표시

        // 예매 버튼 클릭 이벤트 리스너
        bookButton.addActionListener(e -> {
            if (seatTypeGroup.getSelection() == null) { // 좌석 유형이 선택되지 않은 경우
                JOptionPane.showMessageDialog(this, "좌석 유형을 선택해주세요.");
                return;
            }
            if (!manualRadio.isSelected() && !autoRadio.isSelected()) { // 예매 방법이 선택되지 않은 경우
                JOptionPane.showMessageDialog(this, "예매 방법을 선택해주세요.");
                return;
            }
            int seatInfoId = Integer.parseInt(seatTypeGroup.getSelection().getActionCommand()); // 선택된 좌석 유형 id

            String seatType = seatInfoIdNameMap.get(seatInfoId);  // 좌석 유형명(예: "응원석")
            bookingInfo.setSeatType(seatType);                    // 예매 정보에 좌석 유형 저장
            bookingInfo.setSeatNumber(null);                      // 좌석 번호 초기화
            
            if (manualRadio.isSelected()) {
                // 직접 좌석 선택 페이지로 이동
            	new SeatSelectionFrame(bookingInfo, seatInfoId);  // 좌석 선택 프레임 생성
                this.dispose(); // 현재 창 닫기
            } else if (autoRadio.isSelected()) { 
                JComboBox<Integer> countCombo = new JComboBox<>(new Integer[]{1,2,3,4}); // 자동 배정일 때 매수 선택 콤보박스 생성
                int result = JOptionPane.showConfirmDialog(
                    this,
                    countCombo,
                    "자동 배정할 매수를 선택하세요 (최대 4매)",
                    JOptionPane.OK_CANCEL_OPTION
                );
                if (result != JOptionPane.OK_OPTION) return; // 취소 시 종료
                int ticketCount = (Integer) countCombo.getSelectedItem();  // 선택한 매수

                try (Connection conn = ConnectDB.getConnection()) { // DB 연결
                    // 가능한 모든 좌석 seat_number, id를 불러온다
                    String sql = "SELECT id, seat_number FROM seat_status WHERE seat_info_id = ? AND status = 'AVAILABLE' ORDER BY seat_number";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, seatInfoId);
                    ResultSet rs = pstmt.executeQuery();

                    // 행별로 좌석을 저장할 맵 생성 (row 번호 → 좌석 리스트)
                    Map<Integer, List<int[]>> rowSeatsMap = new HashMap<>();
                    while (rs.next()) {
                        int seatNum = rs.getInt("seat_number");
                        int seatStatusId = rs.getInt("id");
                        int row = (seatNum - 1) / 20; // 0~14
                        rowSeatsMap.computeIfAbsent(row, k -> new ArrayList<>()).add(new int[]{seatNum, seatStatusId});
                    }

                    boolean found = false; // 연속 좌석 찾음 여부
                    int[] foundSeatNums = null; // 찾은 좌석 번호 배열
                    int[] foundSeatStatusIds = null; // 찾은 seat_status id 배열

                    // 각 행에서 연속된 좌석이 있는지 검사
                    for (List<int[]> seats : rowSeatsMap.values()) {
                        seats.sort(Comparator.comparingInt(a -> a[0])); // 좌석 번호순 
                        for (int i = 0; i <= seats.size() - ticketCount; i++) {
                            boolean consecutive = true;
                            int first = seats.get(i)[0];
                            for (int j = 1; j < ticketCount; j++) {
                                if (seats.get(i + j)[0] != first + j) { // 연속되지 않으면
                                    consecutive = false;
                                    break;
                                }
                            }
                            if (consecutive) {  // 연속 좌석 발견 시 정보 저장
                                found = true;
                                foundSeatNums = new int[ticketCount];
                                foundSeatStatusIds = new int[ticketCount];
                                for (int j = 0; j < ticketCount; j++) {
                                    foundSeatNums[j] = seats.get(i + j)[0];
                                    foundSeatStatusIds[j] = seats.get(i + j)[1];
                                }
                                break;
                            }
                        }
                        if (found) break; // 찾으면 반복 종료
                    }

                    // 연속 좌석을 찾지 못한 경우
                    if (!found) {
                        JOptionPane.showMessageDialog(this, "같은 행에 연속된 좌석이 " + ticketCount + "개 없습니다.\n예매할 수 없습니다.");
                        return;
                    }

                    int userId = login.UserSession.getInstance().getUserId(); // 로그인한 사용자 id
                    if (userId == 0) { // 로그인 안 했으면
                        JOptionPane.showMessageDialog(this, "로그인이 필요합니다.");
                        return;
                    }// 예약 및 좌석 상태 업데이트 쿼리 준비
                    String reserveSql = "INSERT INTO reservation (user_id, seat_status_id, status) VALUES (?, ?, 'RESERVED')";
                    String updateSql = "UPDATE seat_status SET status = 'BOOKED' WHERE id = ?";
                    PreparedStatement reservePstmt = conn.prepareStatement(reserveSql);
                    PreparedStatement updatePstmt = conn.prepareStatement(updateSql);

                    // 선택된 좌석 수만큼 예약 및 상태 변경 반복
                    for (int i = 0; i < ticketCount; i++) {
                        reservePstmt.setInt(1, userId);
                        reservePstmt.setInt(2, foundSeatStatusIds[i]);
                        reservePstmt.executeUpdate();

                        updatePstmt.setInt(1, foundSeatStatusIds[i]);
                        updatePstmt.executeUpdate();
                    }

                    // 예매 성공 메시지 및 좌석 번호 안내
                    JOptionPane.showMessageDialog(this, "자동으로 좌석이 배정되었습니다!\n좌석 번호: " +
                        Arrays.toString(foundSeatNums));
                    this.dispose(); // 창 닫기
                    // 다음 페이지로 이동 (필요시 코드 추가)
                } catch (Exception ex) { // 예외 발생 시
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "자동 배정 중 오류가 발생했습니다.");
                }
            }
        });
    }
}

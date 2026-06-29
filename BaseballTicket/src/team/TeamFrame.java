package team;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;

import login.ConnectDB;
import ticket.CodeGenerator;
import ticket.ReservationFrame;
import booking.BookingInfo;

// TeamFrame 클래스는 JFrame을 상속받음
public class TeamFrame extends JFrame {
    private JPanel mainPanel;
    private Image backgroundImage, contentImage, logobackImage, teamLogoImage, teamBackImage;
    private BookingInfo bookingInfo;

    public TeamFrame(String teamName, String teamLogoPath, String teamBackPath, BookingInfo bookingInfo) {
    	this.bookingInfo = bookingInfo;
        this.teamLogoImage = new ImageIcon(teamLogoPath).getImage();
        this.teamBackImage = new ImageIcon(teamBackPath).getImage(); // 여기서 팀 배경 설정!
        
        setTitle(teamName + " 팀 경기 일정"); // 창 제목 설정
        setExtendedState(JFrame.MAXIMIZED_BOTH); // 전체화면 시작
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 닫으면 창만 종료
        
        backgroundImage = new ImageIcon("images\\background.png").getImage(); // 배경 이미지 로딩
        contentImage = new ImageIcon("images\\panel.png").getImage(); // 콘텐츠 배경 이미지 로딩
        //logoImage = new ImageIcon("images\\logo.png").getImage();
        logobackImage = new ImageIcon("images\\logoback2.png").getImage(); // 로고 배경 이미지 로딩
        //nameImage = new ImageIcon("images\\nameimage.png").getImage();
        
        JPanel backgroundPanel = new JPanel() { // 배경 패널 생성
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2.drawImage(contentImage, (getWidth() - 1600) / 2, 50, 1600, getHeight() - 50, this);
                g2.drawImage(teamBackImage, (getWidth() - 200)/2, 50, 200, 200, this);
            }
        };

        backgroundPanel.setLayout(null); // 레이아웃 직접 배치
        setContentPane(backgroundPanel); // 프레임에 배경 패널 설정
        
        JPanel scrollContainer = new JPanel(new BorderLayout()); // 스크롤을 담을 컨테이너
        scrollContainer.setBounds(150, 250, 1600, 700);  // 위치 및 크기 조정
        scrollContainer.setOpaque(false); // 투명하게 설정

        //경기 일정 패널 (스크롤 대상)
        mainPanel = new JPanel(null); // 경기 정보가 담길 메인 패널 (직접 위치 설정)
        mainPanel.setOpaque(false);  // 투명 설정

        JScrollPane scrollPane = new JScrollPane(mainPanel); // 메인 패널에 스크롤 기능 추가
        scrollPane.setOpaque(false); // 투명하게 설정
        scrollPane.getViewport().setOpaque(false); // 내부 뷰포트도 투명
        scrollPane.setBorder(null); // 테두리 제거
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도 설정

        // 빈 패널 안에 scrollPane 추가 > backgroundPanel에 배치
        scrollContainer.add(scrollPane, BorderLayout.CENTER); // 스크롤을 중앙에 배치
        backgroundPanel.add(scrollContainer); // 배경에 추가

        loadGameData(teamName);  // 경기 일정 DB에서 불러오기

        setVisible(true);  // 창 보이게 하기
    }
      
    // DB에서 팀 경기 정보 조회 쿼리
    private void loadGameData(String teamName) {
        String query = "SELECT game_date, game_time, stadium, sale_info, logo_path, opponent, opponent_logo_path, home_team " +
                       "FROM team_info WHERE team_name = ? ORDER BY game_date";

        try (Connection conn = ConnectDB.getConnection(); // DB 연결
             PreparedStatement pstmt = conn.prepareStatement(query)) { // 쿼리 준비

        	pstmt.setString(1, teamName); // 쿼리에 팀 이름 바인딩
            ResultSet rs = pstmt.executeQuery(); // 쿼리 실행

            int yPosition = 0; // 시작 위치
            int gap = 150; // 패널 간격
            int panelWidth = 1500; // 패널 너비
            int panelHeight = 150; // 패널 높이
            
            while (rs.next()) {// 결과가 있을 때까지 반복
                String gameDate = rs.getString("game_date"); // 경기 날짜
                String gameTime = rs.getString("game_time"); // 경기 시간
                String stadium = rs.getString("stadium"); // 경기장
                String saleInfo = rs.getString("sale_info"); // 판매 정보
                String logoPath = rs.getString("logo_path"); // 홈팀 로고
                String opponent = rs.getString("opponent");  // 상대팀 이름
                String opponentLogoPath = rs.getString("opponent_logo_path"); // 상대 팀 로고 경로
                String HomeTeam = rs.getString("home_team"); // 홈팀

                // 경기 패널 생성
                JPanel gamePanel = createGamePanel(teamName, opponent, logoPath, opponentLogoPath, gameDate, gameTime, stadium, saleInfo, HomeTeam);
                gamePanel.setBounds(50, yPosition, panelWidth, panelHeight); // 원하는 위치에 배치
                mainPanel.add(gamePanel); // 메인 패널에 추가

                yPosition += panelHeight + 30; // 다음 패널 위치
            }
            mainPanel.setPreferredSize(new Dimension(1580, yPosition + 50)); // 전체 높이 설정
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB 오류: " + e.getMessage()); // 에러 메시지 출력
            e.printStackTrace(); // 콘솔에 에러 출력
        }
    }

    private JPanel createGamePanel(String Team, String opponentTeam, String homeLogoPath, String awayLogoPath,
            String date, String time, String stadium, String saleInfo, String HomeTeam) {

    	JPanel gamePanel = new JPanel(); // 경기 정보 한 줄을 담는 패널
    	gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.X_AXIS)); // 가로 정렬
    	gamePanel.setOpaque(false); // 배경 투명
    	gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패딩 설정

    	// 날짜 표시
    	JLabel dateLabel = new JLabel(date);
    	dateLabel.setPreferredSize(new Dimension(120, 50));
    	dateLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
    	gamePanel.add(dateLabel);

    	// 시간 표시
    	JLabel timeLabel = new JLabel(time);
    	timeLabel.setPreferredSize(new Dimension(80, 50));
    	timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
    	gamePanel.add(Box.createHorizontalStrut(75));
    	gamePanel.add(timeLabel);

    	// 홈팀 로고 이미지
    	JLabel homeLogo = new JLabel(loadImage(homeLogoPath, 100, 100));
    	gamePanel.add(Box.createHorizontalStrut(75));
    	gamePanel.add(homeLogo);

    	// "VS" 표시
    	JLabel vsLabel = new JLabel(" VS ");
    	vsLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
    	gamePanel.add(Box.createHorizontalStrut(25));
    	gamePanel.add(vsLabel);

    	// 원정팀 로고 이미지
    	JLabel awayLogo = new JLabel(loadImage(awayLogoPath, 100, 100));
    	gamePanel.add(Box.createHorizontalStrut(25));
    	gamePanel.add(awayLogo);

    	// 경기장 이름 표시
    	JLabel stadiumLabel = new JLabel(stadium);
    	stadiumLabel.setPreferredSize(new Dimension(200, 50));
    	stadiumLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
    	gamePanel.add(Box.createHorizontalStrut(75));
    	gamePanel.add(stadiumLabel);

    	// "예매하기" 버튼 생성
    	JButton ticketButton = new JButton("예매하기");
    	ticketButton.setBackground(new Color(220, 53, 69)); // 빨간색 배경
    	ticketButton.setForeground(Color.WHITE); // 흰색 글자
    	ticketButton.setFocusPainted(false); // 포커스 테두리 제거
    	ticketButton.setPreferredSize(new Dimension(200, 75));
    	gamePanel.add(Box.createHorizontalStrut(75));
    	gamePanel.add(ticketButton);

    	// 버튼 클릭 시 코드 입력 팝업 실행
    	ticketButton.addActionListener(e -> {
    	    // 예매 정보 저장
    	    bookingInfo.setGameDate(date);
    	    bookingInfo.setGameTime(time);
    	    bookingInfo.setStadium(stadium);
    	    bookingInfo.setOpponentTeam(opponentTeam);
    	    bookingInfo.setHomeTeam(HomeTeam);

    	    // 6자리 랜덤 코드 생성
    	    String randomCode = CodeGenerator.generateRandomCode(6);

    	    // 코드 입력 창 생성
    	    JFrame codeFrame = new JFrame("코드 입력");
    	    codeFrame.setSize(400, 200);
    	    codeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	    codeFrame.setLayout(new BorderLayout());

    	    // 랜덤 코드 표시
    	    JLabel codeLabel = new JLabel("코드: " + randomCode, JLabel.CENTER);
    	    codeLabel.setFont(new Font("Serif", Font.BOLD, 24));
    	    codeFrame.add(codeLabel, BorderLayout.NORTH);

    	    // 입력 필드 + 확인 버튼
    	    JPanel inputPanel = new JPanel(new FlowLayout());
    	    JTextField codeInput = new JTextField(10);
    	    JButton submitButton = new JButton("확인");
    	    inputPanel.add(new JLabel("코드 입력:"));
    	    inputPanel.add(codeInput);
    	    inputPanel.add(submitButton);
    	    codeFrame.add(inputPanel, BorderLayout.CENTER);

    	    // 코드 일치 확인
    	    submitButton.addActionListener(ev -> {
    	        if (codeInput.getText().equals(randomCode)) {
    	            codeFrame.dispose();
    	            JOptionPane.showMessageDialog(TeamFrame.this, "예매 페이지로 이동합니다.");
    	            new ReservationFrame(bookingInfo); // 예매 창 열기
    	        } else {
    	            JOptionPane.showMessageDialog(codeFrame, "코드가 일치하지 않습니다!");
    	        }
    	    });

    	    codeFrame.setLocationRelativeTo(this); // 화면 중앙에 위치
    	    codeFrame.setVisible(true); // 창 열기
    	});

    	return gamePanel; // 완성된 경기 패널 반환

    }

    // 주어진 경로의 이미지를 지정된 크기로 불러오는 메서드
    private Icon loadImage(String path, int width, int height) {
        File file = new File(path); // 파일 객체 생성
        if (!file.exists()) { // 파일이 없으면
            System.out.println("이미지 파일 없음: " + path); // 경고 출력
            return new ImageIcon(); // 빈 이미지 반환
        }
        // 이미지 로드 후 지정된 크기로 리사이즈해서 아이콘으로 반환
        return new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
}
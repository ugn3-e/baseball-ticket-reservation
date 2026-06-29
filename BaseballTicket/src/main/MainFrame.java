package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import booking.BookingInfo;
import team.TeamFrame;
import ticket.MyReservationFrame; // 꼭 import 해주세요!
import login.UserSession; // 로그인 세션에서 userId를 얻는 경우

//JFrame을 상속한 메인 화면 클래스 선언
public class MainFrame extends JFrame { 
	private Image backgroundImage;
	private Image contentImage;
	private Image logobackImage; 
	
	// 각 구단별 이미지 변수 선언
	private Image doosan;
	private Image kt;
	private Image samsung;
	private Image hanhwa;
	private Image lg;
	private Image lotte;
	private Image kiwoom;
	private Image ssg;
	private Image kia;
	private Image nc;
	
	// 생성자
    public MainFrame() {
        setTitle("메인 화면");  // 창 제목 설정
        setExtendedState(JFrame.MAXIMIZED_BOTH); // 전체화면 시작
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 닫기 버튼 클릭 시 프로그램 종료
        
        // 이미지 파일 로드
        backgroundImage = new ImageIcon("images\\background.png").getImage();
        contentImage = new ImageIcon("images\\panel.png").getImage();
        //logoImage = new ImageIcon("images\\logo.png").getImage();
        logobackImage = new ImageIcon("images\\logoback2.png").getImage();
        //nameImage = new ImageIcon("images\\nameimage.png").getImage();
        
        samsung = new ImageIcon("images\\samsung.png").getImage();
        doosan = new ImageIcon("images\\doosan.png").getImage();
        kt = new ImageIcon("images\\kt.png").getImage();
        hanhwa = new ImageIcon("images\\hanhwa.png").getImage();
        lg = new ImageIcon("images\\lg.png").getImage();
        lotte = new ImageIcon("images\\lotte.png").getImage();
        kiwoom = new ImageIcon("images\\kiwoom.png").getImage();
        ssg = new ImageIcon("images\\ssg.png").getImage();
        kia = new ImageIcon("images\\kia.png").getImage();
        nc = new ImageIcon("images\\nc.png").getImage();

        JPanel backgroundPanel = new JPanel() {  // 배경 이미지를 그릴 패널 생성
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);// 안티앨리어싱 적용
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // 배경 이미지 그리기
                g2.drawImage(contentImage, (getWidth() - 1600) / 2, 50, 1600, getHeight() - 50, this); // 패널 이미지 그리기
                //g2.drawImage(logoImage,400, 400, 350, 350, this);
                g2.drawImage(logobackImage,(getWidth() - 200)/2, 50, 200, 200, this); // 로고 배경 이미지 그리기
                //g2.drawImage(nameImage,300, 400, 500, 220, this);
            }
        }; 
        backgroundPanel.setLayout(null); // 배경 패널 레이아웃 직접 지정
        //setContentPane(backgroundPanel);
        //backgroundPanel.setLayout(new BorderLayout()); // 
        
        // panel.png 위치 기준 contentPanel 생성
        int contentWidth = 1600;
        int contentHeight = 1030; 
        int contentX = (Toolkit.getDefaultToolkit().getScreenSize().width - contentWidth) / 2; 
        int contentY = 50;

        JPanel contentPanel = new JPanel(null);
        contentPanel.setOpaque(false);  // 배경 투명
        //contentPanel.setLayout(null);
        //contentPanel.setLayout(new BorderLayout());
        contentPanel.setBounds(contentX, contentY, contentWidth, contentHeight); // 위치, 크기 설정
        backgroundPanel.add(contentPanel);  // 배경 패널에 내용 패널 추가

        // 예매 내역/ 취소 버튼 생성
        JButton settingsButton = new JButton("예매 내역/ 취소");
        settingsButton.setBounds(contentWidth - 300, 150, 120, 30); // 위치, 크기 
        contentPanel.add(settingsButton); // contentPanel 패널에 추가
        
        // 예매 내역/ 취소 버튼 클릭 이벤트
        settingsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "예매 내역/ 취소 버튼 클릭됨"); 
            
            int userId = UserSession.getInstance().getUserId(); // 로그인 세션에서 userId 얻기
            if (userId == 0) { // 로그인 안 되어 있으면
                JOptionPane.showMessageDialog(this, "로그인 후 이용 가능합니다.");
                return;
            }
            new ticket.MyReservationFrame(userId); // 예매내역 창 띄우기
        });

        JLabel subtitleLabel = new JLabel("구단을 선택하세요");  // 구단 선택 안내 라벨
        subtitleLabel.setFont(new Font("한컴산뜻돋움", Font.BOLD, 19)); 
        subtitleLabel.setBounds(730, 200, 200, 30); // 위치, 크기 설정
        contentPanel.add(subtitleLabel); // contentPanel 패널에 추가

        JPanel buttonPanel = new JPanel(null);
        buttonPanel.setOpaque(false); // 투명하게
        buttonPanel.setBounds(250, 300, 1200, 700);  // 위치, 크기 설정
        contentPanel.add(buttonPanel); // contentPanel 패널에 추가
        
        // 구단명 배열
        String[] teams = {
            "두산", "삼성", "롯데", "LG", "NC",
            "SSG", "KT", "키움", "KIA", "한화"
        };
        
        // 구단명 → 영어 파일명 변환 맵
        HashMap<String, String> teamNameMap = new HashMap<>();
        teamNameMap.put("두산", "doosan");
        teamNameMap.put("삼성", "samsung");
        teamNameMap.put("롯데", "lotte");
        teamNameMap.put("LG", "lg");
        teamNameMap.put("NC", "nc");
        teamNameMap.put("SSG", "ssg");
        teamNameMap.put("KT", "kt");
        teamNameMap.put("키움", "kiwoom");
        teamNameMap.put("KIA", "kia");
        teamNameMap.put("한화", "hanhwa");
        
        // 구단별 이미지 맵
        HashMap<String, Image> teamImageMap = new HashMap<>();
        teamImageMap.put("두산", doosan);
        teamImageMap.put("삼성", samsung);
        teamImageMap.put("롯데", lotte);
        teamImageMap.put("LG", lg);
        teamImageMap.put("NC", nc);
        teamImageMap.put("SSG", ssg);
        teamImageMap.put("KT", kt);
        teamImageMap.put("키움", kiwoom);
        teamImageMap.put("KIA", kia);
        teamImageMap.put("한화", hanhwa);

        int btnWidth = 200, btnHeight = 50, gapX = 30, gapY = 200; // 버튼 크기, 간격
        int startX = 0, startY = 0; // 버튼 시작 위치

        //정보 저장
        BookingInfo bookingInfo = new BookingInfo(); // 예매 정보 객체 생성
        
        // 구단 버튼 및 로고 반복 생성
        for (int i = 0; i < teams.length; i++) {
            String team = teams[i];
            JButton teamBtn = new JButton(team+" 예매하기"); // 구단 예매 버튼 생성
            
            int x = startX + (i % 5) * (btnWidth + gapX); // x좌표 계산
            int y = startY + (i / 5) * (btnHeight + gapY); // y좌표 계산
            
            Image rawImage = teamImageMap.get(team); // 구단 이미지 가져오기
            Image scaledImage = rawImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // 이미지 크기 조정
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage)); 
            logoLabel.setBounds(x + (btnWidth - 200) / 2, y, 200, 200);  // 이미지 위치 설정
            
            buttonPanel.add(logoLabel); // 버튼 패널에 로고 추가
            
            teamBtn.setBounds(x, y + 200, btnWidth, btnHeight); // 버튼 위치, 크기 설정
            buttonPanel.add(teamBtn); // 버튼 패널에 버튼 추가

            // 구단 버튼 클릭 이벤트 리스너
            teamBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String englishTeamName = teamNameMap.getOrDefault(team, team); // 영문 팀명 변환
                    String teamLogoPath = "images/" + englishTeamName + ".png"; // 로고 이미지 경로
                    String teamBackPath = "images/" + englishTeamName + "back.png"; // 배경 이미지 경로
                    System.out.println("✔ 선택한 팀: " + team); // 확인용 코드
                    System.out.println("✔ 변환된 영어 팀명: " + englishTeamName); // 확인용 코드
                    System.out.println("✔ 생성된 로고 경로: " + teamLogoPath); // 확인용 코드
                    
                    bookingInfo.setTeam(team); // 예매 정보에 구단명 저장
                    
                    new TeamFrame(team, teamLogoPath, teamBackPath, bookingInfo); // 팀 프레임 생성
                    dispose(); // 메인 프레임 닫기
                }
            });
        }

        
        getContentPane().add(backgroundPanel); // 프레임에 배경 패널 추가
        setLocationRelativeTo(null); // 화면 중앙에 창 띄우기
        setVisible(true); // 창을 화면에 표시


    }
}
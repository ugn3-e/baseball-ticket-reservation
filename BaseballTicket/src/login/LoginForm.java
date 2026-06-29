package login;

import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Color;
import java.awt.RenderingHints;

import main.MainFrame;

//JFrame을 상속받는 로그인 폼 클래스 선언
public class LoginForm extends JFrame {
	private Image backgroundImage;
	private Image contentImage;
	private Image logoImage;
	private Image logobackImage;
	private Image nameImage;
	
	// 생성자
    public LoginForm() {

        setTitle("로그인"); //창 제목
        setExtendedState(JFrame.MAXIMIZED_BOTH); // 전체화면으로 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //닫기 버튼 클릭 시 종료
        getContentPane().setLayout(null); //레이아웃 직접 설정
        
        //배경 이미지 로드
        backgroundImage = new ImageIcon("images\\background.png").getImage();
        contentImage = new ImageIcon("images\\panel.png").getImage();
        logoImage = new ImageIcon("images\\logo.png").getImage();
        logobackImage = new ImageIcon("images\\logoback2.png").getImage();
        nameImage = new ImageIcon("images\\nameimage.png").getImage();


        JPanel backgroundPanel = new JPanel() { // 배경 패널 생성
            @Override
            protected void paintComponent(Graphics g) { // 패널에 이미지 그리기 메서드 오버라이드
                super.paintComponent(g);  // 부모 메서드 호출
                Graphics2D g2 = (Graphics2D) g;  // Graphics2D로 변환
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 안티앨리어싱 적용
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);// 배경 이미지 그리기
                g2.drawImage(contentImage, (getWidth() - 1600) / 2, 50, 1600, getHeight() - 50, this);// 내용 패널 이미지 그리기
                //g2.drawImage(logoImage,400, 400, 350, 350, this);
                g2.drawImage(logobackImage,(getWidth() - 200)/2, 50, 200, 200, this);// 로고 배경 이미지 그리기
                g2.drawImage(nameImage,300, 400, 500, 220, this);// 이름 이미지 그리기
            }
        }; 
        backgroundPanel.setLayout(null);// 배경 패널 레이아웃 직접 지정
        setContentPane(backgroundPanel);// 프레임의 내용 패널로 설정
        
        JLabel NewLetter3 = new JLabel("playballticket에 오신걸 환영합니다.");// 환영 메시지 라벨 생성
        NewLetter3.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 20)); //폰트, 크기
        NewLetter3.setBounds(900, 370, 450, 50); //위치와 크기
        getContentPane().add(NewLetter3); // 패널에 라벨 추가
        
        JLabel LoginLetter = new JLabel("아이디와 비밀번호를 입력해주세요."); // 입력 안내 라벨 생성
        LoginLetter.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 20)); //폰트, 크기
        LoginLetter.setBounds(900, 400, 500, 50); //위치와 크기
        getContentPane().add(LoginLetter);  // 패널에 라벨 추가

        JLabel lblUser = new JLabel("아이디"); //아이디 라벨 생성
        lblUser.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 16)); //폰트, 크기
        lblUser.setBounds(900, 460, 100, 25); //위치와 크기
        getContentPane().add(lblUser); // 패널에 라벨 추가

        JTextField tfUser = new JTextField(); //아이디 입력 필드
        tfUser.setBounds(900, 485, 300, 35); //위치와 크기
        getContentPane().add(tfUser);// 패널에 라벨 추가

        JLabel lblPass = new JLabel("비밀번호"); //비밀번호 라벨 생성
        lblPass.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 16)); //폰트, 크기
        lblPass.setBounds(900, 520, 115, 25);  //위치와 크기
        getContentPane().add(lblPass);// 패널에 라벨 추가

        JPasswordField pfPass = new JPasswordField(); //비밀번호 입력 필드
        pfPass.setBounds(900, 545, 300, 35); //위치와 크기
        getContentPane().add(pfPass);// 패널에 라벨 추가

        JButton btnLogin = new JButton("로그인"); //로그인 버튼 생성
        btnLogin.setFont(new Font("한컴산뜻돋움", Font.BOLD, 24)); //폰트, 크기
        btnLogin.setBounds(900, 595, 300, 40); //위치와 크기
        getContentPane().add(btnLogin);// 패널에 라벨 추가
        
        
        JLabel NewLetter1 = new JLabel("아직 회원이 아니신가요?"); // 회원 가입 유도 메시지 라벨 생성
        NewLetter1.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 20)); //폰트, 크기
        NewLetter1.setBounds(900, 660, 350, 50); //위치와 크기
        getContentPane().add(NewLetter1); // 패널에 라벨 추가
        
        JLabel NewLetter2 = new JLabel("playballticket의 새로운 회원이 되어주세요."); // 회원 가입 유도 메시지 라벨 생성
        NewLetter2.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 20)); //폰트, 크기
        NewLetter2.setBounds(900, 690, 450, 50); //위치와 크기
        getContentPane().add(NewLetter2); // 패널에 라벨 추가
        
        JButton btnRegister = new JButton("회원가입"); //회원가입 버튼 생성
        btnRegister.setFont(new Font("한컴산뜻돋움", Font.BOLD, 24)); //폰트, 크기
        btnRegister.setBounds(900, 740, 300, 40); //위치와 크기
        getContentPane().add(btnRegister);// 패널에 라벨 추가
        
        //로그인 버튼 클릭
        btnLogin.addActionListener(e -> { 
            String username = tfUser.getText(); //입력한 아이디 가져오기
            String password = new String(pfPass.getPassword()); //입력한 비밀번호 가져오기
            String hashed = Password.hashPassword(password); //비밀번호 해싱
            
            //DB 연결 시도
            try (Connection conn = ConnectDB.getConnection()) {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?"; //정보 일치 확인
                PreparedStatement pstmt = conn.prepareStatement(sql); //위의 sql 실행을 위한 객체 생성
                pstmt.setString(1, username); //입력한 아이디
                pstmt.setString(2, hashed); //해싱된 비밀번호
                ResultSet rs = pstmt.executeQuery(); //결과 받아옴
                
                //로그인 성공
                if (rs.next()) {
                	int userId = rs.getInt("id"); // 유저 아이디 가져오기
                    UserSession.getInstance().setUserId(userId); // 싱글톤에 유저 아이디 저장
                    
                    JOptionPane.showMessageDialog(this, "로그인 성공!");  // 성공 메시지 출력
                    dispose(); //창 닫기
                    new MainFrame(); //메인 화면으로 연결
                } else {
                    JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 틀렸습니다."); // 실패 메시지 출력
                }
            } catch (Exception ex) { // 예외 발생 시
                ex.printStackTrace(); // 예외 내용 출력
            }
        });

        btnRegister.addActionListener(e -> new SignupForm()); // 회원가입 버튼 클릭 시 회원가입 폼 생성

        setVisible(true); // 창을 화면에 표시
    }
    
    public static void main(String[] args) { // 메인 메서드 시작
        new LoginForm(); //프로그램 시작
    }
}
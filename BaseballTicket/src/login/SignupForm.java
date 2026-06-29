package login;

import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

//JFrame을 상속한 회원가입 폼 클래스 선언
public class SignupForm extends JFrame {
	private Image backgroundImage;
	private Image contentImage;
	private Image logoImage;
	private Image logobackImage;
	private Image nameImage;
	
	// 생성자
    public SignupForm() {
        setTitle("회원가입"); // 창 제목 설정
        setExtendedState(JFrame.MAXIMIZED_BOTH); // 전체화면 시작
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창 닫을 때만 창만 닫힘(프로그램 종료 X)
        getContentPane().setLayout(null); // 레이아웃 직접 지정(절대 위치)

        backgroundImage = new ImageIcon("images\\background.png").getImage(); // 배경 이미지 로드
        contentImage = new ImageIcon("images\\panel.png").getImage(); // 패널 이미지 로드
        logoImage = new ImageIcon("images\\logo.png").getImage(); // 로고 이미지 로드
        logobackImage = new ImageIcon("images\\logoback2.png").getImage();  // 로고 배경 이미지 로드
        nameImage = new ImageIcon("images\\nameimage.png").getImage(); // 이름 이미지 로드

        JPanel backgroundPanel = new JPanel() { // 배경 패널 생성
            @Override
            protected void paintComponent(Graphics g) { // 패널에 이미지 그리기 오버라이드
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g; // 2D 그래픽 객체로 변환
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 안티앨리어싱 적용
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // 배경 이미지 그리기
                g2.drawImage(contentImage, (getWidth() - 1600) / 2, 50, 1600, getHeight() - 50, this); // 패널 이미지 그리기
                //g2.drawImage(logoImage,400, 400, 350, 350, this);
                g2.drawImage(logobackImage,(getWidth() - 200)/2, 50, 200, 200, this); // 로고 배경 그리기
                g2.drawImage(nameImage,300, 400, 500, 220, this); // 이름 이미지 그리기
            }
        }; 
        backgroundPanel.setLayout(null); // 배경 패널 레이아웃 직접 지정
        setContentPane(backgroundPanel); // 이 패널을 프레임의 컨텐트로 설정
        
        JLabel NewLetter3 = new JLabel("만나서 반갑습니다."); // 환영 메시지 라벨
        NewLetter3.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 20)); //폰트, 크기
        NewLetter3.setBounds(900, 370, 450, 50); //위치와 크기
        getContentPane().add(NewLetter3); // 패널에 추가
        
        JLabel LoginLetter = new JLabel("사용하실 아이디와 비밀번호를 입력해주세요."); // 안내 메시지 라벨
        LoginLetter.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 20)); //폰트, 크기
        LoginLetter.setBounds(900, 400, 500, 50); //위치와 크기
        getContentPane().add(LoginLetter); // 패널에 추가

        JLabel lblUser = new JLabel("아이디"); //아이디 라벨 생성
        lblUser.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 16)); //폰트, 크기
        lblUser.setBounds(900, 460, 100, 25); //위치와 크기
        getContentPane().add(lblUser); // 패널에 추가

        JTextField tfUser = new JTextField(); //아이디 입력 필드
        tfUser.setBounds(900, 485, 300, 35); //위치와 크기
        getContentPane().add(tfUser); // 패널에 추가

        JLabel lblPass = new JLabel("비밀번호"); //비밀번호 라벨 생성
        lblPass.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 16)); //폰트, 크기
        lblPass.setBounds(900, 520, 115, 25);  //위치와 크기
        getContentPane().add(lblPass); // 패널에 추가

        JPasswordField pfPass = new JPasswordField(); //비밀번호 입력 필드
        pfPass.setBounds(900, 545, 300, 35); //위치와 크기
        getContentPane().add(pfPass); // 패널에 추가
        
        // 비밀번호 보기 체크박스
        JCheckBox cbShowPass = new JCheckBox("보기");
        cbShowPass.setBounds(1210, 545, 70, 35); // 위치, 크기 설정
        getContentPane().add(cbShowPass); // 패널에 추가
        cbShowPass.addActionListener(e -> { // 체크박스 이벤트 리스너
            if (cbShowPass.isSelected()) {
                pfPass.setEchoChar((char) 0); // 비밀번호 보이게 설정
            } else {
                pfPass.setEchoChar('•'); // 비밀번호 숨김(도트 문자)
            }
        });

        // 비밀번호 확인 란
        JLabel lblPassConfirm = new JLabel("비밀번호 확인");
        lblPassConfirm.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 16));
        lblPassConfirm.setBounds(900, 580, 115, 25);
        getContentPane().add(lblPassConfirm); // 패널에 추가

        JPasswordField pfPassConfirm = new JPasswordField(); // 비밀번호 확인 입력 
        pfPassConfirm.setBounds(900, 605, 300, 35);
        getContentPane().add(pfPassConfirm); // 패널에 추가

        // 비밀번호 확인 보기 체크박스
        JCheckBox cbShowPassConfirm = new JCheckBox("보기");
        cbShowPassConfirm.setBounds(1210, 605, 70, 35);
        getContentPane().add(cbShowPassConfirm);
        cbShowPassConfirm.addActionListener(e -> {
            if (cbShowPassConfirm.isSelected()) {
                pfPassConfirm.setEchoChar((char) 0); // 비밀번호 보임
            } else {
                pfPassConfirm.setEchoChar('•'); // 비밀번호 숨김
            }
        });
        
        JLabel name = new JLabel("이름"); // 이름 라벨
        name.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 16)); //폰트, 크기
        name.setBounds(900, 640, 115, 25);  //위치와 크기
        getContentPane().add(name); // 패널에 추가

        JTextField pfName = new JTextField(); // 이름 입력 필드
        pfName.setBounds(900, 665, 300, 35); //위치와 크기
        getContentPane().add(pfName); // 패널에 추가
        
        JLabel birth = new JLabel("생년월일 (ex. 2001-01-01)"); // 생년월일 라벨
        birth.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 16)); //폰트, 크기
        birth.setBounds(900, 700, 200, 25);  //위치와 크기
        getContentPane().add(birth); // 패널에 추가

        JTextField pfBirth = new JTextField(); // 생년월일 입력 필드
        pfBirth.setBounds(900, 725, 300, 35); //위치와 크기
        getContentPane().add(pfBirth); // 패널에 추가
        
        JLabel email = new JLabel("이메일"); // 이메일 라벨 생성
        email.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 16)); //폰트, 크기
        email.setBounds(900, 760, 115, 25);  //위치와 크기
        getContentPane().add(email); // 패널에 추가

        JTextField pfEmail = new JTextField(); // 이메일 입력 필드
        pfEmail.setBounds(900, 785, 300, 35); //위치와 크기
        getContentPane().add(pfEmail); // 패널에 추가
        
        JLabel phone = new JLabel("휴대폰 번호"); // 휴대폰 번호 라벨 생성
        phone.setFont(new Font("한컴산뜻돋움", Font.PLAIN, 16)); //폰트, 크기
        phone.setBounds(900, 820, 115, 25);  //위치와 크기
        getContentPane().add(phone); // 패널에 추가

        JTextField pfPhone = new JTextField(); // 휴대폰 번호 입력 필드
        pfPhone.setBounds(900, 845, 300, 35); //위치와 크기
        getContentPane().add(pfPhone); // 패널에 추가

        JButton btnRegister = new JButton("회원가입"); // 회원가입 버튼 생성
        btnRegister.setFont(new Font("한컴산뜻돋움", Font.BOLD, 24)); //폰트, 크기
        btnRegister.setBounds(900, 895, 300, 40);  //위치와 크기
        getContentPane().add(btnRegister); // 패널에 추가

        // 회원가입 버튼 클릭 이벤트 리스너
        btnRegister.addActionListener(e -> {
            String username = tfUser.getText(); // 입력한 아이디
            String password = new String(pfPass.getPassword()); //입력한 비밀번호 가져오기
            String hashed = Password.hashPassword(password); //비밀번호 해싱
            
            String passwordConfirm = new String(pfPassConfirm.getPassword()); // 비밀번호 확인 입력값
            
            String realname = new String(pfName.getText()); // 이름 입력값
            String birthdate = new String(pfBirth.getText()); // 생년월일 입력값
            String emailaddress = new String(pfEmail.getText()); // 이메일 입력값
            String phonenum = new String(pfPhone.getText()); // 휴대폰 번호 입력값

            // 비밀번호 일치 확인
            if (!password.equals(passwordConfirm)) {
                JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다!"); // 일치하지 않으면 메시지
                return;
            }

            // DB에 회원정보 저장 시도
            try (Connection conn = ConnectDB.getConnection()) { // DB 연결
                String sql = "INSERT INTO users (username, password, realname, birth, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql); // 쿼리 준비
                pstmt.setString(1, username); // 아이디
                pstmt.setString(2, hashed); // 해싱된 비밀번호
                pstmt.setString(3, realname); // 이름
                pstmt.setString(4, birthdate); // 생년월일 
                pstmt.setString(5, emailaddress); // 이메일
                pstmt.setString(6, phonenum); // 휴대폰 번호
                pstmt.executeUpdate(); // 쿼리 실행(회원정보 저장)

                JOptionPane.showMessageDialog(this, "회원가입 성공!"); // 성공 메시지
                dispose(); // 창 닫기
            } catch (Exception ex) { // 예외 발생 시
                ex.printStackTrace(); // 콘솔에 에러 출력
                JOptionPane.showMessageDialog(this, "회원가입 실패!"); // 실패 메시지
            }
        });

        setVisible(true); // 창을 화면에 표시
    }
}
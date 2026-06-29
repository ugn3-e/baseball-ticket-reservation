package login;

public class UserSession { // UserSession 클래스 선언 (싱글톤 패턴)
    private static UserSession instance = new UserSession(); // 클래스 내부에서 단 하나의 인스턴스 생성
    private int userId;  // 현재 로그인한 사용자의 ID를 저장할 변수

    private UserSession() {} // 외부에서 생성자 호출을 막음

    public static UserSession getInstance() { // UserSession 인스턴스를 반환하는 메서드
        return instance; // 항상 같은 인스턴스를 반환
    }

    public int getUserId() {  // 현재 저장된 userId 값을 반환
        return userId;
    }
    
    public void setUserId(int userId) { // userId 값을 설정
        this.userId = userId;
    }
}

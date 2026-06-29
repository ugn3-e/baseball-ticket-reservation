package login;

import java.security.MessageDigest;

public class Password { // Password 클래스 선언
    public static String hashPassword(String password) {
        try {
        	// 비밀번호를 SHA-256 해시로 변환하는 정적 메서드
            MessageDigest md = MessageDigest.getInstance("SHA-256"); // SHA-256 해시 알고리즘 인스턴스 생성
            byte[] hashed = md.digest(password.getBytes("UTF-8")); // 입력된 비밀번호를 UTF-8 바이트 배열로 변환 후 해시 계산
            StringBuilder sb = new StringBuilder();  // 해시된 바이트 배열을 16진수 문자열로 변환하기 위한 StringBuilder 생성
            for (byte b : hashed) {
                sb.append(String.format("%02x", b)); // 해시 바이트 배열을 순회하며 각 바이트를 2자리 16진수 문자열로 변환해 추가
            }
            return sb.toString(); // 완성된 16진수 해시 문자열 반환
        } catch (Exception e) {
            throw new RuntimeException(e); // 런타임 예외로 감싸서 다시 던짐
        }
    }
}
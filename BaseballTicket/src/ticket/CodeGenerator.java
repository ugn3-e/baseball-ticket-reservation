package ticket;

import java.util.Random; // 난수 생성을 위한 Random 클래스 임포트

// 코드 생성기 클래스 선언
public class CodeGenerator {
    public static String generateRandomCode(int length) { // 지정한 길이만큼의 랜덤한 영문/숫자 코드 생성 메서드
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";  // 사용할 문자 집합(대문자+숫자)
        Random rnd = new Random(); // 난수 생성기 객체 생성
        StringBuilder sb = new StringBuilder(); // 결과 문자열을 만들 StringBuilder 생성
        for (int i = 0; i < length; i++) { // 원하는 길이만큼 반복
            sb.append(chars.charAt(rnd.nextInt(chars.length()))); // 랜덤 인덱스의 문자 추가
        }
        return sb.toString();  // 완성된 코드를 문자열로 반환
    }
}
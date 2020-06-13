package com.may.app.member.password;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.may.app.member.PasswordStrength;
import com.may.app.member.entity.Password;

/**
 * TDD예: 암호검사기
 * [규칙]  
 * 1. 길이가 8글자 이상
 * 2. 0부터 9 사이의 숫자를 포함 
 * 3. 대문자 포함 
 * 
 * 3가지 만족하면 '강함
 * 2가지 만족하면 '보통'
 * 1가지 만족하면 '약함' 
 * 
 * 테스트를 거쳐가면서 견고하게 기능을 완성해 나감 
 * 요구사항이 명확하고, 거기에서 조건을 잘 뽑아내야 가능
 * 구현하기 쉬운 경우부터 시작해야 테스트를 빠르게 작성할 수 있다. 또한 한 번에 많은 코드를 작성하지 않아도 되기에 버그 발생률이 낮다. 
 * 예외 상황을 초반에 잡아야 if-else 구조가 미리 만들어지기 때문에 코드를 완성한 뒤에 예외 상황을 반영할 때보다 코드 구조가 덜 바뀐다. 
 * 만들어야할 코드가 잘 떠오르지 않을 때, 상수를 사용해서 테스트를 통과시키고 뒤에 구현을 일반화하는 과정을 점진적으로 구현해가면 진행할 수 있는 밑거름이 된다.
 * 테스트를 통과한 후 리팩토링을 진행한다. 
 * 
 * TDD 시작이 안될때
 * 1. 검증 코드부터 작성한다. 
 *    -> assertEquals(기대하는 만료일, 실제 만료일)
 *    -> assertEquals(LocalDate.of(2019,8,9), realExpiryDate)
 * 2. realExpiryDate를 계산하는 메서드를 작성한다. 
 *    -> LocalDate realExpiryDate = cal.calculateExpiryDate(파라미터);
 * 3. 메서드가 어떤 일을 하는지는 모르지만 날짜를 계산해준다. 어떤 파라미터를 받을지 정한다. 
 * 4. 메서드를 구현한다. 
 */
public class PasswordStrengthMeterTest {
	private final Password pwd = new Password();
	
	private void assertStrength(String password, PasswordStrength expStr) {
		PasswordStrength result = pwd.passwordValidation(password);
		assertEquals(expStr, result);
	}
	
	@Test
	public void 암호가_모든_조건을_충족하면_암호_강도는_강함() throws Exception {
		assertStrength("ab12!@AB", PasswordStrength.STRONG);
		assertStrength("abc1!Add", PasswordStrength.STRONG);
	}
	
	@Test
	public void 길이만_8글자_미만이고_나머지_조건은_충족하는_경우() throws Exception {
		assertStrength("ab12!@A", PasswordStrength.NORMAL);
		assertStrength("Ab12!c", PasswordStrength.NORMAL);
	}
	
	@Test
	public void 숫자를_포함하지_않고_나머지_조건은_충족하는_경우() throws Exception {
		assertStrength("avjA!AaGLSK", PasswordStrength.NORMAL);
	}
	
	@Test
	public void 값이_없는_경우() throws Exception {
		assertStrength(null, PasswordStrength.INVALID);
	}
	
	@Test
	public void 빈_문자열인_경우() throws Exception {
		assertStrength("", PasswordStrength.INVALID);
	}
	
	@Test
	public void 대문자를_포함하지_않고_나머지_조건을_충족하는_경우() throws Exception {
		assertStrength("123ab@!2", PasswordStrength.NORMAL);
	}
	
	@Test
	public void 길이가_8글자_이상인_조건만_충족하는_경우() throws Exception {
		assertStrength("abdefghi", PasswordStrength.WEAK);
	}
	
	@Test
	public void 숫자_포함_조건만_충족하는_경우() throws Exception {
		assertStrength("12345", PasswordStrength.WEAK);
	}
	
	@Test
	public void 대문자_포함_조건만_충족하는_경우() throws Exception {
		assertStrength("ABC", PasswordStrength.WEAK);
	}
	
	@Test
	public void 아무_조건도_충족하지_않은_경우() throws Exception {
		assertStrength("abc", PasswordStrength.WEAK);
	}
}

package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 인터페이스에 의존
 */
@Slf4j
public class MemberServiceV4 {

    private final MemberRepository memberRepository;

    public MemberServiceV4(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String fromID, String toID, int money) {
        bizLogic(fromID, toID, money); // 비즈니스 로직
    }

    private void bizLogic(String fromID, String toID, int money) {
        Member fromMember = memberRepository.findById(fromID);
        Member toMember = memberRepository.findById(toID);

        memberRepository.update(fromID, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toID, toMember.getMoney() + money);
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}

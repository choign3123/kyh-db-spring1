package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {
    
    private final MemberRepositoryV1 memberRepository;
    
    public void accountTransfer(String fromID, String toID, int money) throws SQLException {
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

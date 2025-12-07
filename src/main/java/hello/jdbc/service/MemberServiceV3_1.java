package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

//    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromID, String toID, int money) {
        // 트랜잭션 시작
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            bizLogic(fromID, toID, money); // 비즈니스 로직
            transactionManager.commit(transaction); // 커밋. 커넥션 release도 같이 해줌.
        } catch (Exception e) {
            transactionManager.rollback(transaction); // 롤백. 커넥션 release도 같이 해줌.
            log.error("계좌이체 중 오류 발생", e);
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromID, String toID, int money) throws SQLException {
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

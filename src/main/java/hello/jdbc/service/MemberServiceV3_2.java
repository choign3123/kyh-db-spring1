package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromID, String toID, int money) {
        txTemplate.executeWithoutResult((transactionStatus -> {
            try {
                bizLogic(fromID, toID, money); // 비즈니스 로직
            } catch (SQLException e) {
                log.error("계좌이체 중 오류 발생", e);
                throw new RuntimeException(e);
            }
        }));
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

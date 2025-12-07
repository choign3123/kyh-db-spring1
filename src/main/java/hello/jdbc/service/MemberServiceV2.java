package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromID, String toID, int money) {
        Connection con = null;
        try {
            con = dataSource.getConnection(); // 커넥션 획득
            con.setAutoCommit(false); // 트랜잭션 시작

            // 비즈니스 로직
            bizLogic(con, fromID, toID, money);

            con.commit(); // 커밋
        } catch (Exception e) {
            rollback(con);
            log.error("계좌이체 중 오류 발생", e);
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }


    }

    private void bizLogic(Connection con, String fromID, String toID, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromID);
        Member toMember = memberRepository.findById(con, toID);

        memberRepository.update(con, fromID, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toID, toMember.getMoney() + money);
    }

    private static void rollback(Connection con) {
        if (con == null) {
            return;
        }

        try {
            con.rollback();
        } catch (SQLException ex) {
            log.error("롤백 중 오류 발생", ex);
            throw new IllegalStateException(ex);
        }
    }

    private static void release(Connection con) {
        if (con == null) {
            return;
        }

        try {
            con.setAutoCommit(true); // 커넥션 풀 사용을 고려. 다음 세션이 해당 커넥션을 사용할 때 수동 커밋 모드로 동작하는 상황 방지를 위해.
            con.close();
        } catch (SQLException e) {
            log.error("커넥션 release 중 오류 발생", e);
            throw new IllegalStateException(e);
        }
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}

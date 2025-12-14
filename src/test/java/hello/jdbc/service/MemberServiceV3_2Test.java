package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connction.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
class MemberServiceV3_2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberServiceV3_2 memberService;
    private MemberRepositoryV3 memberRepository;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberServiceV3_2(transactionManager, memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10_000);
        Member memberB = new Member(MEMBER_B, 10_000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // when
        log.debug("트랜잭션 시작");
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
        log.debug("트랜잭션 종료");

        // then
        Member updatedA = memberRepository.findById(memberA.getMemberId());
        assertEquals(8000, updatedA.getMoney());
        Member updatedB = memberRepository.findById(memberB.getMemberId());
        assertEquals(12000, updatedB.getMoney());
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10_000);
        Member memberEx = new Member(MEMBER_EX, 10_000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        // when
        log.debug("트랜잭션 시작");
        assertThrows(
                IllegalStateException.class,
                () -> {memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000);}
        );
        log.debug("트랜잭션 종료");

        // then
        Member updatedA = memberRepository.findById(memberA.getMemberId());
        assertEquals(10_000, updatedA.getMoney());
        Member updatedB = memberRepository.findById(memberEx.getMemberId());
        assertEquals(10_000, updatedB.getMoney());
    }
}
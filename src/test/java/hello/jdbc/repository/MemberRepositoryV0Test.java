package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 memberRepository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV0", 10_000);
        memberRepository.save(member);

        // findById
        Member findMember = memberRepository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}
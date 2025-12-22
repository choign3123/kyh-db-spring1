package hello.jdbc.repository;

import hello.jdbc.domain.Member;

import java.sql.SQLException;

// (throws SQLException 때문에) JDBC 기술에 종속적인 인터페이스
public interface MemberRepositoryEx {

    Member save(Member member) throws SQLException;
    Member findById(String memberId) throws SQLException;
    void update(String memberId, int money) throws SQLException;
    void delete(String memberId) throws SQLException;
}

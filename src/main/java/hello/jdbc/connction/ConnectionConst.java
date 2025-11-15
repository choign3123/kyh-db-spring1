package hello.jdbc.connction;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionConst {

    public static final String URL = System.getenv("db.url");
    public static final String USERNAME = System.getenv("db.username");
    public static final String PASSWORD = System.getenv("db.password");
}

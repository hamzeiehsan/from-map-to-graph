package hamzei.ehsan.database;

import hamzei.ehsan.config.Configuration;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

/**
 * Created by Ehsan Hamzei on 5/11/2017.
 */
public class SessionManager {
    private static SessionManager instance;
    private Driver driver;
    private Session session;

    private SessionManager() {
        connect();
    }

    public static SessionManager getInstance() {
        if (instance == null)
            instance = new SessionManager();
        return instance;
    }

    private Session connect() {
        driver = GraphDatabase.driver(Configuration.NEO4J_URL, AuthTokens.basic(Configuration.NEO4J_USER, Configuration.NEO4J_PASS));
        this.session = driver.session();
        return this.session;
    }

    public void close() {
        session.close();
        session = null;
        driver.close();
        driver = null;
    }

    public Session getSession() {
        if (session == null)
            connect();
        return session;
    }
}

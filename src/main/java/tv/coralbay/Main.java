package tv.coralbay;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import tv.coralbay.entities.TestEntity;
import tv.coralbay.entities.TreeEntity;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class Main
{
    private static SessionFactory sessionFactory;

    public static void main(String[] args) throws Exception
    {
        System.out.println("Hibernate version: " + org.hibernate.Version.getVersionString());


        hibernateSetup();

        testBasicNativeQuery();

        testTreeQueries();
    }

    protected static void runInTx(Consumer<Session> toRun)
    {
        try (var session = sessionFactory.openSession())
        {
            var tx = session.beginTransaction();
            try
            {
                toRun.accept(session);
                tx.commit();
            }
            catch (Exception e)
            {
                System.out.println("Exception:" + e);
                tx.rollback();
                throw e;
            }
        }
    }

    protected static void testBasicNativeQuery()
    {
        // Insert some data
        runInTx(session -> {
            session.persist(new TestEntity("event1", "2", "3"));
            session.persist(new TestEntity("event2", "a", "b"));
        });

        // do a native sql lookup
        runInTx(session -> {
            var query = session.createNativeQuery(
                            """
                                    SELECT {t.*} FROM testentity t WHERE value2 = :param1
                                    """)
                    .setParameter("param1", "a")
                    .addEntity("t", TestEntity.class)
                    .setTimeout(10);
            var item = query.getSingleResult();

            System.out.println("Retrieved item: " + item);

        });
    }

    protected static void testTreeQueries()
    {
        // Build a basic tree with 4 layers
        runInTx(session -> {
            var root = new TreeEntity("root", null, null);
            session.persist(root);

            var level1_1 = new TreeEntity("level1_1", root, null);
            session.persist(level1_1);
            var level1_2 = new TreeEntity("level1_2", root, null);
            session.persist(level1_2);

            var level1_1_1 = new TreeEntity("level1_1_1", level1_1, null);
            session.persist(level1_1_1);
            var level1_1_2 = new TreeEntity("level1_1_2", level1_1, null);
            session.persist(level1_1_2);

            var level1_2_1 = new TreeEntity("level1_2_1", level1_2, null);
            session.persist(level1_2_1);
            var level1_2_2 = new TreeEntity("level1_2_2", level1_2, null);
            session.persist(level1_2_2);

            var level1_1_1_1 = new TreeEntity("level1_1_1_1", level1_1, null);
            session.persist(level1_1_1_1);
            var level1_1_1_2 = new TreeEntity("level1_1_1_2", level1_1, null);
            session.persist(level1_1_1_2);
        });

        // do a native sql lookup
        runInTx(session -> {
            var query = session.createNativeQuery(
                            """
                                    SELECT {t.*}, {t2.*}, {t3.*}
                                    FROM tree t
                                    INNER JOIN tree t2 ON t2.parentident = t.ident
                                    INNER JOIN tree t3 ON t3.parentident = t2.ident
                                    WHERE t.value = 'root'
                                    """)
                    .addEntity("t", TreeEntity.class)
                    .addJoin("t2", "t.children")
                    .addJoin("t3", "t2.children")
                    .setTimeout(10);
            var items = query.list();

            System.out.println("Retrieved SQL items: " + items);
        });
    }

    protected static void hibernateSetup() throws Exception
    {
        Configuration config = new Configuration();
        config.configure();
        config.addAnnotatedClass(TestEntity.class);
        config.addAnnotatedClass(TreeEntity.class);
        sessionFactory = config.buildSessionFactory();
    }
}

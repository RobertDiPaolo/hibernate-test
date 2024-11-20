package tv.coralbay;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import tv.coralbay.entities.TestEntity;
import tv.coralbay.entities.TreeEntity;

public class Main
{
    private static SessionFactory sessionFactory;

    public static void main(String[] args)
    {
        hibernateSetup();

        testBasicNativeQuery();

        testTreeQueries();

    }

    protected static void testBasicNativeQuery()
    {
        // add a couple of items to the db
        sessionFactory.inTransaction(session -> {
            session.persist(new TestEntity("event1", "2", "3"));
            session.persist(new TestEntity("event2", "a", "b"));
        });

        // do a native sql lookup
        sessionFactory.inTransaction(session -> {
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
        sessionFactory.inTransaction(session -> {
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

        // do an hql lookup
        sessionFactory.inTransaction(session -> {
            var query = session.createQuery(
                    """
                        SELECT t
                        FROM TreeEntity t
                        JOIN FETCH t.children c
                        JOIN FETCH c.children
                        WHERE t.value = 'root'
                        """, TestEntity.class)
                    .setTimeout(10);
            var item = query.list();

            System.out.println("Retrieved HQL items: " + item);
        });

        // do a native sql lookup
        sessionFactory.inTransaction(session -> {
            var query = session.createNativeQuery(
                    """
                        SELECT {t.*}, {t2.*}, {t3.*}
                        FROM tree t
                        INNER JOIN tree t2 ON t2.parentident = t.ident
                        INNER JOIN tree t3 ON t3.parentident = t2.ident
                        WHERE value = 'root'
                        """)
                    .addEntity("t", TestEntity.class)
                    .addJoin("t2", "t.children")
                    .addJoin("t3", "t2.children")
                    .setTimeout(10);
            var item = query.list();

            System.out.println("Retrieved SQL items: " + item);
        });
    }

    protected static void hibernateSetup()
    {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().build();
        try
        {
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(TestEntity.class)
                    .addAnnotatedClass(TreeEntity.class)
                    .buildMetadata()
                    .buildSessionFactory();
        }
        catch (Exception e)
        {
            // The registry would be destroyed by the SessionFactory, but we
            // had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
            throw e;
        }
    }
}

package tv.coralbay.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "testentity")
public class TestEntity
{
    private String value1;
    private String value2;
    private String value3;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "testentity_sequence")
    @SequenceGenerator(name = "testentity_sequence", sequenceName = "testentity_sequence", allocationSize = 1)
    private long ident;

    public TestEntity() {}

    public TestEntity(String value1,
                      String value2,
                      String value3)
    {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    public String getValue1()
    {
        return value1;
    }

    public void setValue1(String value1)
    {
        this.value1 = value1;
    }

    public String getValue2()
    {
        return value2;
    }

    public void setValue2(String value2)
    {
        this.value2 = value2;
    }

    public String getValue3()
    {
        return value3;
    }

    public void setValue3(String value3)
    {
        this.value3 = value3;
    }

    public long getIdent()
    {
        return ident;
    }

    public void setIdent(long ident)
    {
        this.ident = ident;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestEntity that = (TestEntity) o;
        return ident == that.ident && Objects.equals(value1, that.value1) && Objects.equals(value2,
                that.value2) && Objects.equals(value3, that.value3);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value1, value2, value3, ident);
    }

    @Override
    public String toString()
    {
        return "TestEntity{" +
               "value1='" + value1 + '\'' +
               ", value2='" + value2 + '\'' +
               ", value3='" + value3 + '\'' +
               ", ident=" + ident +
               '}';
    }
}

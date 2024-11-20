package tv.coralbay.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tree")
public class TreeEntity
{
    private String value;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentident")
    private TreeEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TreeEntity> children = new HashSet<TreeEntity>();

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tree_sequence")
    @SequenceGenerator(name = "tree_sequence", sequenceName = "tree_sequence", allocationSize = 1)
    private long ident;

    public TreeEntity()
    {
    }

    public TreeEntity(String value,
                      TreeEntity parent,
                      Set<TreeEntity> children)
    {
        this.value = value;
        this.parent = parent;
        this.children = children;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public TreeEntity getParent()
    {
        return parent;
    }

    public void setParent(TreeEntity parent)
    {
        this.parent = parent;
    }

    public Set<TreeEntity> getChildren()
    {
        return children;
    }

    public void setChildren(Set<TreeEntity> children)
    {
        this.children = children;
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
        TreeEntity that = (TreeEntity) o;
        return ident == that.ident && Objects.equals(value, that.value) && Objects.equals(parent,
                that.parent) && Objects.equals(children, that.children);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value, parent, children, ident);
    }

    @Override
    public String toString()
    {
        return "TreeEntity{" +
               "value='" + value + '\'' +
               ", parent=" + parent +
               ", children=" + children +
               ", ident=" + ident +
               '}';
    }
}

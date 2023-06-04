import java.util.ArrayList;

public class Scope {
    private ScopeID id;
    private ScopeID parent;
    private ArrayList<Vars> vars;

    public Scope(ScopeID id, ScopeID parent, ArrayList<Vars> vars) {
        this.id = id;
        this.parent = parent;
        this.vars = vars;
    }

    public ScopeID getId() {
        return id;
    }

    public ScopeID getParent() {
        return parent;
    }

    public ArrayList<Vars> getVars() {
        return vars;
    }

    @Override
    public String toString() {
        return "Scope{" +
                "id='" + id + '\'' +
                ", parent=" + parent +
                ", vars=" + vars +
                '}';
    }
}

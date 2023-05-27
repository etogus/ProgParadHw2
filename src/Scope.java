import java.util.ArrayList;

public class Scope {
    private String id;
    private ParentScope parentScope;
    private ArrayList<Vars> vars;

    public Scope(String id, ParentScope parentScope, ArrayList<Vars> vars) {
        this.id = id;
        this.parentScope = parentScope;
        this.vars = vars;
    }

    public String getId() {
        return id;
    }

    public ParentScope getParentScope() {
        return parentScope;
    }

    public ArrayList<Vars> getVars() {
        return vars;
    }

    @Override
    public String toString() {
        return "Scope{" +
                "id='" + id + '\'' +
                ", parentScope=" + parentScope +
                ", vars=" + vars +
                '}';
    }
}

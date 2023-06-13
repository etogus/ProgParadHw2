import java.util.ArrayList;

public class Scope {
    private String id;
    private String parent;
    private ArrayList<Vars> vars;

    public Scope(String id, String parent, ArrayList<Vars> vars) {
        this.id = id;
        this.parent = parent;
        this.vars = vars;
    }

    public String getId() {
        return id;
    }

    public String getParent() {
        return parent;
    }

    public ArrayList<Vars> getVars() {
        return vars;
    }

    @Override
    public String toString() {
        return "Scope{" +
                "id=" + id + ", " +
                "vars=" + vars +
                '}';
    }
}

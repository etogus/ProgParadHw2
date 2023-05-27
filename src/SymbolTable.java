import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Scope> scopes;

    public SymbolTable(HashMap<String, Scope> scopes) {
        this.scopes = scopes;
    }

    public HashMap<String, Scope> getScopes() {
        return scopes;
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "scopes=" + scopes +
                '}';
    }
}

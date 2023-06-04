import java.util.HashMap;

public class SymbolTable {
    private HashMap<ScopeID, Scope> scopes;

    public SymbolTable(HashMap<ScopeID, Scope> scopes) {
        this.scopes = scopes;
    }

    public HashMap<ScopeID, Scope> getScopes() {
        return scopes;
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "scopes=" + scopes +
                '}';
    }
}

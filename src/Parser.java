import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private SymbolTable symbolTable;
    private ScopeID globalScopeID;
    private Scope currentScope;

    public void runParser() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/test"));
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();

        globalScopeID = new ScopeID("global");
        currentScope = new Scope(globalScopeID, null, new ArrayList<>());
        symbolTable = new SymbolTable(new HashMap<>());
        symbolTable.getScopes().put(globalScopeID, currentScope);

        for(int i = 0; i < lines.size(); i++) {
            line = lines.get(i);

            if (readFun(line, i)) {
                continue;
            }
            if (readVar(line, i)) {
                continue;
            }
            if (enterScope(line)) {
                continue;
            }
            exitScope(line);
        }
        System.out.println(currentScope);
    }

    public boolean readFun(String line, int lineIndex) {
        String funType;
        String funName;
        String funParam;
        String rx = "^(\\w+)\\s+(\\w+)\\((.*?)\\)\\s*\\{";
        Pattern p = Pattern.compile(rx);
        Matcher matcher = p.matcher(line);
        if (matcher.find()){
            funType = matcher.group(1);
            funName = matcher.group(2);
            funParam = matcher.group(3);
            symbolTable.getScopes().get(globalScopeID).getVars().add(new Vars(funName, funType, lineIndex + 1));
            enterScope(line);
            if(funParam != null) {
                readVar(funParam, lineIndex);
            }
            return true;
        }
        return false;
    }

    public boolean readVar(String line, int lineIndex) {
        String varType;
        String varName;
        String extraVar;
        String temp;
        String rx = "\\b(int|float|char|bool)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(?:\\s*,\\s*([a-zA-Z_][a-zA-Z0-9_]*)(?:\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*)*)?\\s*;*";
        Pattern p = Pattern.compile(rx);
        Matcher matcher = p.matcher(line);
        if (matcher.find()){
            varType = matcher.group(1);
            varName = matcher.group(2);
            extraVar = matcher.group(3);
            temp = varType;
            currentScope.getVars().add(new Vars(varName, varType, lineIndex + 1));
            if(extraVar != null) {
                if(extraVar.equals("int") || extraVar.equals("float") || extraVar.equals("char") || extraVar.equals("String") || extraVar.equals("double")) {
                    temp = extraVar;
                    extraVar = line.substring(line.indexOf(","));
                    extraVar = extraVar.substring(extraVar.indexOf(temp) + temp.length() + 1);
                }
            }

            while(extraVar != null) {
                if(extraVar.contains(",")) {
                    if(extraVar.contains("int") || extraVar.contains("float") || extraVar.contains("char") || extraVar.contains("bool")) {
                        varType = extraVar.substring(0, extraVar.indexOf(" "));
                        varName = extraVar.substring(extraVar.indexOf(" "), extraVar.indexOf(","));
                        extraVar = extraVar.substring(extraVar.indexOf(","));
                    } else {
                        varName = extraVar.substring(0, extraVar.indexOf(","));
                        extraVar = extraVar.substring(extraVar.indexOf(","));
                        varType = temp;
                    }
                } else {
                    varName = extraVar;
                    extraVar = null;
                    varType = temp;
                }
                currentScope.getVars().add(new Vars(varName, varType, lineIndex + 1));
            }
            return true;
        }
        return false;
    }

    public boolean enterScope(String line) {
        String scopeName;
        ScopeID newScopeID;
        String rx = "^\\s*(.*?)\\{\\s*$";
        Pattern p = Pattern.compile(rx);
        Matcher matcher = p.matcher(line);
        if (matcher.find()) {
            scopeName = matcher.group(1);
            if(scopeName.equals("")) {
                scopeName = "Some Block Scope";
            }
            newScopeID = new ScopeID(scopeName);
            Scope newScope = new Scope(newScopeID, currentScope.getId(), new ArrayList<>());
            symbolTable.getScopes().put(newScopeID, newScope);
            currentScope = newScope;
            return true;
        }
        return false;
    }

    public void exitScope(String line) {
        String rx = "^\\s*}\\s*$";
        Pattern p = Pattern.compile(rx);
        Matcher matcher = p.matcher(line);
        if (matcher.find() && !(currentScope.getId().getId().equals(globalScopeID.getId()))) {
            System.out.println(currentScope);
            currentScope = symbolTable.getScopes().get(currentScope.getParent());
        }
    }
}

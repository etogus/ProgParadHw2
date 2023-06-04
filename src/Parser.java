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
        //System.out.println("=== READFUN ===");
        //System.out.println("line = " + line);
        //System.out.println("matcher.find() = " + matcher.find());
        if (matcher.find()){
            funType = matcher.group(1);
            //System.out.println("funType = " + funType);
            funName = matcher.group(2);
            //System.out.println("funName = " + funType);
            funParam = matcher.group(3);
            //System.out.println("funParam = " + funType);
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
        String rx = "\\b(int|float|char|bool)\\s+([a-zA-Z_][a-zA-Z0-9_]*)(?:\\s*,\\s*([a-zA-Z_][a-zA-Z0-9_]*)(?:\\s*,\\s*[a-zA-Z_][a-zA-Z0-9_]*)*)?\\s*;";
        Pattern p = Pattern.compile(rx);
        Matcher matcher = p.matcher(line);
        //System.out.println("=== READVAR ===");
        //System.out.println("line = " + line);
        //System.out.println("matcher.find() = " + matcher.find());
        if (matcher.find()){
            varType = matcher.group(1);
            //System.out.println("varType = " + varType);
            varName = matcher.group(2);
            //System.out.println("varName = " + varName);
            extraVar = matcher.group(3);
            //System.out.println("extraVar = " + extraVar);
            temp = varType;
            currentScope.getVars().add(new Vars(varType, varName, lineIndex + 1));

            while(extraVar != null) {
                //rx = "^\\s*,?\\s*(int|float|char|bool)?\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*((,\\s*(int|float|char|bool)?\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*)*)?;?\\s*$";
                //p = Pattern.compile(rx);
                //matcher = p.matcher(extraVar);
                //System.out.println(matcher.find());
                //if(!(matcher.find())) break;
                //System.out.println("=== READVAR EXTRA ===");
                if(extraVar.contains(",")) {
                    varName = extraVar.substring(0, extraVar.indexOf(","));
                    extraVar = extraVar.substring(extraVar.indexOf(","));
                } else {
                    varName = extraVar;
                    extraVar = null;
                }
                //varName = extraVar.substring(0, extraVar.indexOf(",")); //matcher.group(2);
                //System.out.println("varName = " + varName);
                varType = temp;
                //if(!(matcher.group(1).isEmpty())) {
                //    varType = matcher.group(1);
                //}
                //System.out.println("varType = " + varType);
                currentScope.getVars().add(new Vars(varType, varName, lineIndex + 1));
                //extraVar = extraVar.substring(extraVar.indexOf(","));//matcher.group(3);
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

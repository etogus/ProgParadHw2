import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private SymbolTable symbolTable;
    private ParentScope parentScope;
    private Scope curScope;

    public void runParser() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/test"));
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();

        parentScope = new ParentScope("global");
        curScope = new Scope(parentScope.getId(), null, new ArrayList<>());
        symbolTable = new SymbolTable(new HashMap<>());

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
            if (exitScope(line)) {
                continue;
            }
        }
        System.out.println(curScope);
    }

    public boolean readFun(String line, int lineIndex) {
        String funType;
        String funName;
        String funParam;
        String rx = "^(\\w+)\\s+(\\w+)\\((.*?)\\)\\s*\\{";
        Pattern p = Pattern.compile(rx);
        Matcher matcher = p.matcher(line);
        System.out.println("=== READFUN ===");
        System.out.println("line = " + line);
        System.out.println("matcher.find() = " + matcher.find());
        if (matcher.find()){
            funType = matcher.group(1);
            System.out.println("funType = " + funType);
            funName = matcher.group(2);
            System.out.println("funName = " + funType);
            funParam = matcher.group(3);
            System.out.println("funParam = " + funType);
            symbolTable.getScopes().get(parentScope.getId()).getVars().add(new Vars(funName, funType, lineIndex + 1));
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
        System.out.println("=== READVAR ===");
        System.out.println("line = " + line);
        //System.out.println("matcher.find() = " + matcher.find());
        if (matcher.find()){
            varType = matcher.group(1);
            System.out.println("varType = " + varType);
            varName = matcher.group(2);
            System.out.println("varName = " + varName);
            extraVar = matcher.group(3);
            System.out.println("extraVar = " + extraVar);
            temp = varType;

            curScope.getVars().add(new Vars(varType, varName, lineIndex + 1));

            while(extraVar != null) {
                rx = "^\\s*,?\\s*(int|float|char|bool)?\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s*((,\\s*(int|float|char|bool)?\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*)*)?;?\\s*$";
                p = Pattern.compile(rx);
                matcher = p.matcher(extraVar);
                if(!(matcher.find())) break;
                varName = matcher.group(2);
                varType = temp;
                if(!(matcher.group(1).isEmpty())) {
                    varType = matcher.group(1);
                }
                curScope.getVars().add(new Vars(varType, varName, lineIndex + 1));
                extraVar = matcher.group(3);
            }
            return true;
        }
        return false;
    }

    public boolean enterScope(String line) {
        String scopeName;
        ParentScope newParentScope;
        String rx = "^\\s*(.*?)\\{\\s*$";
        Pattern p = Pattern.compile(rx);
        Matcher matcher = p.matcher(line);
        if (matcher.find()) {
            scopeName = matcher.group(1);
            if(scopeName.equals("")) {
                scopeName = "Unnamed Scope";
            }
            newParentScope = new ParentScope(scopeName);
            Scope newScope = new Scope(newParentScope.getId(), newParentScope, new ArrayList<>());
            symbolTable.getScopes().put(scopeName, newScope);
            curScope = newScope;
            return true;
        }
        return false;
    }

    public boolean exitScope(String line) {
        String rx = "^\\s*}\\s*$";
        Pattern p = Pattern.compile(rx);
        Matcher matcher = p.matcher(line);
        if (matcher.find() && !(curScope.getId().equals(parentScope.getId()))) {
            System.out.println(curScope);
            curScope = symbolTable.getScopes().get(curScope.getParentScope().getId());
            return true;
        } else return false;
    }
}

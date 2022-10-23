package visitor;
import java.util.*;

class param {
    public String paramType = new String();
    public String paramName = new String();
}

class method {
    public String className;
    public String accessType;
    public String dataType;
    //public HashMap<String, String> params = new HashMap<String, String>();
    public Vector<param> params = new Vector<param>(); 
    public HashMap<String, String> locals = new HashMap<String, String>();
}

class classDef {
    public String name = new String();
    public HashMap<String, String> fields = new HashMap<String, String>();
    public HashMap<String, method> methods = new HashMap<String, method>();
    //public HashMap<String, ArrayList<Object>> classElements = new HashMap<String, ArrayList<Object>>();
}

public class SymbolTable {
    public HashMap<String, String> inheritanceGraph = new HashMap<String, String>();
    public HashMap<String, classDef> classes = new HashMap<String, classDef>();
    public HashSet<String> classNames = new HashSet<String>();
    public void printClassDefs() {
        for(Map.Entry C : classes.entrySet()) {
            System.out.println(C.getKey());
            classDef p = classes.get(C.getKey());
            System.out.println("ClassName : " + p.name);
            System.out.println("Fields : ");
            for(Map.Entry F : p.fields.entrySet()) {
                System.out.println(F.getKey() + " " + F.getValue());
            }

            System.out.println("Methods : ");
            for(Map.Entry M : p.methods.entrySet()) {
                method met = p.methods.get(M.getKey());
                System.out.println(M.getKey() + " " + met.className + " " + met.accessType + " " + met.dataType);
                for(param pr : met.params) {
                    System.out.println(pr.paramName + " " + pr.paramType);
                }
                for(Map.Entry lr : met.locals.entrySet()) {
                    System.out.println(lr.getKey() + " " + lr.getValue());
                }
            }
        }
    }
}

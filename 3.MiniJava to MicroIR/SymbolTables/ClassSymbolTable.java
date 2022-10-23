package SymbolTables;
import java.util.*;

public class ClassSymbolTable 
{
    public String className;
    public String parentClassName;
    public HashMap<String,String> varDecList;
    public HashMap<String,MethodSymbolTable> methodList;
    public AllocObject obj;
    public ClassSymbolTable()
    {
       className = null;
       parentClassName = null;
       obj = new AllocObject();
       varDecList = new HashMap<String,String>();
       methodList = new HashMap<String,MethodSymbolTable>();
       varDecList.clear();
       methodList.clear();
    }
    public void insertClassVariable(String name,String type)
    {
       varDecList.put(name,type);
    }
    public void insertClassMethod(MethodSymbolTable m)
    {
       String id = m.methodName;
       methodList.put(id,m);
    }
    public void print()
    {
       /*System.out.println(className);
       System.out.println(parentClassName);
       System.out.println("#####class variables");
       for(String var: varDecList.keySet())
       {
         System.out.println(var + ":" + varDecList.get(var));
       }
       System.out.println("##### class methods");
       for(String met: methodList.keySet())
       {
         methodList.get(met).print();
       }*/
       System.out.println(className);
       obj.print();
    }
    public HashSet<String> getPubProtMethods()
    {
       HashSet<String> res = new HashSet<String>();
       for(MethodSymbolTable m : methodList.values())
       {
          if(m.accessType != "private")
          {
             res.add(m.methodName);
          }
       }
       return res;
    }
 }
package SymbolTables;
import java.util.*;

public class MethodSymbolTable {
    public String methodName;
    public String accessType;
    public String ownerClass;
    public int paramCount;
    public HashMap<String,String> paramList;
    public HashMap<Integer,String> paramTypes;
    public HashMap<String,Integer> paramPos;
    public HashMap<String,String> varDecList;
    public String returnType;
    public MethodSymbolTable()
    {
       methodName = null;
       accessType = null;
       ownerClass = null;
       paramCount = 0;
       paramList = new HashMap<String,String>();
       varDecList = new HashMap<String,String>();
       paramTypes = new HashMap<Integer,String>();
       paramPos = new HashMap<String,Integer>();
       paramPos.clear();
       paramTypes.clear();
       paramList.clear();
       varDecList.clear();
       returnType = null;
    }
    public void insertParameter(String name,String type,int pos)
    {
        paramList.put(name,type);
        paramTypes.put(pos,type);
        paramPos.put(name,pos);
    }
    public void insertVariable(String name,String type)
    {
        varDecList.put(name,type);
    }
    public void print()
    {
       System.out.println("###### new method");
        System.out.println(methodName);
        System.out.println(accessType);
        System.out.println(paramCount);
        System.out.println(returnType);
        System.out.println("###### method param with types");
        for(String p: paramList.keySet())
        {
           System.out.println(p + ":" + paramList.get(p));
        }
        System.out.println("###### method param numbers with types");
        for(int i = 0;i<paramCount;++i)
        {
           System.out.println((i+1) + paramTypes.get((Integer)(i+1)));
        }
        System.out.println("###### method variables");
        for(String p:  varDecList.keySet())
        {
            System.out.println(p + ":" + varDecList.get(p));
        }
    }
}
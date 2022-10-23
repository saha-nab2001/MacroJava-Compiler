package SymbolTables;
import java.util.*;

public class AllocObject 
{
    public ArrayList<String> functionTableList;
    public HashMap<String,Integer> functionTableOffset;
    public HashMap<String,String> overList;
    public ArrayList<String> elemList;
    public HashMap<String,Integer> elemOffset;
    public int size;
    public AllocObject()
    {
        functionTableList = new ArrayList<String>();
        functionTableOffset = new HashMap<String,Integer>();
        elemList = new ArrayList<String>();
        elemOffset = new HashMap<String,Integer>();
        overList = new HashMap<String,String>();
        size = 0;
    }
    public void print()
    {
        System.out.println( "%%% FUNCTION TABLE LIST" + functionTableList.toString());
        System.out.println("^^^^ VAR TABLE LIST" + elemList.toString());
        System.out.println("##### function table offsets");
        for(String fn:functionTableOffset.keySet())
        {
            System.out.println(fn+":::"+functionTableOffset.get(fn));
        }
    }
}

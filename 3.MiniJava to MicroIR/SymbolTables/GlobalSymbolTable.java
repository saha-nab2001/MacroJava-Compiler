package SymbolTables;
import java.util.*;

public class GlobalSymbolTable 
{
    public HashMap<String,ClassSymbolTable> classList;
    public GlobalSymbolTable()
    {
       classList = new HashMap<String,ClassSymbolTable>();
       classList.clear();
    }
    public void insertClass(ClassSymbolTable table)
    {
       String s = table.className;
       classList.put(s, table);
    }
    public void print()
    {
       for(String key : classList.keySet())
       {
         classList.get(key).print();
       }
    }
    public void recObjAllocation(String key,HashMap<String,Boolean> flags)
    {
       Stack<String> parents = new Stack<String> ();
       String curParent = key;
       while(curParent!=null)
       {
          parents.push(curParent);
          curParent = (classList.get(curParent)).parentClassName;
       }
       String first = null;
       String next = null;
       while(!parents.empty())
       {
          next = parents.peek();
          if(flags.get(next))
          {
             parents.pop();
             first = next;
             continue;
          }
          else
          {
            computeAllocation(first,next);
            flags.put(next,true);
            parents.pop();
            first = next;
          }
       }
    }
    public void computeAllocation(String parent,String child)
    {
       if(parent == null)
       {
          ClassSymbolTable childCls = classList.get(child);
          AllocObject childObj = childCls.obj;

          //method section
          ArrayList<String> mList = new ArrayList<String>((childCls.methodList).keySet());
          HashMap<String,Integer> mOffsets = new HashMap<String,Integer>();
          HashMap<String,String> oList = new HashMap<String,String>();
          String mname;
          for(int i=0;i<mList.size();++i)
          {
             mname = mList.get(i);
             mname = child + "_" + mname;
             mList.set(i,mname);
             mOffsets.put(mname,i);
          }

          //var section
          ArrayList<String> varList = new ArrayList<String>((childCls.varDecList).keySet());
          HashMap<String,Integer> varOffsets = new HashMap<String,Integer>();
          String vname;
          for(int i=0;i<varList.size();++i)
          {
             vname = varList.get(i);
             vname = child + "_" + vname;
             varList.set(i,vname);
             varOffsets.put(vname,i);                          // add + 1 if needed
          }
          childObj.elemList = varList;
          childObj.elemOffset = varOffsets;
          childObj.functionTableList = mList;
          childObj.functionTableOffset = mOffsets;
          childObj.overList = oList;
          childObj.size = (varList.size()+1);                  // * 4
       }
       else
       {
         ClassSymbolTable childCls = classList.get(child);
         ClassSymbolTable parentCls = classList.get(parent);

         AllocObject childObj = childCls.obj;
         AllocObject parObj = parentCls.obj;

         //method section
         ArrayList<String> chdMList = new ArrayList<String>((childCls.methodList).keySet());
         HashMap<String,Integer> mOffsets = new HashMap<String,Integer>(parObj.functionTableOffset);
         ArrayList<String> parMList = new ArrayList<String> (parObj.functionTableList);
         HashMap<String,String> oList = new HashMap<String,String>(parObj.overList);

         String mname;
         int parMethodCount = mOffsets.size();
         for(int i=0;i < chdMList.size();++i)
         {
            mname = chdMList.get(i);
            mname = child + "_" + mname;
            chdMList.set(i,mname);
            mOffsets.put(mname,i + parMethodCount);
         }
         parMList.addAll(chdMList);

         String temp = classList.get(child).parentClassName;
         HashSet<String> common;
         while(temp!=null)
         {
            common = new HashSet<String>((classList.get(child)).methodList.keySet());
            common.retainAll(classList.get(temp).methodList.keySet());
            for(String fn : common)
            {
               oList.put(temp+'_'+fn,child+'_'+fn);
            }
            temp = (classList.get(temp)).parentClassName;
         }

         //variable section
         ArrayList<String> chdVList = new ArrayList<String>((childCls.varDecList).keySet());
         HashMap<String,Integer> vOffsets = new HashMap<String,Integer>(parObj.elemOffset);
         ArrayList<String> parVList = new ArrayList<String> (parObj.elemList);

         String vname;
         int parVarCount = vOffsets.size();
         for(int i=0;i < chdVList.size();++i)
         {
            vname = chdVList.get(i);
            vname = child + "_" + vname;
            chdVList.set(i,vname);
            vOffsets.put(vname,i + parVarCount);
         }
         parVList.addAll(chdVList);

         childObj.elemList = parVList;
         childObj.elemOffset = vOffsets;
         childObj.functionTableList = parMList;
         childObj.functionTableOffset = mOffsets;
         childObj.overList = oList;
         childObj.size = (chdVList.size()+1);                        // * 4 maybe required
       }
    }
    public void createObjectAllocation()
    {
       HashMap<String,Boolean> flags = new HashMap<String,Boolean>();
       for(String key : classList.keySet())
       {
          flags.put(key,false);
       }
       for(String key : classList.keySet())
       {
          if(flags.get(key) == false)
          {
             recObjAllocation(key,flags);
          }
       }
    }
 }

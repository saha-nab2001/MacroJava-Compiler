package visitor;
import syntaxtree.*;
import java.util.*;

class Pair {
    public int fst = 0;
    public int snd = 0;
    public String pairStr = "";
}

public class SymbolTable{
    HashMap<String, MethodTable> methodMap = new HashMap<String,MethodTable>();
    MethodTable presentMethod = new MethodTable();
}

class MethodTable{

    public int stackSlotCount = 0;
    public int sRegisterBase = 0;
    public int maximumParamCount = 0;
    public int tRegisterBase = 0;
    public int stackVarCount = 0;
    public int stackVarBase = 0;
    public int blockCount = 1;
    public int tempVarCount = 0;
    public int stackCount = 0;
    public Vector<String> registerList;

    MethodTable() {
        this.registerList = new Vector<String>(18);
        for(int i = 0; i <= 7; ++i) {
            registerList.add("s" + Integer.toString(i));
        }
        for(int i = 0; i <= 9; ++i) {
            registerList.add("t" + Integer.toString(i));
        }
    }

    public String functionName = null;
    public String paramCount = null;
    public BlockTable presentBlock = null;
    public String presentLabel = null;

    public HashMap<String,BlockTable> labelMap = new HashMap<String,BlockTable>();
    public HashMap<String,String> stackVarMap = new HashMap<String,String>();
    public HashMap<String,String> registerMap = new HashMap<String,String>();

    public LinkedHashMap<String,Pair> liveIntervalMap = new LinkedHashMap<String,Pair>();
    public LinkedHashMap<Integer,BlockTable> blockMap = new LinkedHashMap<Integer,BlockTable>();

    public ArrayList<String> tempList = new ArrayList<String>();
    public boolean isPresStack = false;

}

class BlockTable{
    ArrayList<String> in_n = new ArrayList<String>();
    ArrayList<String> out_n = new ArrayList<String>();
    ArrayList<BlockTable> succ_n = new ArrayList<BlockTable>();
    ArrayList<String> inTemp_n = new ArrayList<String>();
    ArrayList<String> outTemp_n = new ArrayList<String>();
    ArrayList<String> use_n = new ArrayList<String>();
    ArrayList<String> def_n = new ArrayList<String>();
    String label = null;

}
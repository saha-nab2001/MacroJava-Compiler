package visitor;
import syntaxtree.*;
import java.util.*;

public class MiniRAConvertor<R,A> extends GJDepthFirst<R,A> {

    public void gen (String str) {
        System.out.println(str);
    }

    SymbolTable SymTab = new SymbolTable();

    public R visit(NodeList n, A argu) {
        R _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
           e.nextElement().accept(this,argu);
           _count++;
        }
        return _ret;
     }
  
     public R visit(NodeListOptional n, A argu) {
        if ( n.present() ) {
           R _ret=null;
           int _count=0;
           for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
              e.nextElement().accept(this,argu);
              _count++;
           }
           return _ret;
        }
        else
           return null;
     }
  
     public R visit(NodeOptional n, A argu) {
        if ( n.present() )
           return n.node.accept(this,argu);
        else
           return null;
     }
  
     public R visit(NodeSequence n, A argu) {
        R _ret=null;
        int _count=0;
        for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
           e.nextElement().accept(this,argu);
           _count++;
        }
        return _ret;
     }

    public R visit(NodeToken n, A argu) { return (R) n.tokenImage; }

    /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
    public R visit(Goal n, A argu) {
        R _ret=null;

        SymTab.presentMethod.stackVarCount++;
        SymTab.presentMethod.tempVarCount++;
        n.f0.accept(this,(A)"First Pass");

        SymTab.presentMethod.stackVarCount = 0;
        SymTab.presentMethod.tempVarCount = 0;

        MethodTable mobj = new MethodTable();
        mobj.functionName = "MAIN";
        mobj.paramCount = "0";
        SymTab.methodMap.put("MAIN", mobj);

        SymTab.presentMethod = SymTab.methodMap.get("MAIN");
        
        SymTab.presentMethod.tempVarCount++;
        n.f1.accept(this,(A)"First Pass");

        SymTab.presentMethod.stackVarCount++;
        n.f2.accept(this,(A)"First Pass");

        SymTab.presentMethod.stackVarCount = 0;
        SymTab.presentMethod.tempVarCount = 0;

        BlockTable special = new BlockTable();

        int regsAllocated = 0;

        if(Integer.parseInt(SymTab.presentMethod.paramCount) < 4) {
            regsAllocated = Integer.parseInt(SymTab.presentMethod.paramCount);
        }
        else {
            regsAllocated = 4;
        }

        int i = 0;

        while(i < regsAllocated){
            special.def_n.add("TEMP" + (i));
            i++;            
        }

        special.succ_n.add(SymTab.presentMethod.blockMap.get(1));
        SymTab.presentMethod.blockMap.put(0,special);

        int runNum = 0;

        boolean flag = true;

        while(flag){
            
            runNum++;  

            flag = false;

            
            for(Iterator itr = SymTab.presentMethod.blockMap.entrySet().iterator();itr.hasNext();){
                Map.Entry pair = (Map.Entry) itr.next();
                BlockTable node = (BlockTable) pair.getValue();
                Integer key = (Integer) pair.getKey();

                Iterator<String> itr1 = null;

                node.inTemp_n = new ArrayList<String>();
                node.outTemp_n= new ArrayList<String>();

                itr1 = node.out_n.iterator();

                for(;itr1.hasNext();){
                    node.outTemp_n.add(itr1.next());
                }

                itr1 = node.in_n.iterator();
                for(;itr1.hasNext();){
                    node.inTemp_n.add(itr1.next());
                }
            
                ArrayList noDefs = new ArrayList<String>();

                node.in_n =  new ArrayList<String>();

                for(itr1 = node.use_n.iterator();itr1.hasNext();node.in_n.add(itr1.next()));
                                    
                for(itr1 = node.out_n.iterator();itr1.hasNext();){

                    String tmp = itr1.next();
                    boolean isPres = node.def_n.contains(tmp);
                    if(isPres) {
                        //Need to fill this
                    }
                    else {
                        noDefs.add(tmp);
                    }
                }

            
                for(itr1 = noDefs.iterator();itr1.hasNext();){
                    String temp = itr1.next();
                    boolean isPres = node.in_n.contains(temp);
                    if(isPres) {
                        // Need to fill this
                    }
                    else {
                        node.in_n.add(temp);
                    }
                }

                node.out_n = new ArrayList<String>();

                for(Iterator<BlockTable> itr0 = node.succ_n.iterator();itr0.hasNext();){

                    BlockTable child = itr0.next();

                    for(Iterator<String> itr2 = child.in_n.iterator(); itr2.hasNext(); node.out_n.add(itr2.next()));
                }
            }

            for(Iterator itr = SymTab.presentMethod.blockMap.entrySet().iterator();itr.hasNext();){

                Map.Entry pair = (Map.Entry) itr.next();
                BlockTable node = (BlockTable) pair.getValue();
                Integer key = (Integer) pair.getKey();

                flag = (flag) || (!node.inTemp_n.equals(node.in_n) || !node.outTemp_n.equals(node.out_n));
            }
                    
        }
        
        int iter = 4;
        
        while(iter < Integer.parseInt(SymTab.presentMethod.paramCount)){
            String str = ("TEMP"+Integer.toString(iter));
            SymTab.presentMethod.tempList.remove(str);
            iter++;
        }

        for(Iterator<String> itr = SymTab.presentMethod.tempList.iterator(); itr.hasNext();){
            Pair active_range = new Pair();

            active_range.pairStr = (String) itr.next();

            active_range.fst = 0;

            while(active_range.fst < SymTab.presentMethod.blockCount){

                boolean isPres = SymTab.presentMethod.blockMap.get(active_range.fst).def_n.contains(active_range.pairStr);
                if(isPres) 
                    break;
                    active_range.fst++;
            }

            active_range.snd = SymTab.presentMethod.blockCount;
            active_range.snd--;

            while(active_range.snd >= 0){

                boolean isPres = SymTab.presentMethod.blockMap.get(active_range.snd).in_n.contains(active_range.pairStr);
            
                if(isPres)
                    break;

                    active_range.snd--;
            }

            if(active_range.snd < active_range.fst){
                itr.remove();
                continue;
            }

            SymTab.presentMethod.liveIntervalMap.put(active_range.pairStr, active_range);
        }

        ArrayList<Integer> activeRegisters = new ArrayList<Integer>();
        ArrayList<Pair> liveIntervals = new ArrayList<Pair>();
        ArrayList<Pair> activeIntervals = new ArrayList<Pair>();

        Vector<Boolean> freeRegPool = new Vector<Boolean>(18);

        int u = 0;
        while(u < 18) {
            freeRegPool.add(true);
            u++;
        }

        for(Iterator itr = SymTab.presentMethod.liveIntervalMap.entrySet().iterator();itr.hasNext();){

            int iter1 = 0;
            Map.Entry pair = (Map.Entry)itr.next();

            int pos=-1;
            Pair curr_range = (Pair)pair.getValue();


            for(Pair v : liveIntervals) {
                if( curr_range.fst < liveIntervals.get(iter1).fst){
                    pos = iter1;
                    break;
                }
                iter1++;
            }

            boolean found = false;
            if(pos != -1) found = true;
            if(found) 
                liveIntervals.add(pos,curr_range);
            else
                liveIntervals.add(curr_range);
        }

        i = 0;
        while(i< liveIntervals.size()) {
            
            int j = 0;
            while(j < activeIntervals.size()) {

                if(liveIntervals.get(i).fst > activeIntervals.get(j).snd){
                    freeRegPool.set(activeRegisters.get(j).intValue(), true);
                    activeRegisters.remove(j);
                    activeIntervals.remove(j);
                }

                else
                    break;

                j++;
            }

            if(activeRegisters.size() == 18){

                SymTab.presentMethod.isPresStack = true;
                Pair spill = activeIntervals.get(activeIntervals.size()-1);

                if(liveIntervals.get(i).snd >= spill.snd)
                    SymTab.presentMethod.stackVarMap.put(liveIntervals.get(i).pairStr,Integer.toString(SymTab.presentMethod.stackCount++));

                else {

                    activeIntervals.remove(activeIntervals.size()-1);

                    int freshReg = activeRegisters.get(activeRegisters.size()-1);

                    activeRegisters.remove(activeRegisters.size()-1);
                
                    SymTab.presentMethod.registerMap.put(liveIntervals.get(i).pairStr, SymTab.presentMethod.registerList.get(freshReg));

                    SymTab.presentMethod.registerMap.remove(spill.pairStr);

                    SymTab.presentMethod.stackVarMap.put(spill.pairStr, Integer.toString(SymTab.presentMethod.stackCount++));

                    int pos = -1;

                    int k = 0;

                    for(Pair v : activeIntervals) {
                        if(v.snd > liveIntervals.get(i).snd) {
                            pos = k;
                            break;
                        }
                        k++;
                    }

                    boolean isPres = true;
                    if(pos == -1) isPres = false;
                    
                    if(isPres){
                        activeIntervals.add(pos, liveIntervals.get(i));
                        activeRegisters.add(pos,Integer.valueOf(freshReg));
                    }
                    else{
                        activeIntervals.add(liveIntervals.get(i));
                        activeRegisters.add(Integer.valueOf(freshReg));
                    }
                }
                
            }
            else if(activeRegisters.size() != 18){

                int freshReg = 18;

                for(int k = 0; k < 18; ++k){
                    if(freeRegPool.get(k)){
                        freeRegPool.set(k, false);
                        freshReg = k;
                        break;
                    }
                }

                SymTab.presentMethod.registerMap.put(liveIntervals.get(i).pairStr,SymTab.presentMethod.registerList.get(freshReg));

                int pos = -1;

                int k = 0;

                for(Pair v : activeIntervals) {
                    if(v.snd > liveIntervals.get(i).snd) {
                        pos = k;
                        break;
                    }
                    k++;
                }

                boolean flg = false;
                if(pos + 1 != 0) flg = true;
                if(flg){
                    activeIntervals.add(pos, liveIntervals.get(i));
                    activeRegisters.add(pos,Integer.valueOf(freshReg));
                }
                else{
                    activeIntervals.add(liveIntervals.get(i));
                    activeRegisters.add(Integer.valueOf(freshReg));
                }
            }
            i++;
        }

        SymTab.presentMethod.stackSlotCount += Math.max(Integer.parseInt(SymTab.presentMethod.paramCount) - 4, 0);

        SymTab.presentMethod.sRegisterBase = SymTab.presentMethod.stackSlotCount;
        
        if(SymTab.presentMethod.functionName.equals("MAIN")){
            //Need to fill action later
        }
        else {
            SymTab.presentMethod.stackSlotCount+=8;
        }

        SymTab.presentMethod.tRegisterBase = SymTab.presentMethod.stackSlotCount;

        if(SymTab.presentMethod.maximumParamCount < 0){
            //Need to fill action later
        }
        else {
            SymTab.presentMethod.stackSlotCount +=10;
        }

        SymTab.presentMethod.stackVarBase = SymTab.presentMethod.stackSlotCount;
        
        SymTab.presentMethod.stackVarMap.replaceAll((k, v) -> Integer.toString(Integer.parseInt(v) + SymTab.presentMethod.stackVarBase));
        
        
        int iter2 = 0;
        while(iter2 < Integer.parseInt(SymTab.presentMethod.paramCount) - 4){
            SymTab.presentMethod.stackVarMap.put("TEMP"+Integer.toString(iter2+4), Integer.toString(iter2));
            iter2++;
        }

        SymTab.presentMethod.stackSlotCount = SymTab.presentMethod.stackSlotCount + SymTab.presentMethod.stackCount;    

        SymTab.presentMethod.stackVarCount++; 
        n.f3.accept(this,(A)"First Pass");

        SymTab.presentMethod.tempVarCount++; 
        n.f4.accept(this,(A)"First Pass");

        SymTab.presentMethod.stackVarCount = 0;
        SymTab.presentMethod.tempVarCount = 0; 
        n.f0.accept(this,(A)"Second Pass");

        SymTab.presentMethod = SymTab.methodMap.get("MAIN");

        String str = "MAIN [" + SymTab.presentMethod.paramCount + "] [";
        str = str + SymTab.presentMethod.stackSlotCount + "] [";
        str += SymTab.presentMethod.maximumParamCount + "] ";
        gen(str);

        SymTab.presentMethod.stackVarCount++; 
        n.f1.accept(this,(A)"Second Pass");
        
        SymTab.presentMethod.tempVarCount++;
        n.f2.accept(this,(A)"Second Pass");

        gen("END");
        if(SymTab.presentMethod.isPresStack)
            gen("//SPILLED");
        else 
            gen("//NOTSPILLED");

        SymTab.presentMethod.stackVarCount = 0;
        SymTab.presentMethod.tempVarCount = 0;

        SymTab.presentMethod.stackVarCount++;
        n.f3.accept(this,(A)"Second Pass");

        SymTab.presentMethod.tempVarCount++;
        n.f4.accept(this,(A)"Second Pass");

        SymTab.presentMethod.stackVarCount = 0;
        SymTab.presentMethod.tempVarCount = 0;
        
        return _ret;
    }

    /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
    public R visit(StmtList n, A argu) {
        R _ret=null;

        String label = "";

        if(n.f0.present() == true){

            int i = 0;
            while(i<n.f0.size()) {

                boolean isPres = true;

                isPres = ((NodeOptional)((NodeSequence)n.f0.elementAt(i)).elementAt(0)).present();

                label = "";

                if(isPres){

                    label = (String) ((NodeSequence)n.f0.elementAt(i)).elementAt(0).accept(this,argu);

                    if(argu.toString().equals("First Pass") && !(SymTab.presentMethod.labelMap.containsKey(label))) {

                        BlockTable node = new BlockTable();
                        node.label = label;
                        SymTab.presentMethod.labelMap.put(label, node);

                    }

                    SymTab.presentMethod.presentLabel = label;
                } 

                if(argu.toString().equals("Second Pass") && !label.equals("")){
                    gen(SymTab.presentMethod.functionName + label);
                }

                ((NodeSequence)n.f0.elementAt(i)).elementAt(1).accept(this,argu);

                i++;

                SymTab.presentMethod.presentLabel = null;

            }
        }
        return _ret;
    }

    /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
    public R visit(Procedure n, A argu) {
        R _ret=null;

        String fName = "";
        String argCnt = "";
        
        if(argu.toString() == "First Pass") { 

            fName = (String)n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            argCnt = (String)n.f2.accept(this, argu);
            n.f3.accept(this, argu);
            MethodTable mobj = new MethodTable();
            mobj.functionName = fName;
            mobj.paramCount = argCnt;
            SymTab.methodMap.put(fName, mobj);
            SymTab.presentMethod = SymTab.methodMap.get(fName);
            n.f4.accept(this, argu);

            BlockTable special = new BlockTable();

            int registersAllocated;

            if(4 > Integer.parseInt(SymTab.presentMethod.paramCount)) {
                registersAllocated = Integer.parseInt(SymTab.presentMethod.paramCount);
            }
            else {
                registersAllocated = 4;
            }

            int i = 0;

            while(i < registersAllocated){
                special.def_n.add("TEMP" + (i));
                i++;            
            }

            special.succ_n.add(SymTab.presentMethod.blockMap.get(1));
            SymTab.presentMethod.blockMap.put(0,special);

            int runNum = 0;

            boolean flag = true;

            while(flag){
                
                runNum++;  

                flag = false;

                for(Iterator itr = SymTab.presentMethod.blockMap.entrySet().iterator();itr.hasNext();){
                    Map.Entry pair = (Map.Entry) itr.next();
                    BlockTable node = (BlockTable) pair.getValue();
                    Integer key = (Integer) pair.getKey();

                    Iterator<String> itr1 = null;

                    node.inTemp_n = new ArrayList<String>();
                    node.outTemp_n= new ArrayList<String>();

                    itr1 = node.out_n.iterator();

                    for(;itr1.hasNext();){
                        node.outTemp_n.add(itr1.next());
                    }

                    itr1 = node.in_n.iterator();
                    for(;itr1.hasNext();){
                        node.inTemp_n.add(itr1.next());
                    }
                
                    ArrayList noDefs = new ArrayList<String>();

                    node.in_n =  new ArrayList<String>();

                    for(itr1 = node.use_n.iterator();itr1.hasNext();node.in_n.add(itr1.next()));
                                        
                    for(itr1 = node.out_n.iterator();itr1.hasNext();){

                        String tmp = itr1.next();
                        boolean isPres = node.def_n.contains(tmp);
                        if(isPres) {
                            //Need to fill this
                        }
                        else {
                            noDefs.add(tmp);
                        }
                    }

                
                    for(itr1 = noDefs.iterator();itr1.hasNext();){
                        String temp = itr1.next();
                        boolean isPres = node.in_n.contains(temp);
                        if(isPres) {
                            // Need to fill this
                        }
                        else {
                            node.in_n.add(temp);
                        }
                    }

                    node.out_n = new ArrayList<String>();

                    for(Iterator<BlockTable> itr0 = node.succ_n.iterator();itr0.hasNext();){

                        BlockTable child = itr0.next();

                        for(Iterator<String> itr2 = child.in_n.iterator(); itr2.hasNext(); node.out_n.add(itr2.next()));
                    }
                }

                for(Iterator itr = SymTab.presentMethod.blockMap.entrySet().iterator();itr.hasNext();){

                    Map.Entry pair = (Map.Entry) itr.next();
                    BlockTable node = (BlockTable) pair.getValue();
                    Integer key = (Integer) pair.getKey();

                    flag = (flag) || (!node.inTemp_n.equals(node.in_n) || !node.outTemp_n.equals(node.out_n));
                }
                        
            }

            int iter = 4;
        
            while(iter < Integer.parseInt(SymTab.presentMethod.paramCount)){
                String str = ("TEMP"+Integer.toString(iter));
                SymTab.presentMethod.tempList.remove(str);
                iter++;
            }

            for(Iterator<String> itr = SymTab.presentMethod.tempList.iterator(); itr.hasNext();){
                Pair active_range = new Pair();

                active_range.pairStr = (String) itr.next();

                active_range.fst = 0;

                while(active_range.fst < SymTab.presentMethod.blockCount){

                    boolean isPres = SymTab.presentMethod.blockMap.get(active_range.fst).def_n.contains(active_range.pairStr);
                    if(isPres) 
                        break;
                        active_range.fst++;
                }

                active_range.snd = SymTab.presentMethod.blockCount;
                active_range.snd--;

                while(active_range.snd >= 0){

                    boolean isPres = SymTab.presentMethod.blockMap.get(active_range.snd).in_n.contains(active_range.pairStr);
                
                    if(isPres)
                        break;

                        active_range.snd--;
                }

                if(active_range.snd < active_range.fst){
                    itr.remove();
                    continue;
                }

                SymTab.presentMethod.liveIntervalMap.put(active_range.pairStr, active_range);
            }

            ArrayList<Integer> activeRegisters = new ArrayList<Integer>();
            ArrayList<Pair> liveIntervals = new ArrayList<Pair>();
            ArrayList<Pair> activeIntervals = new ArrayList<Pair>();

            Vector<Boolean> freeRegPool = new Vector<Boolean>(18);

            int u = 0;
            while(u < 18) {
                freeRegPool.add(true);
                u++;
            }

            for(Iterator itr = SymTab.presentMethod.liveIntervalMap.entrySet().iterator();itr.hasNext();){

                int iter1 = 0;
                Map.Entry pair = (Map.Entry)itr.next();

                int pos=-1;
                Pair curr_range = (Pair)pair.getValue();


                for(Pair v : liveIntervals) {
                    if( curr_range.fst < liveIntervals.get(iter1).fst){
                        pos = iter1;
                        break;
                    }
                    iter1++;
                }

                boolean found = false;
                if(pos != -1) found = true;
                if(found) 
                    liveIntervals.add(pos,curr_range);
                else
                    liveIntervals.add(curr_range);
            }

            i = 0;
            while(i< liveIntervals.size()) {
                
                int j = 0;
                while(j < activeIntervals.size()) {

                    if(liveIntervals.get(i).fst > activeIntervals.get(j).snd){
                        freeRegPool.set(activeRegisters.get(j).intValue(), true);
                        activeRegisters.remove(j);
                        activeIntervals.remove(j);
                    }

                    else
                        break;

                    j++;
                }

                if(activeRegisters.size() == 18){

                    SymTab.presentMethod.isPresStack = true;
                    Pair spill = activeIntervals.get(activeIntervals.size()-1);

                    if(liveIntervals.get(i).snd >= spill.snd)
                        SymTab.presentMethod.stackVarMap.put(liveIntervals.get(i).pairStr,Integer.toString(SymTab.presentMethod.stackCount++));

                    else {

                        activeIntervals.remove(activeIntervals.size()-1);

                        int freshReg = activeRegisters.get(activeRegisters.size()-1);

                        activeRegisters.remove(activeRegisters.size()-1);
                    
                        SymTab.presentMethod.registerMap.put(liveIntervals.get(i).pairStr, SymTab.presentMethod.registerList.get(freshReg));

                        SymTab.presentMethod.registerMap.remove(spill.pairStr);

                        SymTab.presentMethod.stackVarMap.put(spill.pairStr, Integer.toString(SymTab.presentMethod.stackCount++));

                        int pos = -1;

                        int k = 0;

                        for(Pair v : activeIntervals) {
                            if(v.snd > liveIntervals.get(i).snd) {
                                pos = k;
                                break;
                            }
                            k++;
                        }

                        boolean isPres = true;
                        if(pos == -1) isPres = false;
                        
                        if(isPres){
                            activeIntervals.add(pos, liveIntervals.get(i));
                            activeRegisters.add(pos,Integer.valueOf(freshReg));
                        }
                        else{
                            activeIntervals.add(liveIntervals.get(i));
                            activeRegisters.add(Integer.valueOf(freshReg));
                        }
                    }
                    
                }
                else if(activeRegisters.size() != 18){

                    int freshReg = 18;

                    for(int k = 0; k < 18; ++k){
                        if(freeRegPool.get(k)){
                            freeRegPool.set(k, false);
                            freshReg = k;
                            break;
                        }
                    }

                    SymTab.presentMethod.registerMap.put(liveIntervals.get(i).pairStr,SymTab.presentMethod.registerList.get(freshReg));

                    int pos = -1;

                    int k = 0;

                    for(Pair v : activeIntervals) {
                        if(v.snd > liveIntervals.get(i).snd) {
                            pos = k;
                            break;
                        }
                        k++;
                    }

                    boolean flg = false;
                    if(pos + 1 != 0) flg = true;
                    if(flg){
                        activeIntervals.add(pos, liveIntervals.get(i));
                        activeRegisters.add(pos,Integer.valueOf(freshReg));
                    }
                    else{
                        activeIntervals.add(liveIntervals.get(i));
                        activeRegisters.add(Integer.valueOf(freshReg));
                    }
                }
                i++;
            }
            SymTab.presentMethod.stackSlotCount += Math.max(Integer.parseInt(SymTab.presentMethod.paramCount) - 4, 0);

        SymTab.presentMethod.sRegisterBase = SymTab.presentMethod.stackSlotCount;
        
        if(SymTab.presentMethod.functionName.equals("MAIN")){
            //Need to fill action later
        }
        else {
            SymTab.presentMethod.stackSlotCount+=8;
        }

        SymTab.presentMethod.tRegisterBase = SymTab.presentMethod.stackSlotCount;

        if(SymTab.presentMethod.maximumParamCount < 0){
            //Need to fill action later
        }
        else {
            SymTab.presentMethod.stackSlotCount +=10;
        }

        SymTab.presentMethod.stackVarBase = SymTab.presentMethod.stackSlotCount;
        
        SymTab.presentMethod.stackVarMap.replaceAll((k, v) -> Integer.toString(Integer.parseInt(v) + SymTab.presentMethod.stackVarBase));
        
        
        int iter2 = 0;
        while(iter2 < Integer.parseInt(SymTab.presentMethod.paramCount) - 4){
            SymTab.presentMethod.stackVarMap.put("TEMP"+Integer.toString(iter2+4), Integer.toString(iter2));
            iter2++;
        }

        SymTab.presentMethod.stackSlotCount = SymTab.presentMethod.stackSlotCount + SymTab.presentMethod.stackCount;    
            
        }

        else if(argu.toString() == "Second Pass") {
            fName = (String)n.f0.accept(this, argu);
            SymTab.presentMethod = SymTab.methodMap.get(fName);
            n.f1.accept(this, argu);
            String str = (String)n.f2.accept(this, argu);
            n.f3.accept(this, argu);

            argCnt = str;

            str = fName + " [" + argCnt + "] [";
            str += SymTab.presentMethod.stackSlotCount + "] ["+ SymTab.presentMethod.maximumParamCount + "] ";

            gen(str);
            
            n.f4.accept(this, argu);

            if(SymTab.presentMethod.isPresStack) 
                gen("//SPILLED");
            else 
                gen("//NOTSPILLED");

        }
        return _ret;
    }

    /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    */
    public R visit(Stmt n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "NOOP"
    */
    public R visit(NoOpStmt n, A argu) {
        R _ret=null;
        if(argu.toString().equals("First Pass")) {
            BlockTable node;
            if(SymTab.presentMethod.presentLabel == null){
                node = new BlockTable();
            }
            else {
                node = SymTab.presentMethod.labelMap.get(SymTab.presentMethod.presentLabel);
            }

            int num = SymTab.presentMethod.blockCount - 1;

            if(SymTab.presentMethod.blockMap.containsKey(num)){
                BlockTable parent = new BlockTable();
                parent = SymTab.presentMethod.blockMap.get(num);
                parent.succ_n.add(node);
            }

            SymTab.presentMethod.blockMap.put(num + 1,node);

            SymTab.presentMethod.blockCount++;

            int index = SymTab.presentMethod.blockCount-1;
        }
        if(argu.toString().equals("Second Pass")){gen("\tNOOP");}
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "ERROR"
    */
    public R visit(ErrorStmt n, A argu) {
        R _ret=null;
        if(argu.toString().equals("First Pass")){
            BlockTable node;
            if(SymTab.presentMethod.presentLabel == null){
                node = new BlockTable();
            }
            else {
                node = SymTab.presentMethod.labelMap.get(SymTab.presentMethod.presentLabel);
            }

            int num = SymTab.presentMethod.blockCount - 1;

            if(SymTab.presentMethod.blockMap.containsKey(num)){
                BlockTable parent = new BlockTable();
                parent = SymTab.presentMethod.blockMap.get(num);
                parent.succ_n.add(node);
            }

            SymTab.presentMethod.blockMap.put(num + 1,node);

            SymTab.presentMethod.blockCount++;

            int index = SymTab.presentMethod.blockCount-1;
        }
        if(argu.toString().equals("Second Pass")) {
            gen("\tERROR");
        }
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
    public R visit(CJumpStmt n, A argu) {
        R _ret=null;

        int index = 0;

        String label = "";
        String tmp = "";

        n.f0.accept(this,argu);

        if(argu.toString() == "First Pass") {

            BlockTable node;
            if(SymTab.presentMethod.presentLabel == null){
                node = new BlockTable();
            }
            else {
                node = SymTab.presentMethod.labelMap.get(SymTab.presentMethod.presentLabel);
            }

            int num = SymTab.presentMethod.blockCount - 1;

            if(SymTab.presentMethod.blockMap.containsKey(num)){
                BlockTable parent = new BlockTable();
                parent = SymTab.presentMethod.blockMap.get(num);
                parent.succ_n.add(node);
            }

            SymTab.presentMethod.blockMap.put(num + 1,node);

            SymTab.presentMethod.blockCount++;

            index = SymTab.presentMethod.blockCount-1;

            SymTab.presentMethod.presentBlock = SymTab.presentMethod.blockMap.get(index);   
            tmp = (String)n.f1.accept(this, argu);
            label = (String)n.f2.accept(this, argu);
            SymTab.presentMethod.presentBlock.use_n.add(tmp);

            if(SymTab.presentMethod.labelMap.containsKey(label)) {
                SymTab.presentMethod.presentLabel = label;
            }

            else {
                BlockTable node1 = new BlockTable();
                node1.label = label;
                SymTab.presentMethod.labelMap.put(label, node1);
            }

            SymTab.presentMethod.presentBlock.succ_n.add(SymTab.presentMethod.labelMap.get(label));
            SymTab.presentMethod.presentBlock = null;
        }
        
        else if (argu.toString() == "Second Pass") {

            tmp = (String) n.f1.accept(this,argu);
            label = (String) n.f2.accept(this,argu);

            boolean isPres = true;

            isPres = SymTab.presentMethod.registerMap.containsKey(tmp);
            
            if(!isPres) {
                gen("\tALOAD v1 SPILLEDARG " + SymTab.presentMethod.stackVarMap.get(tmp));
                gen("\tCJUMP v1 " + SymTab.presentMethod.functionName + label);
            }
            else
                gen("\tCJUMP " + SymTab.presentMethod.registerMap.get(tmp) + " " +SymTab.presentMethod.functionName + label + " " );
        }
        return _ret;
    }

    /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
    public R visit(JumpStmt n, A argu) {
        R _ret=null;

        int index = 0;
        String label = "";

        if(argu.toString().equals("First Pass")){
            
            BlockTable node;
            if(SymTab.presentMethod.presentLabel == null){
                node = new BlockTable();
            }
            else {
                node = SymTab.presentMethod.labelMap.get(SymTab.presentMethod.presentLabel);
            }

            int num = SymTab.presentMethod.blockCount - 1;

            if(SymTab.presentMethod.blockMap.containsKey(num)){
                BlockTable parent = new BlockTable();
                parent = SymTab.presentMethod.blockMap.get(num);
                parent.succ_n.add(node);
            }

            SymTab.presentMethod.blockMap.put(num + 1,node);

            SymTab.presentMethod.blockCount++;

            index = SymTab.presentMethod.blockCount-1;

            SymTab.presentMethod.presentBlock = SymTab.presentMethod.blockMap.get(index);
        }
        n.f0.accept(this, argu);
        label = (String)n.f1.accept(this, argu);

        if(argu.toString().equals("Second Pass")){
            gen("\tJUMP " + SymTab.presentMethod.functionName + label + " ");
        }

        else {

            boolean contains = SymTab.presentMethod.labelMap.containsKey(label);
            if(!contains) {
                BlockTable node = new BlockTable();
                node.label = label;
                SymTab.presentMethod.labelMap.put(label, node);
            }
            else 
                SymTab.presentMethod.presentLabel = label;
                
            SymTab.presentMethod.presentBlock.succ_n.add(SymTab.presentMethod.labelMap.get(label));
            SymTab.presentMethod.presentBlock = null;
        }
        return _ret;
    }

    /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
    public R visit(HStoreStmt n, A argu) {
        R _ret=null;
        String tmp1 ="";
        String tmp2 = "";
        String offset = "";
        if(argu.toString().equals("First Pass")){
            BlockTable node;
            if(SymTab.presentMethod.presentLabel == null){
                node = new BlockTable();
            }
            else {
                node = SymTab.presentMethod.labelMap.get(SymTab.presentMethod.presentLabel);
            }

            int num = SymTab.presentMethod.blockCount - 1;

            if(SymTab.presentMethod.blockMap.containsKey(num)){
                BlockTable parent = new BlockTable();
                parent = SymTab.presentMethod.blockMap.get(num);
                parent.succ_n.add(node);
            }

            SymTab.presentMethod.blockMap.put(num + 1,node);

            SymTab.presentMethod.blockCount++;

            int idx = SymTab.presentMethod.blockCount-1;
            SymTab.presentMethod.presentBlock = SymTab.presentMethod.blockMap.get(idx);
        }

        n.f0.accept(this, argu);
        tmp1 = (String)  n.f1.accept(this, argu);
        offset = (String)n.f2.accept(this, argu);
        tmp2 = (String)  n.f3.accept(this, argu);
        
        if(argu.toString().equals("First Pass")){
            SymTab.presentMethod.presentBlock.use_n.add(tmp1);
            SymTab.presentMethod.presentBlock.use_n.add(tmp2);
            SymTab.presentMethod.presentBlock = null;
        }

        if(argu.toString().equals("Second Pass")){
            
            if(SymTab.presentMethod.registerMap.containsKey(tmp1)){
                if(SymTab.presentMethod.registerMap.containsKey(tmp2)){
                    
                    String str = "\tHSTORE " + SymTab.presentMethod.registerMap.get(tmp1) + " ";
                    str += offset + " " + SymTab.presentMethod.registerMap.get(tmp2) + " ";
                    gen(str);
                }
                else{
                    gen("\tALOAD v1  " + SymTab.presentMethod.stackVarMap.get(tmp2));
                    gen("\tHSTORE " + SymTab.presentMethod.registerMap.get(tmp1) + " " + offset + " v1 ");
                }
            }else{
                if(SymTab.presentMethod.registerMap.containsKey(tmp2)){
                    gen("\tALOAD v1  " + SymTab.presentMethod.stackVarMap.get(tmp1));
                    gen("\tHSTORE v1 " + offset + " " + SymTab.presentMethod.registerMap.get(tmp2) + " ");
                }
                else{
                    gen("\tALOAD v0  " + SymTab.presentMethod.stackVarMap.get(tmp1));
                    gen("\tALOAD v1  " + SymTab.presentMethod.stackVarMap.get(tmp2));
                    gen("\tHSTORE v0 " + offset + " v1 ");
                }
            }

        }
        return _ret;
    }

    /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
    public R visit(HLoadStmt n, A argu) {
        R _ret=null;
        String tmp1 ="";
        String tmp2 = "";
        String offset = "";
        if(argu.toString().equals("First Pass")){
            BlockTable node;
            if(SymTab.presentMethod.presentLabel == null){
                node = new BlockTable();
            }
            else {
                node = SymTab.presentMethod.labelMap.get(SymTab.presentMethod.presentLabel);
            }

            int num = SymTab.presentMethod.blockCount - 1;

            if(SymTab.presentMethod.blockMap.containsKey(num)){
                BlockTable parent = new BlockTable();
                parent = SymTab.presentMethod.blockMap.get(num);
                parent.succ_n.add(node);
            }

            SymTab.presentMethod.blockMap.put(num + 1,node);

            SymTab.presentMethod.blockCount++;

            int index = SymTab.presentMethod.blockCount-1;

            SymTab.presentMethod.presentBlock = SymTab.presentMethod.blockMap.get(index);
        }

        n.f0.accept(this, argu);
        tmp1 = (String)n.f1.accept(this, argu);
        tmp2 = (String)n.f2.accept(this, argu);
        offset= (String)n.f3.accept(this, argu);

        if(argu.toString().equals("First Pass")){
            SymTab.presentMethod.presentBlock.def_n.add(tmp1);
            SymTab.presentMethod.presentBlock.use_n.add(tmp2);
            SymTab.presentMethod.presentBlock = null;
        }

        if(argu.toString().equals("Second Pass")){
            boolean flg1 = SymTab.presentMethod.registerMap.containsKey(tmp1);
            boolean flg2 = SymTab.presentMethod.registerMap.containsKey(tmp2);
            boolean flgSpill = SymTab.presentMethod.stackVarMap.containsKey(tmp1);

            if(flg1){

                if(!flg2){
                    gen("\tALOAD v1  " + SymTab.presentMethod.stackVarMap.get(tmp2) + " ");
                    gen("\tHLOAD " + SymTab.presentMethod.registerMap.get(tmp1)  + " v1 " + offset+ " ");
                }

                else{
                    String str = "\tHLOAD " + SymTab.presentMethod.registerMap.get(tmp1) + " " + SymTab.presentMethod.registerMap.get(tmp2);
                    str += " "+ offset + " ";
                    gen(str);
                }
            }

            else if(flgSpill){

                if(!flg2){
                    
                    gen("\tALOAD v0  " + SymTab.presentMethod.stackVarMap.get(tmp2) + " ");
                    gen("\tHLOAD v1 " + " v0 " + offset+" ");
                    gen("\tASTORE SPILLEDARG " + SymTab.presentMethod.stackVarMap.get(tmp1) + " v1 ");

                }
                else{
                    
                    gen("\tHLOAD v1 " + SymTab.presentMethod.registerMap.get(tmp2) + " "+ offset + " " );
                    gen("\tASTORE SPILLEDARG " + SymTab.presentMethod.stackVarMap.get(tmp1) + " v1 ");

                }
            }
        }

        return _ret;
    }

    /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
    public R visit(MoveStmt n, A argu) {
        R _ret=null;
        String tmp1 = "";
        String expr = "";
        
        if(argu.toString().equals("First Pass")){
            BlockTable node;
            if(SymTab.presentMethod.presentLabel == null){
                node = new BlockTable();
            }
            else {
                node = SymTab.presentMethod.labelMap.get(SymTab.presentMethod.presentLabel);
            }

            int num = SymTab.presentMethod.blockCount - 1;

            if(SymTab.presentMethod.blockMap.containsKey(num)){
                BlockTable parent = new BlockTable();
                parent = SymTab.presentMethod.blockMap.get(num);
                parent.succ_n.add(node);
            }

            SymTab.presentMethod.blockMap.put(num + 1,node);

            SymTab.presentMethod.blockCount++;

            int index = SymTab.presentMethod.blockCount-1;
            SymTab.presentMethod.presentBlock = SymTab.presentMethod.blockMap.get(index);
        }
        
        n.f0.accept(this, argu);
        tmp1 = (String)n.f1.accept(this, argu);
        expr = (String)n.f2.accept(this, argu);

        if(argu.toString().equals("First Pass")){
            SymTab.presentMethod.presentBlock.def_n.add(tmp1);
            SymTab.presentMethod.presentBlock = null;
        }

        if(argu.toString().equals("Second Pass")){
            if(SymTab.presentMethod.registerMap.containsKey(tmp1))
                gen("\tMOVE " + SymTab.presentMethod.registerMap.get(tmp1) + " " + expr + "\n");

            else if(SymTab.presentMethod.stackVarMap.containsKey(tmp1)) {
                gen("\tMOVE v1 "+ expr + "\n" + "\tASTORE SPILLEDARG "); 
                gen(SymTab.presentMethod.stackVarMap.get(tmp1) + " v1 ");
            }                

        }

        return _ret;
    }

    /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
    public R visit(PrintStmt n, A argu) {
        R _ret=null;

        String inst0 = "";
        String inst1 = "";
        if(argu.toString().equals("First Pass")){
            BlockTable node;
            if(SymTab.presentMethod.presentLabel == null){
                node = new BlockTable();
            }
            else {
                node = SymTab.presentMethod.labelMap.get(SymTab.presentMethod.presentLabel);
            }

            int num = SymTab.presentMethod.blockCount - 1;

            if(SymTab.presentMethod.blockMap.containsKey(num)){
                BlockTable parent = new BlockTable();
                parent = SymTab.presentMethod.blockMap.get(num);
                parent.succ_n.add(node);
            }

            SymTab.presentMethod.blockMap.put(num + 1,node);

            SymTab.presentMethod.blockCount++;

            int index = SymTab.presentMethod.blockCount-1;
            SymTab.presentMethod.presentBlock = SymTab.presentMethod.blockMap.get(index);
        }

        inst0 = (String)n.f0.accept(this, argu);
        inst1 = (String)n.f1.accept(this, argu);

        if(argu.toString().equals("First Pass")){
            SymTab.presentMethod.presentBlock = null;
        }

        if(argu.toString().equals("Second Pass")){
            if(SymTab.presentMethod.registerMap.containsKey(inst1)){
                gen("\t" + inst0 + " " + SymTab.presentMethod.registerMap.get(inst1) + " ");
            }
            else{
                gen("\tALOAD v1 SPILLEDARG " + SymTab.presentMethod.stackVarMap.get(inst1)+ " " + "\t" + inst0 + " v1 ");
            }
        }

        return null;
    }

    /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
    public R visit(Exp n, A argu) {
        R _ret = n.f0.accept(this, argu);

        if(n.f0.which == 3){
            
            String str = (String) _ret;

            boolean startsWithTemp = false;

            startsWithTemp = str.startsWith("TEMP");

            if(!startsWithTemp){

                if(argu.toString().equals("Second Pass")){

                    if(!isInteger(str)) {
                        _ret = (R)str;
                    }
                    else {
                        int x = Integer.parseInt(str);
                        gen("\tMOVE v1 "+ str);
                        _ret = (R)("v1");
                    }
                }

            }
            else{
                
                boolean isContains = true;

                isContains = !(SymTab.presentMethod.registerMap.containsKey(str));
            
                if(isContains){
                    if(argu.toString().equals("Second Pass")){
                        gen("\tALOAD v0 SPILLEDARG " + SymTab.presentMethod.stackVarMap.get(str));
                        _ret = (R)("v0");
                    }
                    else {
                        //No dothing
                    }
                }

                else{
                    _ret = (R) SymTab.presentMethod.registerMap.get(str);
                        
                }
            }
        }

        return _ret;
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
    public R visit(StmtExp n, A argu) {
        R _ret=null;
        String rval = "";
        
        if(argu.toString() == "First Pass") {    
            n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            n.f2.accept(this, argu);

            BlockTable node;
            if(SymTab.presentMethod.presentLabel == null){
                node = new BlockTable();
            }
            else {
                node = SymTab.presentMethod.labelMap.get(SymTab.presentMethod.presentLabel);
            }

            int num = SymTab.presentMethod.blockCount - 1;

            if(SymTab.presentMethod.blockMap.containsKey(num)){
                BlockTable parent = new BlockTable();
                parent = SymTab.presentMethod.blockMap.get(num);
                parent.succ_n.add(node);
            }

            SymTab.presentMethod.blockMap.put(num + 1,node);

            SymTab.presentMethod.blockCount++;

            int addr = SymTab.presentMethod.blockCount-1;

            SymTab.presentMethod.presentBlock = SymTab.presentMethod.blockMap.get(addr);

            SymTab.presentMethod.stackVarCount++;
            n.f3.accept(this, argu);

            SymTab.presentMethod.tempVarCount++;
            n.f4.accept(this, argu);

            SymTab.presentMethod.stackVarCount = 0;
            SymTab.presentMethod.tempVarCount = 0;
            

            SymTab.presentMethod.presentBlock = null;
        }
            
        else if(argu.toString() == "Second Pass") {
            n.f0.accept(this, argu);

            int i = 0;
            while(i <= 7){
                gen("\tASTORE SPILLEDARG "+(SymTab.presentMethod.sRegisterBase + i) +" s" + i);
                i++;
            }

            i = 0;

            int num;

            if(Integer.parseInt(SymTab.presentMethod.paramCount) < 4) {
                num = Integer.parseInt(SymTab.presentMethod.paramCount);
            }

            else {
                num = 4;
            }

            while(i < num){
                if(SymTab.presentMethod.registerMap.containsKey("TEMP" + Integer.toString(i))){
                    gen("MOVE " + SymTab.presentMethod.registerMap.get("TEMP" + Integer.toString(i)) + " a" + i);
                }
                i++;
            }

            SymTab.presentMethod.stackVarCount++;
            n.f1.accept(this, argu);

            SymTab.presentMethod.tempVarCount++;
            n.f2.accept(this, argu);

            rval = (String)n.f3.accept(this, argu);

            boolean startsWithTemp = rval.startsWith("TEMP");

            boolean containsKey = SymTab.presentMethod.registerMap.containsKey(rval);



            if(!startsWithTemp)
                gen("\tMOVE v0 " + rval);
            
            else{
                if(!containsKey)
                    gen("\tALOAD v1 SPILLEDARG "+  SymTab.presentMethod.stackVarMap.get(rval) + "\tMOVE v0 v1 ");
                else
                    gen("\tMOVE v0 " + SymTab.presentMethod.registerMap.get(rval));
            }

            n.f4.accept(this, argu);
            
            i = 0;
            while(i < 9){
                if(i < 8) {
                    gen("\tALOAD s" + (i) +" SPILLEDARG " + (SymTab.presentMethod.sRegisterBase +i));
                }
                else {
                    gen("END");
                }
                i++;
            }
        }

        return _ret;
    }

    /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
    public R visit(Call n, A argu) {
        R _ret=null;
        
        if(argu.toString().equals("Second Pass")){
            
            int i = 0;
            while(i<=9){
                gen("\tASTORE SPILLEDARG " + (SymTab.presentMethod.tRegisterBase + i) + " t" + i);
                i++;
            }
        }
        
        String inst0 = (String)n.f0.accept(this, argu);
        String inst1 = (String)n.f1.accept(this, argu);
        String tmp = "";
        
        n.f2.accept(this, argu);
        
        if(n.f3.present()){
            if(argu.toString().equals("First Pass")){
                if(SymTab.presentMethod.maximumParamCount < n.f3.size()) {
                    SymTab.presentMethod.maximumParamCount = n.f3.size();
                }
            }
            
            int i = 0;

            while(i<n.f3.size()){

                tmp = (String)((Node)n.f3.elementAt(i)).accept(this,argu);

                if(argu.toString().equals("Second Pass")){
                    if(i >= 4){
                        if(SymTab.presentMethod.registerMap.containsKey(tmp)){
                            gen("\tPASSARG "+ (i-3) + " "+ SymTab.presentMethod.registerMap.get(tmp) + " ");
                        }
                        else{
                            gen("\tALOAD v1 SPILLEDARG "+ SymTab.presentMethod.stackVarMap.get(tmp) + " ");
                            gen("\tPASSARG "+ (i-3) + " v1 ");
                        }
                    }
                    else{
                        if(SymTab.presentMethod.registerMap.containsKey(tmp)){
                            gen("\tMOVE a" + i + " " + SymTab.presentMethod.registerMap.get(tmp) + " ");
                        }
                        else{
                            gen("\tALOAD v1 SPILLEDARG " + SymTab.presentMethod.stackVarMap.get(tmp) + " ");
                            gen("\tMOVE a"+i +" v1 " );
                        }
                    }
                }

                if(argu.toString().equals("First Pass")){
                    SymTab.presentMethod.presentBlock.use_n.add(tmp);
                }

                i++;
            }
        }

        if(argu.toString().equals("Second Pass")){

            if(inst1.startsWith("TEMP")){
                if(SymTab.presentMethod.registerMap.containsKey(inst1)){
                    gen("\tCALL "  + SymTab.presentMethod.registerMap.get(inst1) + " ");
                }
                else{
                    gen("\tALOAD v1 SPILLEDARG "+SymTab.presentMethod.stackVarMap.get(inst1) + " ");
                    gen("\tCALL v1 ");
                }
            }

            else
                gen("\tCALL "+inst1);

            int i = 0;

            while(i<=9){
                gen("\tALOAD t" + i + " SPILLEDARG " + (i+ SymTab.presentMethod.tRegisterBase) + " ");
                i++;
            }
        }

        n.f4.accept(this, argu);
        return (R)("v0");
    }

    /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
    public R visit(HAllocate n, A argu) {
        R _ret=null;
        String inst0 = (String)n.f0.accept(this, argu);
        String inst1 = (String)n.f1.accept(this, argu);
        
        if(argu.toString().equals("Second Pass")){

            boolean startsWithTemp = inst1.startsWith("TEMP");

            if(!startsWithTemp)
                _ret = (R) (inst0 + " " + inst1);

            else{

                boolean containsKey = true;

                containsKey = SymTab.presentMethod.registerMap.containsKey(inst1);

                if(!containsKey){
                    gen("\tALOAD v0 SPILLEDARG " + SymTab.presentMethod.stackVarMap.get(inst1));
                    gen("\tMOVE v0 PLUS v0 8 ");
                    _ret = (R) "HALLOCATE v0 ";
                }
                
                else{
                    gen("\tMOVE v0 PLUS " +SymTab.presentMethod.registerMap.get(inst1)+ " 8 " );
                    _ret = (R) "HALLOCATE v0 ";
                    
                }
            }
            
        }
        return _ret;
    }

    /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
    public R visit(BinOp n, A argu) {
        R _ret=null;
        String inst0 = (String)n.f0.accept(this, argu);
        String inst1 = (String)n.f1.accept(this, argu);
        String inst2 = (String)n.f2.accept(this, argu);
        
        if(argu.toString().equals("First Pass")){ 
            SymTab.presentMethod.presentBlock.use_n.add(inst1);
        }

        if(argu.toString().equals("Second Pass")){

            boolean startsWithTemp = true;

            startsWithTemp = inst2.startsWith("TEMP");

            boolean flg1 = SymTab.presentMethod.registerMap.containsKey(inst1);
            boolean flg2 = SymTab.presentMethod.registerMap.containsKey(inst2);

            if(flg1){

                if(!startsWithTemp){
                    _ret = (R)(inst0 + " " + SymTab.presentMethod.registerMap.get(inst1) + " " + inst2 + " ");
                }
                else{   

                    String str1 = SymTab.presentMethod.registerMap.get(inst1);
                    String str2 = SymTab.presentMethod.registerMap.get(inst2);
                    if(!flg2){
                        gen("\tALOAD v1 SPILLEDARG ");
                        gen(SymTab.presentMethod.stackVarMap.get(inst2));
                        _ret = (R)(inst0 + " " + str1 + " v1 ");
                    }
                    else{
                        _ret = (R)(inst0 + " " + str1 + " " + str2 + " ");
                    }
                }
            }

            else{

                String str1 =  SymTab.presentMethod.stackVarMap.get(inst1);
                String str2 =  SymTab.presentMethod.stackVarMap.get(inst2);

                if(!startsWithTemp){
                    gen("\tALOAD v1 SPILLEDARG "+  SymTab.presentMethod.stackVarMap.get(inst1));
                    _ret = (R)(inst0 + " v1 " +inst2 + " ");
                }
                else{
                    if(!flg2){
                        gen("\tALOAD v0 SPILLEDARG " + str1);
                        gen("\tALOAD v1 SPILLEDARG " + str2);
                        _ret = (R)(inst0 + " v0 v1 ");
                    }

                    else{
                        gen("\tALOAD v1 SPILLEDARG "+  str1);
                        _ret = (R)(inst0 + " v1 " + str2 + " ");
                    }
                }
            }
        }
        return _ret;
    }

    /**
    * f0 -> "LE"
    *       | "NE"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    *       | "DIV"
    */
    public R visit(Operator n, A argu) {
        R _ret=null;
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
    public R visit(SimpleExp n, A argu) {
        R _ret=null;
        String tmp = (String) n.f0.accept(this, argu);
        if((n.f0.which == 0) && (argu.toString().equals("First Pass"))) {
            SymTab.presentMethod.presentBlock.use_n.add(tmp);
        }
        return (R) tmp;
    }

    /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
    public R visit(Temp n, A argu) {
        R _ret=null;
        
        String tmp = (String)n.f0.accept(this, argu);
        String val = (String)n.f1.accept(this, argu);

        if(argu.toString().equals("First Pass") && !SymTab.presentMethod.tempList.contains(tmp+val)){
            SymTab.presentMethod.tempList.add(tmp+val);
        }
        return (R)(tmp + val);
    }

    /**
    * f0 -> <INTEGER_LITERAL>
    */
    public R visit(IntegerLiteral n, A argu) {
        R _ret = n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    public R visit(Label n, A argu) {
        R _ret = n.f0.accept(this, argu);
        return _ret;
    }

}
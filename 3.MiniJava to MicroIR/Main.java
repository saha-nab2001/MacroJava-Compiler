import syntaxtree.*;
import visitor.*;
import SymbolTables.*;

public class Main {
   public static void main(String [] args) {
      try {
         Node root = new MiniJavaParser(System.in).Goal();
         GlobalSymbolTable g = new GlobalSymbolTable();
         FirstPassST fp = new FirstPassST();
         root.accept(fp,g);
         g.createObjectAllocation();
         //g.print();
         SecondPass sp = new SecondPass(g);
         root.accept(sp,null);
      }
      catch (ParseException e) {
            PrintError.displayMessage(e.toString());
      }
   }
}
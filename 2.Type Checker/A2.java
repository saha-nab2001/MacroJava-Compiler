import syntaxtree.*;
import visitor.*;

public class A2 {
   public static void main(String [] args) {
      try {
         Node root = new MiniJavaParser(System.in).Goal();

         SymbolTableGenerator v = new SymbolTableGenerator();

         root.accept(v, null); //You assignment part is invoked here.

         //v.table.printClassDefs();

         TypeChecker t = new TypeChecker();

         root.accept(t, v.table);

         System.out.println("Program type checked successfully");

         //root.accept(new GJNoArguDepthFirst()); // Your assignment part is invoked here.
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 


package SymbolTables;

public class Attribute 
{
    public String msg;
    public String lblFalse;
    public String lblTrue;
    public int tempAddr;
    public String lblEnd;
    public String id;
    public Attribute()
    {
        msg = "";
        lblTrue = "";
        lblFalse = "";
        lblEnd = "";
        id = "";
        tempAddr = -1;
    }
    public Attribute(Attribute a)
    {
        this.msg = a.msg;
        this.lblTrue = a.lblTrue;
        this.lblFalse = a.lblFalse;
        this.lblEnd = a.lblEnd;
        this.tempAddr = a.tempAddr;
    }
}

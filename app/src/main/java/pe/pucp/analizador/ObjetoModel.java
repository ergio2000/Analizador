package pe.pucp.analizador;

public class ObjetoModel {
    //propiedades
    public String name;

    public int left;
    public int top;
    public int width;
    public int height;


    //constructor
    public ObjetoModel(String pName)
    {
        name=pName;

        setRectangle(0,0,0,0);
    }

    //metodos
    public void setRectangle(int pLeft, int pTop, int pWidth, int pHeight)
    {
        left=pLeft;
        top=pTop;
        width=pWidth;
        height=pHeight;
    }

    public String toString()
    {
        String mr="";
        mr=  name + ":(" + String.valueOf(left) + "," + String.valueOf(top) + ")" + " - " + "(" + String.valueOf(width) + "," + String.valueOf(height) + ")";

        return  mr;
    }
}

package pe.pucp.analizador;

public class CaraModel {
    //propiedades
    public double age;
    public String gender;

    public String smile; //SI/NO
    public String glasses;
    public String emotion;


    public int left;
    public int top;
    public int width;
    public int height;


    //constructor
    public CaraModel()
    {
        age=0;
        gender="";

        smile="";
        glasses="";
        emotion="";

        setRectangle(0,0,0,0);
    }

    public CaraModel(double pAge, String pGender)
    {
        age=pAge;
        gender=pGender;

        smile="";
        glasses="";
        emotion="";

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
        //mr= "edad:"+String.valueOf(age) + "-genero:" + gender + "-sge("+smile+"-"+glasses+"-"+emotion+")" + ":(" + String.valueOf(left) + "," + String.valueOf(top) + ")" + " - " + "(" + String.valueOf(width) + "," + String.valueOf(height) + ")";
        mr= "edad:"+String.valueOf(age) + "-genero:" + gender + "-sge("+smile+"-"+glasses+"-"+emotion+")";

        return  mr;
    }

    public String Elementos()
    {
        String mr="";
        mr= "(" + String.valueOf(age) + " años, " + gender + ", "+smile+", "+glasses+", "+emotion+")";

        return  mr;
    }

}


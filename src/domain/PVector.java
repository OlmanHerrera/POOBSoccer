package src.domain;
public class PVector{
    float x;
    float y;

    public PVector(float x_, float y_)
    {
        this.x = x_;
        this.y = y_;
    }
    
    public void add(PVector v)
    {
        x = x + v.x;
        y = y + v.y;
    }
    
    public void sub(PVector v)
    {
        
        x= x - v.x;
        y= y - v.y;
    }


    public void mult(float n)
    {
        x = x*n;
        y = y*n;
    }
    
    public void div(float n)
    {
        x = x/n;
        y = y/n;
    }

    public float mag()
    {
        return (float) Math.sqrt(x*x+y*y);
    }


    public void normalize()
    //
    {
        float m = mag();
        if (m!= 0){
            div(m);   
        }

    }
    
    public void limit(float max){
        if (mag() > max){
            normalize();
            mult(max);
        }
        
    }
    
    public PVector get(){
        return new PVector(x,y);
    }
    

}

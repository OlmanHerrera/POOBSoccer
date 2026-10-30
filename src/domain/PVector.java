package stc.domain;
public class PVector{
    double x;
    double y;

    public PVector(double x, double y){
        this.x = x;
        this.y = y;
    }
    public void add(Pvector b){
        this.x += b.x;
        this.y += b.y;
    }

    public void mul(int c){
        this.x = x*c; 
        this.y = y*c;
    }

    public void mul(float f){
        this.x = x*f;
        this.y = y*f;
    }

    public float mag(){
        return Math.sqrt(x*x+ y*y);
    }

    public void normalize(){
        float mag = mag();
        mul(mag);

    }
}
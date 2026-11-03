package src.domain;
import java.awt.Graphics;
public interface Entity {
    void update();
    void display(Graphics g);
    void checkEdges();
}

package trash;

public class Trash {
    public float x, y, speed;
    public int colorIdx;
    public float rot;

    public Trash(float x, float speed, int colorIdx) {
        this.x = x;
        this.y = -40;
        this.speed = speed;
        this.colorIdx = colorIdx;
        this.rot = (float) (Math.random() * 360);
    }
}

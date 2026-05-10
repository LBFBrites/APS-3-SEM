package factory;

public class Issue {
    public static final int SMOKE = 0;
    public static final int OIL = 1;
    public static final int NUKE = 2;

    public int type;
    public int factoryIdx;
    public float timeLeft = 100;
    public float x, y;

    public Issue(int type, int factoryIdx, float x, float y) {
        this.type = type;
        this.factoryIdx = factoryIdx;
        this.x = x;
        this.y = y;
    }
}

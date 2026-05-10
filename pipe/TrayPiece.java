package pipe;

public class TrayPiece {
    public int pipeType;
    public int targetGridIdx;
    public int x, y;
    public boolean placed = false;

    public TrayPiece(int pt, int tgi) {
        pipeType = pt;
        targetGridIdx = tgi;
    }
}

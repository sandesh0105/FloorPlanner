
interface Command{
    void execute();
    void undo();
}
public class RoomCommand implements Command{
    private Canvas canvas;
    private Room room;

    public RoomCommand(Canvas canvas, Room room){
        this.canvas = canvas;
        this.room = room;
    }

    public void execute(){
        canvas.addRoom(room);
    }
    public void undo(){
        canvas.removeRoom(room);
    }
}

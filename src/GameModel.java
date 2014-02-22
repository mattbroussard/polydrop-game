import java.util.ArrayList;

public class GameModel {
	
//	ArrayList<Block> blockList = new ArrayList<Block>();
	Platform p;
//	double x = 50, y = 50, w = 50, h = 10;
	
	
	
	public GameModel() {
		p = new Platform(50,50,50,10);
		
		//do something
		
	}
	public Platform getPlatform(){
		return p;
	}
	

}
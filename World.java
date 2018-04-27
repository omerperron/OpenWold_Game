package world;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import Camera.MasterCamera;
import buildings.Building;
import buildings.Storage;
import entities.Entity;
import guis.GuiHandler;
import mousepicker.MouseHandler;
import mousepicker.MousePicker;
import player.PlayerEntity;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;
import resource.Resource;
import terrain.MasterTerrain;
import terrain.Terrain;
import toolbox.Maths;

public class World {
	private MasterTerrain masterTerrain; //handles all terrains
	public Terrain currentTerrain; //terrain our camera is currently on
	private final static float GRAVITY = -1f;
	
	private ArrayList<PlayerEntity> players; //all player entities on screen
	private MasterCamera masterCamera; //handles player and aerial camera
	//private MousePicker picker; //handles mouse picking
	
	
	public static MasterRenderer renderer; //handles all rendering and processing
	private GuiHandler guiHandler; //handles all gui information
	

	private PlayerEntity selectedPlayer = null; //pointer to currently selected player if any
	
	private static float currentTime = 0;
	private float lastPress = 0;

	Vector3f fc = new Vector3f(); //center of the far plane in the projection matrix, this is used 
								  //for frustrum culling
	Vector3f[] normals = new Vector3f[5]; //normals of the planes in the projection matrix pointing inwards

	int count = 0;
	private float frameRate= 0;
	Entity curHovering = null;
	float entCount = 0; //just for tests
	
	ArrayList<Entity> entitiesOnScreen = new ArrayList<Entity>();
	ArrayList<Terrain> terrainsOnScreen = new ArrayList<Terrain>();

	
	public World(MasterTerrain masterTerrain, MasterCamera masterCamera, 
			ArrayList<PlayerEntity>players,	MasterRenderer masterRenderer, GuiHandler guiHandler){
		this.masterTerrain = masterTerrain;
		this.masterCamera = masterCamera;
		this.players = players;
		this.guiHandler = guiHandler;
		
		renderer = masterRenderer;

		
		MouseHandler.init(new MousePicker(masterCamera, renderer.getProjectionMatrix(), 
				masterTerrain.getTerrain(masterCamera.getCurrentCamera().getPosition())));

	}

	public void update(){
		currentTime += DisplayManager.getFrameTimeSeconds();
		frustrumCull();
		MouseHandler.update();
		

		
		guiHandler.update();

//		count++;
//		if(frameRate + 1 <= currentTime){
//			frameRate = currentTime;
//			System.out.println("terrains: " + terrainsOnScreen.size() + " entities: " + entCount +
//					" frameRate: " + count + "  " + Maths.getVector3fDistance(masterCamera.getPosition(), new Vector3f(0, 0, 0)));
//
//			//System.out.println(count);
//			count = 0;
//		}

		currentTerrain = masterTerrain.getTerrain(masterCamera.getPosition());


		for(PlayerEntity player: players){
			player.update(masterTerrain, renderer);
		}
		masterCamera.update(currentTerrain);
		

		updateCurHovering(); 
		checkClick();
		checkInput();

		cleanUp();


	}
	
	public void cleanUp(){
		terrainsOnScreen.clear();
    	entitiesOnScreen.clear();
		entCount = 0;
	}
	
	public void updateCurHovering(){
		
		Entity hovering = MouseHandler.getCurrentEntity(entitiesOnScreen);
		if(hovering != curHovering){
			if(curHovering != null) curHovering.setIsHovered(false);
			
			if(selectedPlayer != null){
				guiHandler.setPlayerDisplay(selectedPlayer);
			}else{
				guiHandler.changeDisplay(null);
			}
			curHovering = hovering;
			if(curHovering != null) curHovering.setIsHovered(true);
			if (curHovering instanceof Resource) {
				guiHandler.setResourceDisplay((Resource) curHovering);
			}
		}


	}
	
	public void checkClick(){

		if(MouseHandler.isLeftButtonDragged() && masterCamera.isAerialMode()){
			masterCamera.getCurrentCamera().calculatePitch(currentTerrain);
		}
		
		boolean right;
		if(MouseHandler.isRightButtonPressed()) right = true;
		else if(MouseHandler.isLeftButtonPressed()) right = false;
		else return;
		
		if(curHovering instanceof Resource) {
	    	handleResourcePressed((Resource) curHovering, right);
	    	
	    }else if(curHovering instanceof Building){
	    	handleBuildingPressed((Building) curHovering, right );
	    	
	    }else if(curHovering instanceof PlayerEntity){
	    	handlePlayerPressed((PlayerEntity) curHovering, right );
	    	
	    }else if(curHovering instanceof Entity){
	    	System.out.println("clicked on an entity");
	    	
	    }else{
	    	handleLandPressed(right);
	    }

		

	}
	
	public void handleResourcePressed(Resource resource, boolean right){
		
		if(right){
			if(selectedPlayer != null){
				selectedPlayer.setInteracting(resource, Resource.HARVEST);
			}   
		}
			
	}
	
	public void handleBuildingPressed(Building building, boolean right){
		
		if(right){
			if(building instanceof Storage){
				if(selectedPlayer != null){
					selectedPlayer.setInteracting(building, Storage.OFFLOAD_INVENTORY);
				}
			}
		}
	}
	
	public void handlePlayerPressed(PlayerEntity player, boolean right){
		
		if(right){
			if(selectedPlayer == player){
				masterCamera.setPlayerMode(player);
			}
			selectedPlayer = (PlayerEntity) player;

			guiHandler.setPlayerDisplay(player);
			
		}
    	
	}
	
	public void handleLandPressed(boolean right){
		if(right){
			if(selectedPlayer != null){
				selectedPlayer.clearEvents();
				selectedPlayer.clearPath();
				Vector3f worldPosition = MouseHandler.getWorldPostion();
				selectedPlayer.moveToPosition(worldPosition, masterTerrain);
			}
		}
    	

	}

	
	/**
	 * returns a boolean which checks if a button has been
	 * pressed in a certain amount of time
	 * @return
	 */
	public boolean timeBuffer(){
		if(lastPress + 0.3 <= currentTime) return true;
		return false;	
	}
	
	
	/**
	 * check to see if a button was pressed
	 */
	private void checkInput(){
		if(timeBuffer()){

			if(MouseHandler.isDoubleClick() && !masterCamera.isAerialMode()){
				masterCamera.setAerialMode(selectedPlayer.getPosition(), selectedPlayer.getRotY());
				selectedPlayer = null;

			}
			if(Keyboard.isKeyDown(Keyboard.KEY_1)){
				masterCamera.setPlayerMode(players.get(0));
				selectedPlayer = players.get(0);
				lastPress = currentTime;


			}
			if(Keyboard.isKeyDown(Keyboard.KEY_2)){
				masterCamera.setPlayerMode(players.get(1));
				selectedPlayer = players.get(1);
				lastPress = currentTime;


			}
			if(Keyboard.isKeyDown(Keyboard.KEY_3)){
				masterCamera.setPlayerMode(players.get(2));
				selectedPlayer = players.get(2);
				lastPress = currentTime;


			}

		}

	}
	
	
	public boolean isAerialMode(){
		return masterCamera.isAerialMode();
	}
	

	/**
	 * loads up the normals of the 5 planes that make up the projection matrix;
	 * we treat the camera as a point on all 4 sides of the projection matrix, 
	 * and get the negative camera ray as the normal for the front face.
	 */
	public void frustrumCull(){
		Vector3f p = masterCamera.getPosition(); //position of camera
		Vector3f d = MouseHandler.getCameraRay();//picker.getCameraRay(); //where the camera is pointing
		Vector3f up = Maths.getUpVector(d);
		Vector3f rightV = Maths.getRightVector(d, up);

		float farDist = MasterRenderer.FAR_PLANE;
		float Hfar = MasterRenderer.HFAR;
		float Wfar = MasterRenderer.WFAR;
		
		fc = Maths.vectorAdd(p, Maths.vectorScale(d, farDist));

		Vector3f ftl = getFtl(fc, up, Hfar, rightV, Wfar);
		Vector3f ftr = getFtr(ftl, rightV, Wfar);
		Vector3f fbl = getFbl(ftl, up, Hfar);
		Vector3f fbr = getFbr(fbl, rightV, Wfar);
		
		Vector3f[] top = {ftl, ftr, p};
		Vector3f[] bottom = {fbr, fbl, p};
		Vector3f[] left = {fbl, ftl, p};
		Vector3f[] right = {ftr, fbr, p};

		normals[0] = Maths.normalise(top);
		normals[1] = Maths.normalise(bottom);
		normals[2] = Maths.normalise(left);
		normals[3] = Maths.normalise(right);
		normals[4] = Maths.negativeVector(d);

		
		//get collision box for entity and transform each point into world coords.
		//check if point is within the view frustrum volume. if any point is within the volume
		//then render the entity
		
		frustrumCullTerrain();
		for(Terrain terrain: terrainsOnScreen){
			for(Entity entity : terrain.getAllEntities()){
				Vector3f minVector = Maths.getWorldCoords(entity.getMinVector(), entity);
				Vector3f maxVector = Maths.getWorldCoords(entity.getMaxVector(), entity);
				ArrayList<Vector3f> collisionCorners = new ArrayList<Vector3f>();
				collisionCorners.add(minVector);
				collisionCorners.add(maxVector);
				for(Vector3f vec: collisionCorners){
					if(checkPoint(vec, masterCamera.getPosition(), fc) == true){
						//if(Maths.getVector3fDistance(p, entity.getPosition()) < 900){
							if(entity.isInteractable()){
								entitiesOnScreen.add(entity);
							}
							entCount++;
							renderer.processEntity(entity);

							break;
						//}
						
					}
				}	
			}
		}
		for(PlayerEntity player: players){
			
			
			Vector3f minVector = Maths.getWorldCoords(player.getMinVector(), player);
			Vector3f maxVector = Maths.getWorldCoords(player.getMaxVector(), player);
			ArrayList<Vector3f> collisionCorners = new ArrayList<Vector3f>();
			collisionCorners.add(minVector);
			collisionCorners.add(maxVector);

			for(Vector3f vec: collisionCorners){
				if(checkPoint(vec, masterCamera.getPosition(), fc) == true){
					entitiesOnScreen.add(player);
					break;
				}
			}
		}
	}
	
	public void frustrumCullTerrain(){

		for(Terrain terrain: masterTerrain.getTerrains()){
			for(Vector3f point: terrain.getCheckPoints()){
				if(checkPoint(point, masterCamera.getPosition(), fc)){
					if(Maths.getVector3fDistance(point, masterCamera.getPosition()) < 800){
						terrainsOnScreen.add(terrain);
						renderer.processTerrain(terrain);
						break;
					}

				}
			}
		}

	}
	
	/**
	 * checks if a point is inside the frustrum and if it is return true
	 * the equation for a plane is ax + by + cz = -d where (a,b,c) is the normal of the plane
	 * d can be calculated by getting the dot product of a point on the plane and the negative
	 * xyz are the points of the vector we are testing, and if the result is negative then the
	 * point of behind the plane. if the point is behind a single one of the planes then it is 
	 * not inside the view frustrum and we return false.
	 * normal of the plane. 
	 * @param v : point we are checking
	 * @param p : camera position
	 * @param fc : center of front face of projection matrix
	 * @return
	 */
	
	public boolean checkPoint(Vector3f v, Vector3f p, Vector3f fc){
		
		Vector3f frontN = normals[4];
		float frontD = Vector3f.dot(fc, Maths.negativeVector(frontN));
		float frontDistance = v.x*frontN.x + v.y*frontN.y + v.z*frontN.z + frontD;
		if(frontDistance < 0){
			return false;
		}
		
		for(int i = 0; i < 4; i++){
			Vector3f n = normals[i];
			float d = Vector3f.dot(p, Maths.negativeVector(n));
			float distance = v.x*n.x + v.y*n.y + v.z*n.z + d;
			if(distance < 0){
				return false;
			}
		}
		return true;
	}
	
/**
 * get the top left vertice of the view frustrum
 * @param fc
 * @param up
 * @param Hfar
 * @param right
 * @param Wfar
 * @return
 */
	public Vector3f getFtl(Vector3f fc, Vector3f up, float Hfar, Vector3f right, float Wfar){
		fc = Maths.copy(fc);
		up = Maths.copy(up);
		right = Maths.copy(right);
		Vector3f ftr = new Vector3f();
		Vector3f.add(fc, Maths.vectorScale(up, Hfar/2), ftr);
		Vector3f.add(ftr,  Maths.vectorScale(right, Wfar/2), ftr);
		return ftr;
	}
	
	/**
	 * get the top right of the view  frustrum
	 * @param ftl
	 * @param right
	 * @param Wfar
	 * @return
	 */
	public Vector3f getFtr(Vector3f ftl, Vector3f right, float Wfar){
		ftl = Maths.copy(ftl);
		right = Maths.copy(right);
		Vector3f ftr = Maths.vectorSub3f(ftl, Maths.vectorScale(right, Wfar));
		return ftr;
	}
	
	/**
	 * get the bottom left of the view frustrum
	 * @param ftl
	 * @param up
	 * @param Hfar
	 * @return
	 */
	public Vector3f getFbl(Vector3f ftl, Vector3f up, float Hfar){
		ftl = Maths.copy(ftl);
		up = Maths.copy(up);
		Vector3f fbr = Maths.vectorSub3f(ftl, Maths.vectorScale(up, Hfar));
		return fbr;
	}

	/**
	 * get the bottom right of the view frustrum
	 * @param fbl
	 * @param right
	 * @param Wfar
	 * @return
	 */
	public Vector3f getFbr(Vector3f fbl, Vector3f right, float Wfar){
		fbl = Maths.copy(fbl);
		right = Maths.copy(right);
		Vector3f fbr = Maths.vectorSub3f(fbl, Maths.vectorScale(right, Wfar));
		return fbr;
	}
	
	public static float getTime(){
		return currentTime;
	}

	public static float getGravity(){
		return GRAVITY;
	}
	

	


}


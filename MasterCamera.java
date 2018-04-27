package Camera;

import org.lwjgl.util.vector.Vector3f;

import player.PlayerEntity;
import terrain.Terrain;

public class MasterCamera {

	private boolean aerialMode = true;
	
	private PlayerCamera playerCamera;
	private AerialCamera aerialCamera;
	private CameraInterface curCamera;
	
	public MasterCamera(PlayerCamera playerCam, AerialCamera aerialCam){
		this.playerCamera = playerCam;
		this.aerialCamera = aerialCam;
		if(aerialMode) curCamera = aerialCamera;
		else curCamera = playerCamera;
	}
	
	public void update(Terrain curPlayerTerrain){
		if(!getCurrentCamera().isInPos()){
			if(isAerialMode()){
				getCurrentCamera().cameraMoveTo(new Vector3f(), curPlayerTerrain);
			}else{
				getCurrentCamera().cameraMoveTo(playerCamera.getPlayer().getPosition(), curPlayerTerrain);
			}
		}else{
			move(curPlayerTerrain);
		}
	}
	
	
	public boolean isAerialMode(){
		return aerialMode;
	}
	

	
	public Vector3f getPosition(){
		return this.curCamera.getPosition();
	}

	
	public void setPlayerMode(PlayerEntity player){
		playerCamera.setInPos(false);

		if(aerialMode){
			aerialMode = false;			
			playerCamera.setUpTransition(curCamera.getPosition(), curCamera.getRotation(), 
					curCamera.getPitch(), player);
		}else{
			playerCamera.setUpPlayerTransition(player, playerCamera.getPosition());
		}
		curCamera = playerCamera;

	}
	
	public void switchPlayerMode(PlayerEntity player){
		//playerCamera.setInPos(true);
		playerCamera.setPlayer(player);
		//playerCamera.setUpTransition(playerCamera.getPosition(), playerCamera.getRotation(), 
			//	playerCamera.getPitch(), playerCamera.getZoom());
	}
	
	public void setAerialMode(Vector3f position, float rotation){
		aerialMode = true;
		//playerCamera.getPlayer().setIsControlled(false);
		curCamera = aerialCamera;
		aerialCamera.setInPos(false);
		playerCamera.setInPos(false);

		
		aerialCamera.setUpTransition(playerCamera.getPosition(), playerCamera.getPitch(), 
				playerCamera.getRotation(), playerCamera.getZoom());

	}
	
	public void move(Terrain terrain){
		if(aerialMode){
			aerialCamera.move(terrain);
		}else{
			playerCamera.move(terrain);
		}
	}
	
	
	
	public boolean getCameraMode(){
		return this.aerialMode;
	}
	
	public AerialCamera getAerialCamera(){
		return this.aerialCamera;
	}
	
	public PlayerCamera getPlayerCamera(){
		return this.playerCamera;
	}
	
	public CameraInterface getCurrentCamera(){
		return curCamera;
	}
}

package Camera;

import org.lwjgl.util.vector.Vector3f;

import mousepicker.MouseHandler;
import player.PlayerEntity;
import terrain.Terrain;
import toolbox.Maths;

public class PlayerCamera implements CameraInterface{
	
	
	private Vector3f position = new  Vector3f(0, 10f, 0);
	private Vector3f wantedPosition;

	private float pitch = 20;
	private float yaw;
	private float angleAroundPlayer = 0; 
	private float wantedAngleAroundPlayer = 180;
	private boolean inPos = false;
	private int distanceFromPlayer = 10;
	private final int wantedDistanceFromPlayer = 200;
	private final float wantedPitch = 40;
	private PlayerEntity player;
	
	public PlayerCamera(PlayerEntity player){
		this.player = player;
	}
	
	public void setPlayer(PlayerEntity player){
		this.player = player;
	}
	
	
	
	public void move(Terrain terrain){
		calculatePitch(terrain);
		
		calculateYaw();
		calculateCameraPosition();
	
	}
	
	public void cameraMoveTo(Vector3f dest, Terrain terrain){
		
		//figure this out later
		float angleDif = wantedAngleAroundPlayer - angleAroundPlayer;
		if(angleDif > 180){
			angleAroundPlayer += angleDif/10;

		}else{
			angleAroundPlayer += angleDif/10;

		}

		calculateYaw();
		calculatePitch(terrain);


		float dx = (wantedPosition.x - position.x)/10;
		float dy = (wantedPosition.y - position.y)/10;
		float dz = (wantedPosition.z - position.z)/10;
		position.x += dx;
		position.y += dy;
		position.z += dz;
		pitch += (wantedPitch - pitch)/10;

		if(Math.abs(dx) < 0.1 && Math.abs(dy) < 0.1 && Math.abs(dz) < 0.1){
			inPos = true;
			distanceFromPlayer = wantedDistanceFromPlayer;
			calculateCameraPosition();
		}

	
	}
	
	private void calculateYaw(){
		float netAngle = angleAroundPlayer;
		this.yaw = 180 - netAngle;
	}
	
	public void calculatePitch(Terrain terrain){
		if(MouseHandler.isLeftButtonDragged()){
//			if(terrain != null && position.y + calculateVerticalDistance() >= terrain.getHeightOfTerrain(position.x, position.z) + 1){
				pitch -= MouseHandler.getDY() * 0.1f;
//			}else pitch += 0.05f;
			float changeInX = MouseHandler.getDX()*0.3f;
			angleAroundPlayer = (angleAroundPlayer - changeInX) % 360;
		}
	}
	
	public void setUpTransition(Vector3f pos, float oldRot, float givenPitch, PlayerEntity player){
		//set pitch and angle around player
		float oldPitch = givenPitch;
		calculateYaw();
		//calculate how far from the player the camera should be
		float xDist = pos.x - player.getPosition().x;
		float yDist = pos.y - player.getPosition().y;
		float zDist = pos.z - player.getPosition().z;
		float hDist = (float) Math.sqrt((xDist*xDist) + (zDist*zDist) + (yDist*yDist));
		setPlayer(player);
		this.pitch = wantedPitch;// (float) Math.toDegrees(Math.asin(vDist/hDist));
		angleAroundPlayer = (player.getRotY()) % 360;
		wantedAngleAroundPlayer = angleAroundPlayer;
		distanceFromPlayer = wantedDistanceFromPlayer;
		calculateCameraPosition();

		wantedPosition = position;
		
		
		distanceFromPlayer = (int) hDist;//(int) Maths.getVectorDistance(pos, player.getPosition()  );
		this.angleAroundPlayer = oldRot;

		pitch = oldPitch;
		System.out.println("position:" + position);
		System.out.println("pos: " + pos );
		position = pos;

		inPos = false;
		

	}
	
	public void setUpPlayerTransition(PlayerEntity player, Vector3f givenPos){
		//set pitch and angle around player
		Vector3f oldPos = Maths.vectorClone(givenPos);
		float oldPitch = pitch;
		System.out.println("cameraPos before we start: " + oldPos);
		//calculate how far from the player the camera should be
		float xDist = position.x - player.getPosition().x;
		float yDist = position.y - player.getPosition().y;
		float zDist = position.z - player.getPosition().z;
		float hDist = (float) Math.sqrt((xDist*xDist) + (zDist*zDist) + (yDist*yDist));
		pitch = wantedPitch;
		distanceFromPlayer = wantedDistanceFromPlayer;
		setPlayer(player);
		float oldAngle = angleAroundPlayer;
		angleAroundPlayer = (player.getRotY()) % 360;
		wantedAngleAroundPlayer = angleAroundPlayer;
		calculateCameraPosition();
		angleAroundPlayer = oldAngle;
		wantedPosition = Maths.vectorClone(position);
		distanceFromPlayer = (int) hDist;//(int) Maths.getVectorDistance(pos, player.getPosition()  );
		pitch = oldPitch;
		System.out.println("test: " + oldPos);

		position = Maths.vectorClone(oldPos);


		inPos = false;
		

	}

	
	
	private void calculateCameraPosition(){
		position.y = player.getPosition().y + calculateVerticalDistance();
		float theta = (float) Math.toRadians(angleAroundPlayer);
		position.x = (float) (player.getPosition().x - calculateHorizontalDistance() * Math.sin(theta));
		position.z = (float) (player.getPosition().z - calculateHorizontalDistance() * Math.cos(theta));

	}
	
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	public PlayerEntity getPlayer(){
		return this.player;
	}
	
	public void setInPos(boolean b){
		inPos = b;
	}
	
	public boolean isInPos(){
		return this.inPos;
	}

	
	public void setPosition(Vector3f pos){
		this.position = Maths.vectorClone(pos);
	}
	
	public void setRotation(int rot){
		this.angleAroundPlayer = rot;
	}
	public float getRotation(){
		return this.angleAroundPlayer;
	}
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public int getZoom() {
		return this.distanceFromPlayer;
	}


	
	
	
	
}

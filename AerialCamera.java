package Camera;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import mousepicker.MouseHandler;
import renderEngine.DisplayManager;
import terrain.Terrain;
import toolbox.Maths;

public class AerialCamera implements CameraInterface{
	
	//actual camera attribues
	private Vector3f position = new  Vector3f(0, 150f, 0);
	private Vector3f wantedPosition = new Vector3f(0,0,0);

	private float pitch = 20;
	private float yaw;
	private int distanceFromPlayer = 100;
	
	private float wantedDistanceFromPlayer = 20;
	private float wantedPitch = 50;
	
	private boolean inPos = true;

	//private Vector3f centerPosition = new Vector3f(0, 150, 0);
	//private Vector3f wantedCenterPosition = new Vector3f(0,0,0);
	private float yRotation;

	private float sideSpeed;
	private float currentSpeed;
	private final float RUN_SPEED = 300;
	
	
	public AerialCamera(){
		super();
		yRotation = 0;
		pitch = 0;
		yaw = 0;
	}
	

	public void move(Terrain terrain){
		updatePosition();
		calculateYaw();		

	}
	
	public void cameraMoveTo(Vector3f dest, Terrain terrain){
		calculatePitch(terrain);
		calculateYaw();

		float dx = (wantedPosition.x - position.x)/10;
		float dy = (wantedPosition.y - position.y)/10;
		float dz = (wantedPosition.z - position.z)/10;
		pitch += (wantedPitch - pitch) / 10;
		distanceFromPlayer += (wantedDistanceFromPlayer - distanceFromPlayer) / 10;

		position.x += dx;
		position.y += dy;
		position.z += dz;
		yRotation %= 360;

		if(Math.abs(dx) < 0.7 && Math.abs(dy) < 0.7 && Math.abs(dz) < 0.7) inPos = true;
		
	}

	public void setInPos(boolean b){
		inPos = b;
	}
	
	public void updatePosition(){
		checkInputs();
		float frontSpeed = currentSpeed;
		float frontX = (float) (frontSpeed * Math.sin(Math.toRadians(yRotation)));
		float frontZ = (float) (frontSpeed * Math.cos(Math.toRadians(yRotation)));
		float curSizeSpeed = sideSpeed;

		float sideX = (float) (curSizeSpeed * Math.sin(Math.toRadians(yRotation+90)));
		float sideZ = (float) (curSizeSpeed * Math.cos(Math.toRadians(yRotation+90)));

		position.x += (frontX + sideX) * DisplayManager.getFrameTimeSeconds();
		position.z += (frontZ + sideZ) * DisplayManager.getFrameTimeSeconds();
		
	}
	
	private void checkInputs(){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentSpeed = RUN_SPEED;

		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			
			this.currentSpeed = -RUN_SPEED;
		}else{
			if(currentSpeed < 0){
				this.currentSpeed += RUN_SPEED / 20;

			}else if(currentSpeed > 0){
				this.currentSpeed -= RUN_SPEED / 20;

			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.sideSpeed = RUN_SPEED;

		}else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			
			this.sideSpeed = -RUN_SPEED;
		}else{
			if(sideSpeed < 0){
				this.sideSpeed += RUN_SPEED / 20;

			}else if(sideSpeed > 0){
				this.sideSpeed -= RUN_SPEED / 20;

			}
		}
	}

	public void calculatePitch(Terrain terrain){
		if(MouseHandler.isLeftButtonDragged()){
			
			pitch -= MouseHandler.getDY() * 0.1f;
			if(pitch > 60) pitch = 60;
			else if(pitch < 15) pitch = 15;
			
			float changeInX = MouseHandler.getDX()*0.3f;
			yRotation = (yRotation - changeInX) % 360;
		}
		
	}
	
	public void setPosition(Vector3f pos){
		this.position = pos;
	}
	
	private void calculateYaw(){
		this.yaw = 180 - yRotation;
	}

	public void setUpTransition(Vector3f pos, float pitch, float rotation, int zoom){
		this.position = Maths.vectorClone(pos);
		this.wantedPosition = Maths.vectorClone(pos);
		this.wantedPosition.y = 200;
		this.wantedPosition.x -= wantedDistanceFromPlayer * Math.sin(Math.toRadians(rotation));
		this.wantedPosition.z -= wantedDistanceFromPlayer * Math.cos(Math.toRadians(rotation));
		this.pitch = pitch;
		this.yRotation = rotation;
		this.distanceFromPlayer = zoom;

		calculateYaw();
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public float getRotation(){
		return this.yRotation;
	}


	public float getPitch() {
		return pitch;
	}


	public int getZoom(){
		return distanceFromPlayer;
	}
	
	public float getYaw() {
		return yaw;
	}


	@Override
	public boolean isInPos() {
		return inPos;
	}



	
	
	
	
	
}
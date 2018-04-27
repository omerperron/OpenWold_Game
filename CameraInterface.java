package Camera;

import org.lwjgl.util.vector.Vector3f;

import terrain.Terrain;

public abstract interface CameraInterface {
	public Vector3f getPosition();
	public float getRotation();
	public float getYaw();
	public float getPitch();
	public int getZoom();

	public void calculatePitch(Terrain terrain);
	public void cameraMoveTo(Vector3f v, Terrain terrain);
	public boolean isInPos();
}

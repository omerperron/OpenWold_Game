package objects;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.TexturedModel;
import player.PlayerEntity;
import terrain.MasterTerrain;

public class GameObject extends Entity{
    private String name;
    private int textureId;
    
    public GameObject(String name, TexturedModel model, Vector3f position, int textureId){
    	super(model, position, 0, 1, false, false);
    	this.name = name;
    	this.textureId = textureId;
    	
    }
    

    
    public int getTextureId(){
    	return this.textureId;
    }
    
   
    public String getName(){
    	return this.name;
    }



	@Override
	public void interact(PlayerEntity player, MasterTerrain masterTerrain) {
		// TODO Auto-generated method stub
		
	}
  
   
    

}


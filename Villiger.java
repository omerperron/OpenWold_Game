package player;

import org.lwjgl.util.vector.Vector3f;

import guis.GuiTexture;
import models.TexturedModel;
import terrain.MasterTerrain;

public class Villiger extends PlayerEntity {

	public Villiger(TexturedModel model, Vector3f position, GuiTexture items, GuiTexture texture) {
		super(model, position, 100, 100, 100, items, texture);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void interact(PlayerEntity player, MasterTerrain masterTerrain) {
		// TODO Auto-generated method stub
		
	}

}

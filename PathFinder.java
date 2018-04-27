package pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import terrain.MasterTerrain;
import terrain.Terrain;
import terrain.TerrainNode;

public class PathFinder {
	
	private static TerrainNode curDestination;
	
	public static Map<TerrainNode, Terrain> AStarSearch(TerrainNode endPos, Vector3f startPos,
			MasterTerrain masterTerrain){
		//if(true) return null;
		for(Terrain terrain: masterTerrain.getTerrains()){
			terrain.calculateHPath(endPos);
		}
		curDestination = endPos;
		Terrain startTerrain = masterTerrain.getTerrain(startPos);
		TerrainNode startingPoint = masterTerrain.getTerrainNode(startPos.x, startPos.z);
		System.out.println("moving to: " + endPos.getGridX() + " " + endPos.getGridZ());

		
		//key: (x,y) block position, z = hCost
		//value: [0] = Vector3f parent, [1] = gCost, [2] terrain
		Map<TerrainNode, Object[]> openList = new HashMap<TerrainNode, Object[]>(); 
		
		ArrayList<TerrainNode> closedList = new ArrayList<TerrainNode>();
		ArrayList<TerrainNode> newPath = new ArrayList<TerrainNode>();
		Map<TerrainNode, Terrain> tempPaths = new LinkedHashMap<TerrainNode, Terrain>();
		
		openList.put(startingPoint, new Object[]{new Vector3f(-1f,-1f,-1f), 0f, startTerrain});
		
		TerrainNode lowestCost;
		

		
		int cycles = 0;
		while(true){
			lowestCost = getLowestCost(openList, closedList);
			if(lowestCost == null) {
				return null;
			}
			if(lowestCost.equals(endPos)) break;

			
			addNeighboursToOpenList(openList, lowestCost, closedList, masterTerrain);
			closedList.add(lowestCost);
			if(cycles++ == 100) return null;
			
			
		}
		
		TerrainNode curPath = lowestCost;
		while(true){
			newPath.add(curPath);

			if(curPath.equals(startingPoint)) break;
			curPath = (TerrainNode) openList.get(curPath)[0];
		}
		Collections.reverse(newPath);

		
		for(TerrainNode vector: newPath){
			tempPaths.put(vector, (Terrain) openList.get(vector)[2]);
		}
	
		return tempPaths;
	}
	
	
	private static void addNeighboursToOpenList(Map<TerrainNode, Object[]> openList,
			TerrainNode position, ArrayList<TerrainNode> closedList, MasterTerrain masterTerrain){
		float horizontalCost = Terrain.getBlockSize();
		float diagonalCost = (float) Math.sqrt((2*(horizontalCost*horizontalCost)));
		
		addToList(0, 1,  openList, closedList, position, horizontalCost, masterTerrain);//^
		addToList(0, -1, openList, closedList, position, horizontalCost, masterTerrain);//v
		addToList(1, 0, openList, closedList, position, horizontalCost, masterTerrain);//>
		addToList(-1, 0, openList, closedList, position, horizontalCost, masterTerrain);//<

		addToList(1, 1, openList, closedList, position, diagonalCost, masterTerrain); //^>
		addToList(-1, 1, openList, closedList, position, diagonalCost, masterTerrain); //<^
		addToList(1, -1, openList, closedList, position, diagonalCost, masterTerrain); //v>
		addToList(-1, -1,  openList, closedList, position, diagonalCost, masterTerrain); //v<
		
	}
	
	private static void addToList(int xDir, int yDir,  Map<TerrainNode, Object[]> openList, 
			ArrayList<TerrainNode> closedList, TerrainNode position, float cost, MasterTerrain masterTerrain){
		
		int x = (int) (position.getGridX() + xDir);
		int z = (int) (position.getGridZ() + yDir);

		Terrain terrain = masterTerrain.getTerrainGrid(x, z);
		TerrainNode curNeighbour = terrain.getTerrainNode(x, z);
		
	    for(Entity entity: terrain.getCollisionEntities()){
	    	if(entity.isCollision(curNeighbour) && !curNeighbour.equals(curDestination)) return;	
	    }

		if(!closedList.contains(curNeighbour)){
			if((!openList.containsKey(curNeighbour))){
				openList.put(curNeighbour,new Object[]{position, (float) openList.get(position)[1] + cost,terrain});
			}else if((float) openList.get(position)[1] + cost  <  (float) openList.get(curNeighbour)[1]){
				openList.put(curNeighbour, new Object[]{position, (float) openList.get(position)[1] + cost, terrain });
			}
		}
	}
	


	private static TerrainNode getLowestCost(Map<TerrainNode, Object[]> openList, 
			ArrayList<TerrainNode> closedList){
		
		TerrainNode node = null; 
		float cost = -1;
		for (Map.Entry<TerrainNode, Object[]> entry : openList.entrySet()) {
		    TerrainNode curNode = entry.getKey();
		    //if(curNode == null) continue;
		   // System.out.println("curNode:" + curNode.getGridX() + " " + curNode.getGridZ());
		    Object[] value = entry.getValue();
		    float gCost = (float) value[1];
		    Terrain terrain = (Terrain) value[2];
		    
		    boolean cont= false;
		    for(Entity entity: terrain.getCollisionEntities()){
		    	//we check if size is bigger then one to determine if we are
		    	//starting in the corner of a collision box
		    	if(entity.isCollision(curNode) && openList.size() > 1)
		    		if(!curNode.equals(curDestination)){
		    			cont = true;
		    		}
		    }
	
			if(closedList.contains(curNode) || cont == true) continue;
			
			
			float curFCost = (curNode.gethCost() + gCost);
		    if((node == null || cost == -1) ||  curFCost < cost){
		    	cost = curFCost;
		    	node = curNode;
		    }
		}
		return node;
	}

}

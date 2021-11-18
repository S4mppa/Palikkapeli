package com.entity.game.client.voxelhandling;


import com.entity.game.client.misc.*;
import com.entity.game.client.world.BlockPosition;
import com.entity.game.client.world.Chunk;
import com.entity.game.client.world.ChunkPosition;
import com.entity.game.client.world.World;


import java.util.*;

public class TerrainGeneration extends TerrainGenerator{
    private int CHUNKSIZE = WorldConstants.CHUNKSIZE;
    private int MAXHEIGHT = 4*CHUNKSIZE;

    private HashMap<Integer, BlockData> blockDataMap;
    private int seed;
    private OpenSimplexNoise open;
    private Random random = new Random();
    private int[] lastCamPos;
    private SimplexNoise simplexNoise1;
    private SimplexNoise simplexNoise2;
    private SimplexNoise simplexNoise3;
    private OpenSimplexNoise openSimplexNoise;
    private SimplexNoise biomeNoise;
    private SimplexNoise biomeNoise1;
    private SimplexNoise biomeNoise2;
    private final FrustumCullingFilter frustumCullingFilter = new FrustumCullingFilter();


    private FastNoiseLite caveNoise;

    public TerrainGeneration(World world){
        super(world);
        this.seed = world.getSeed();
        blockDataMap = new HashMap<>();
        lastCamPos = new int[2];
        open = new OpenSimplexNoise(345);
        addBlockData();

        initNoise();
    }

    private void initNoise(){
        simplexNoise1 = new SimplexNoise(1000, 0.4, seed);
        simplexNoise2 = new SimplexNoise(300, 0.5, seed);
        simplexNoise3 = new SimplexNoise(400, 0.01, seed);

        openSimplexNoise = new OpenSimplexNoise(seed);

        biomeNoise = new SimplexNoise(5000, 0.6,seed+1234);
        biomeNoise1 = new SimplexNoise(10000, 0.2, seed+1234);
        biomeNoise2 = new SimplexNoise(12000, 0.4, seed+1234);

        caveNoise = new FastNoiseLite(seed);
        openSimplexNoise = new OpenSimplexNoise(seed);
        caveNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        caveNoise.SetFrequency(0.01f);
        caveNoise.SetFractalType(FastNoiseLite.FractalType.Ridged);
        caveNoise.SetFractalLacunarity(3.0f);
    }

    public void addBlockData(){
        for(BlockData blockData : BlockData.values()){
            blockDataMap.put(blockData.getBlockID(), blockData);
        }
    }

    public int getMapHeightAt(int x, int z, Biomes biome){

        double noise = simplexNoise1.getNoise(x,z);
        double noise2 = simplexNoise2.getNoise(x,z);
        double noise3 = simplexNoise3.getNoise(x,z);
        double noise4 = openSimplexNoise.eval(x/100f,z/100f);
        noise = (noise + 1) / 2;
        noise2 = (noise2 + 1) / 2;
        noise3 = (noise3 + 1) / 2;
        noise4 = (noise4 + 1) / 2;
        noise4 *= 2.41231238272736;
        noise *= biome.getMaxHeight()-5;

        return (int) (noise*noise2*noise3*noise4);
    }

    public Biomes getBiome(int x, int z){
        double noise = biomeNoise.getNoise(x,z);
        double noise2 = biomeNoise2.getNoise(x,z);
        double noise3 = biomeNoise2.getNoise(x,z);
        noise = (noise + 1) / 2;
        noise2 = (noise2 + 1) / 2;
        noise3 = (noise3 + 1) / 2;

        noise*=100;
        noise2*=7;

        double biome = (noise*noise2*noise3);
        if(biome < 60){
            return Biomes.TAIGA;
        }
        else if(biome < 80){
            return Biomes.SWAMP;
        }
        else if(biome < 120){
            return Biomes.DESERT;
        }
        else if(biome < 180){
            return Biomes.OCEAN;
        }
        else if(biome < 300){
            return Biomes.MOUNTAINS;
        }
        else{
            return Biomes.FOREST;
        }

    }



    private int[] createChunkHeightMap(Chunk chunk){
        ChunkPosition chunkPosition = chunk.getOrigin();
        Biomes bbLeft = getBiome(chunkPosition.x,chunkPosition.z);
        Biomes bbRight = getBiome(chunkPosition.x+CHUNKSIZE,chunkPosition.z);
        Biomes btLeft = getBiome(chunkPosition.x,chunkPosition.z+CHUNKSIZE);
        Biomes btRight = getBiome(chunkPosition.x+CHUNKSIZE,chunkPosition.z+CHUNKSIZE);
        chunk.setBiome(bbLeft);
        float bottomLeft = getMapHeightAt(chunkPosition.x,chunkPosition.z, bbLeft);
        float bottomRight = getMapHeightAt(chunkPosition.x+CHUNKSIZE,chunkPosition.z, bbRight);
        float topLeft = getMapHeightAt(chunkPosition.x,chunkPosition.z+CHUNKSIZE, btLeft);
        float topRight = getMapHeightAt(chunkPosition.x+CHUNKSIZE,chunkPosition.z+CHUNKSIZE, btRight);

        float wbLeft = bbLeft.getWaveStrength();
        float wbRight = bbRight.getWaveStrength();
        float wtLeft = btLeft.getWaveStrength();
        float wtRight = btRight.getWaveStrength();

        boolean interpolate = !(bbLeft.getMaxHeight() == bbRight.getMaxHeight() && bbLeft.getMaxHeight() == btLeft.getMaxHeight() && bbLeft.getMaxHeight() == btRight.getMaxHeight());

        int[] heightMap = new int[WorldConstants.CHUNKAREA];
        for(int x = 0; x < CHUNKSIZE; x++){
            for(int z = 0; z < CHUNKSIZE; z++){
                int bx = x + chunkPosition.x;
                int bz = z + chunkPosition.z;
                if(interpolate){
                    heightMap[z * CHUNKSIZE + x] = (int) Maths.smoothInterpolation(bottomLeft, topLeft, bottomRight, topRight, chunkPosition.x, chunkPosition.x+CHUNKSIZE, chunkPosition.z, chunkPosition.z+CHUNKSIZE, bx, bz);
                }
                else {
                    heightMap[z * CHUNKSIZE + x] = (int) getMapHeightAt(bx,bz,bbLeft);
                }

            }
        }
        return heightMap;

    }


    public void genTerrain(Chunk c){
        int[] heightMap = createChunkHeightMap(c);
        int cy = c.getOrigin().y;
        for(int x = 0 ; x < CHUNKSIZE; x++){
            for(int z = 0; z <  CHUNKSIZE; z++){
                int height = heightMap[z * CHUNKSIZE + x];
                Biomes biome = getBiome(c.getOrigin().x+x,c.getOrigin().z+z);
                for(int y = 0; y < CHUNKSIZE; y++){
                    int blockY = cy + y;
                    if(blockY <= height+CHUNKSIZE){
                        if(blockY == height+CHUNKSIZE){
                            if(height+CHUNKSIZE >= 110){
                                int r = random.nextInt(10);
                                if(r < 1){
                                    c.replaceBlockType(new BlockPosition(x,y,z), 5);
                                }
                                else if(r < 3){
                                    c.replaceBlockType(new BlockPosition(x,y,z), 15);
                                }
                                else {
                                    c.replaceBlockType(new BlockPosition(x,y,z), 2);
                                }

                            }
                            else if(height+CHUNKSIZE >= 100){
                                int r = random.nextInt(10);
                                if(r < 3){
                                    c.replaceBlockType(new BlockPosition(x,y,z), 5);
                                }
                                else{
                                    c.replaceBlockType(new BlockPosition(x,y,z), 2);
                                }

                            }
                            else if(height+CHUNKSIZE >= 90){
                                int r = random.nextInt(10);
                                if(r < 3){
                                    c.replaceBlockType(new BlockPosition(x,y,z), 2);
                                }
                                else if(r < 6){
                                    c.replaceBlockType(new BlockPosition(x,y,z), 5);
                                }
                                else {
                                    c.replaceBlockType(new BlockPosition(x,y,z), biome.getBlockType());
                                }

                            }
                            else if(height+CHUNKSIZE >= 70){
                                if(random.nextInt(10) < 4){
                                    c.replaceBlockType(new BlockPosition(x,y,z), 2);
                                }
                                else {
                                    c.replaceBlockType(new BlockPosition(x,y,z), biome.getBlockType());
                                }

                            }
                            else if(height+CHUNKSIZE >= biome.getWaterLevel()+1){
                                c.replaceBlockType(new BlockPosition(x,y,z), biome.getBlockType());
                            }
                            else {
                                c.replaceBlockType(new BlockPosition(x,y,z), 4);
                            }

                        }
                        else{
                            if(blockY < height+CHUNKSIZE && blockY < height+CHUNKSIZE-5){
                                c.replaceBlockType(new BlockPosition(x,y,z), 2); //stone
                            }
                            else{
                                if(height+CHUNKSIZE < 90){
                                    c.replaceBlockType(new BlockPosition(x,y,z), 3); //dirt
                                }
                                else {
                                    c.replaceBlockType(new BlockPosition(x,y,z), 2); //stone
                                }

                            }

                        }


                    }
                    if(blockY < biome.getWaterLevel() && c.getBlockAt(x,y,z)==0){
                        c.replaceBlockType(new BlockPosition(x,y,z), 6); //water

                    }
                    genDecorations(c,x,y,z,blockY,cy+height, biome);
                }

            }

        }
        //simple cave generation
        for(int x = 0 ; x < CHUNKSIZE; x++) {
            for (int z = 0; z < CHUNKSIZE; z++) {
                int height = heightMap[z * CHUNKSIZE + x];
                for (int y = 0; y < CHUNKSIZE; y++) {
                    int blockY = cy + y;
                    if (blockY <= height + CHUNKSIZE-5) {
                        double caveN = caveNoise.GetNoise(c.getOrigin().x+x,blockY,+c.getOrigin().z+z) * 10;
                        if(caveN > 8) c.replaceBlockType(new BlockPosition(x,y,z), 0);
                        if(blockY == 0) c.replaceBlockType(new BlockPosition(x,y,z),2);
                    }
                }
            }
        }
    }
    public void genDecorations(Chunk c, int x, int y, int z, int blockY, int chunkTop, Biomes biome){
        if(biome.equals(Biomes.TAIGA)){
            if(c.getBlockAt(x,y,z) == 6 && blockY == biome.getWaterLevel()-1 && random.nextFloat() < 0.3){ //water
                c.replaceBlockType(new BlockPosition(x,y,z), 18); //ice
            }
        }
        if(blockY > biome.getWaterLevel() && blockY == chunkTop && c.getBlockAt(x,y,z)==biome.getBlockType()){
            //make a simple tree

            if(biome.equals(Biomes.FOREST)){
                if(random.nextFloat() < biome.getDecorationRarity()){
                    Decorations.tree(TreeType.OAK, x,y,z,chunkManager,random,c.getOrigin());
                }
                if(random.nextFloat() < 0.1){
                    chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x,y+1,z, c.getOrigin()), 14, c.getOrigin());
                }
                if(random.nextFloat() < 0.05){
                    chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x,y+1,z, c.getOrigin()), 13, c.getOrigin());
                }
            }
            else if(biome.equals(Biomes.TAIGA)){
                if(random.nextFloat() < biome.getDecorationRarity()){
                    Decorations.tree(TreeType.SNOWY_SPRUCE, x,y,z,chunkManager,random,c.getOrigin());
                }
            }

            else if(biome.equals(Biomes.DESERT)){
                if(random.nextFloat() < biome.getDecorationRarity()){
                    int cactusHeight = random.nextInt(5) + 2;
                    for(int ty = 0; ty < cactusHeight; ty++){
                        chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x,y+ty,z, c.getOrigin()), 12, c.getOrigin());
                    }
                }
            }

            else if(biome.equals(Biomes.SWAMP)){
                if(random.nextFloat() < biome.getDecorationRarity()){
                    chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x,y+1,z, c.getOrigin()), 14, c.getOrigin());
                }
                if(random.nextFloat() < biome.getDecorationRarity()){
                    chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x,y+1,z, c.getOrigin()), 13, c.getOrigin());
                }
                if(random.nextFloat() < biome.getDecorationRarity()/100f){
                    Decorations.tree(TreeType.SPRUCE, x,y,z,chunkManager,random,c.getOrigin());
                }
            }

            else if(biome.equals(Biomes.MOUNTAINS)){
                if(random.nextFloat() < 0.01){
                    Decorations.tree(TreeType.OAK, x,y,z,chunkManager,random,c.getOrigin());
                }
                if(random.nextFloat() < 0.1){
                    chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x,y+1,z, c.getOrigin()), 14, c.getOrigin());
                }
                if(random.nextFloat() < 0.05){
                    chunkManager.addGlobalBlock(Coordinates.toGlobalBlockPosition(x,y+1,z, c.getOrigin()), 13, c.getOrigin());
                }
            }
        }
    }
}

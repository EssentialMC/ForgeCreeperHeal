package fr.eyzox.forgecreeperheal.reflection.transform;

import fr.eyzox.forgecreeperheal.reflection.ReflectionHelper;
import fr.eyzox.forgecreeperheal.reflection.ReflectionManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class WorldTransform {

    private static Method isValidMethod = ReflectionManager.getInstance().getMethod(World.class, "isValid", new Class<?>[]{BlockPos.class});

    private final World world;
    private final boolean isRemote;
    private final WorldInfo worldInfo;
    private Map<Chunk, ChunkTransform> cachedChunkTransform = new HashMap<Chunk, ChunkTransform>();


    public WorldTransform(World world) {
        this.world = world;
        this.isRemote = this.world.isRemote;
        this.worldInfo = this.world.getWorldInfo();
    }

    public void removeSilentBlockState(BlockPos pos, int flags) {

        final IBlockState newState = Blocks.AIR.getDefaultState();

        this.setBlockState(pos, newState, flags);
    }

    private boolean isValid(BlockPos pos) {
        return ((Boolean) ReflectionHelper.call(world, isValidMethod, pos)).booleanValue();
    }

    private Chunk getChunkFromBlockCoords(BlockPos pos) {
        return this.world.getChunkFromBlockCoords(pos);
    }

    private IBlockState getBlockState(BlockPos pos) {
        return this.world.getBlockState(pos);
    }

    /**
     * From {@link World#setBlockState(BlockPos, IBlockState, int)}
     * Don't call {@link Chunk#setBlockState(BlockPos, IBlockState)} but call {@link ChunkTransform#setBlockState(BlockPos, IBlockState)}
     *
     * @param pos
     * @param newState
     * @param flags
     */
    private boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        if (!this.isValid(pos)) {
            return false;
        } else if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
            return false;
        } else {
            Chunk chunk = this.getChunkFromBlockCoords(pos);
            Block block = newState.getBlock();

            net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
            if (this.world.captureBlockSnapshots && !this.isRemote) {
                blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(this.world, pos, flags);
                this.world.capturedBlockSnapshots.add(blockSnapshot);
            }
            IBlockState oldState = getBlockState(pos);
            int oldLight = oldState.getLightValue(this.world, pos);
            int oldOpacity = oldState.getLightOpacity(this.world, pos);

            /* ======= REMOVED =============
            IBlockState iblockstate = chunk.setBlockState(pos, newState);
            ================================ */
            
            /* ======== ADDED =============== */
            ChunkTransform chunkTransform = this.cachedChunkTransform.get(chunk);
            if (chunkTransform == null) {
                chunkTransform = new ChunkTransform(chunk);
                this.cachedChunkTransform.put(chunk, chunkTransform);
            }

            IBlockState iblockstate = chunkTransform.setBlockState(pos, newState);
            /* =============================== */

            if (iblockstate == null) {
                if (blockSnapshot != null) this.world.capturedBlockSnapshots.remove(blockSnapshot);
                return false;
            } else {
                if (newState.getLightOpacity(this.world, pos) != oldOpacity || newState.getLightValue(this.world, pos) != oldLight) {
                    this.world.theProfiler.startSection("checkLight");
                    this.world.checkLight(pos);
                    this.world.theProfiler.endSection();
                }

                if (blockSnapshot == null) // Don't notify clients or update physics while capturing blockstates
                {
                    this.world.markAndNotifyBlock(pos, chunk, iblockstate, newState, flags);
                }
                return true;
            }
        }
    }

}

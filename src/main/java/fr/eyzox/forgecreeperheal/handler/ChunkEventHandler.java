package fr.eyzox.forgecreeperheal.handler;

import fr.eyzox.forgecreeperheal.ForgeCreeperHeal;
import fr.eyzox.forgecreeperheal.healer.Healer;
import fr.eyzox.forgecreeperheal.healer.HealerManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChunkEventHandler implements IEventHandler {

    private static final String FCH_TAG = "FCHTAG";
    private static final String HEALER_TAG = "HEALER";

    @SubscribeEvent
    public void onChunkDataLoad(final ChunkDataEvent.Load event) {
        if (event.getWorld().isRemote) return;

        final NBTTagCompound FCHTag = event.getData().getCompoundTag(FCH_TAG);
        if (!FCHTag.hasNoTags()) {
            final NBTTagCompound healerTag = FCHTag.getCompoundTag(HEALER_TAG);
            if (!healerTag.hasNoTags()) {

                final Healer healer = new Healer(event.getChunk());
                healer.deserializeNBT(healerTag);

                healer.setLoaded(true);

                ForgeCreeperHeal.getHealerManager((WorldServer) event.getWorld()).getLoadedHealers().put(ChunkPos.asLong(event.getChunk().xPosition, event.getChunk().zPosition), healer);
            }
        }
    }


    @SubscribeEvent
    public void onChunkDataSave(final ChunkDataEvent.Save event) {
        if (event.getWorld().isRemote) return;

        final HealerManager manager = ForgeCreeperHeal.getHealerManager((WorldServer) event.getWorld());

        final Healer healer = manager.getLoadedHealers().get(ChunkPos.asLong(event.getChunk().xPosition, event.getChunk().zPosition));

        if (healer != null) {

            final NBTTagCompound healerTag = healer.serializeNBT();

            final NBTTagCompound FCHTag = event.getData().getCompoundTag(FCH_TAG);
            FCHTag.setTag(HEALER_TAG, healerTag);
            event.getData().setTag(FCH_TAG, FCHTag);

            //If chunk is unloaded, unhandle its healer
            if (!healer.isLoaded()) {
                manager.getLoadedHealers().remove(ChunkPos.asLong(event.getChunk().xPosition, event.getChunk().zPosition));
            }
        }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getWorld().isRemote) return;
        final HealerManager manager = ForgeCreeperHeal.getHealerManager((WorldServer) event.getWorld());

        final Healer healer = manager.getLoadedHealers().get(ChunkPos.asLong(event.getChunk().xPosition, event.getChunk().zPosition));
        if (healer != null) {
            healer.setLoaded(false);
        }

    }

    @Override
    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }

}

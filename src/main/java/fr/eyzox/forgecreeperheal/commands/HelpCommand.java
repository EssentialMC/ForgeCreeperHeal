package fr.eyzox.forgecreeperheal.commands;

import fr.eyzox.forgecreeperheal.exception.ForgeCreeperHealCommandException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Iterator;
import java.util.List;

public class HelpCommand extends ForgeCreeperHealCommands {

    private final static WrappedVanillaHelpCommand wrap = new WrappedVanillaHelpCommand();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    protected String getFCHCommandName() {
        return "help";
    }

    @Override
    protected String getHelp() {
        return "fch.command.help.help";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return wrap.getRequiredPermissionLevel();
    }

    @Override
    protected void _execute(MinecraftServer server, ICommandSender sender, String[] args) throws ForgeCreeperHealCommandException {
        try {
            wrap.execute(server, sender, args);
        } catch (CommandException e) {
            throw new ForgeCreeperHealCommandException(sender, e);
        }
    }

    @Override
    protected String getFCHUsage() {
        return "";
    }

    private static final class WrappedVanillaHelpCommand extends CommandHelp {
        @Override
        protected List<ICommand> getSortedPossibleCommands(ICommandSender sender, MinecraftServer server) {
            final List<ICommand> possibleCommands = super.getSortedPossibleCommands(sender, server);
            for (Iterator<ICommand> it = possibleCommands.iterator(); it.hasNext(); ) {
                ICommand c = it.next();
                if (!(c instanceof ForgeCreeperHealCommands)) {
                    it.remove();
                }
            }
            return possibleCommands;
        }
    }

}

package fr.eyzox.bsc.config.loader;

import fr.eyzox.bsc.config.Config;
import fr.eyzox.bsc.exception.InvalidValueException;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;

public abstract class AbstractFileConfigLoader implements IConfigLoader {

    private final File file;
    private final IErrorManager errorManager = new FileErrorManager();

    AbstractFileConfigLoader(final File file) {
        this.file = file;
    }

    @Override
    public void load(Config config) throws IOException, InvalidValueException {
        if (!file.exists()) {
            throw new NoSuchFileException(file.getAbsolutePath());
        }

        if (!file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath() + "is not a file");
        }

        if (!file.canRead()) {
            throw new AccessDeniedException(file.getAbsolutePath() + "must allow reading");
        }
        errorManager.getErrors().clear();
    }

    @Override
    public void save(Config config) throws IOException {
        if (file.exists()) {
            if (!file.isFile()) {
                throw new FileNotFoundException(file.getAbsolutePath() + " is not a file");
            }

            if (!file.canWrite()) {
                throw new AccessDeniedException(file.getAbsolutePath() + " must allow writing");
            }
        } else if (!file.getParentFile().canWrite()) {
            throw new AccessDeniedException(file.getParentFile().getAbsolutePath() + " must allow writing");
        }
    }

    public File getFile() {
        return file;
    }

    @Override
    public IErrorManager getErrorManager() {
        return errorManager;
    }

    protected class FileErrorManager extends ErrorManager {
        @Override
        public void output(PrintWriter out) throws IOException {
            super.output(out);
            out.println();
            out.println("-------- ORIGINAL FILE --------");
            out.println();
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
                String s = null;
                while ((s = in.readLine()) != null) {
                    out.println(s);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
    }

}

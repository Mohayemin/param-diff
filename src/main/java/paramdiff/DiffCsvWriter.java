package paramdiff;

import java.io.FileWriter;
import java.io.IOException;

public class DiffCsvWriter {
    private FileWriter writer;

    public DiffCsvWriter(FileWriter writer) {
        this.writer = writer;
    }

    public void writeHeader() throws IOException {
        writer.write("Commit SHA,Java File,Old function signature,New function signature\n");
    }

    public void writeDiff(String hash, String filePath, ParamAdditionDiff diff) throws IOException {
        writer.write(String.format("%s,%s,\"%s\",\"%s\"\n", hash, filePath,
                diff.oldMethod.getSignature().asString(),
                diff.newMethod.getSignature().asString()));
    }
}

package paramdiff;

import java.io.FileWriter;
import java.io.IOException;

public class DiffCsvWriter {
    private FileWriter writer;
    private int rowsCount;

    public DiffCsvWriter(FileWriter writer) {
        this.writer = writer;
        rowsCount = 0;
    }

    public void writeHeader() throws IOException {
        writer.write("Commit SHA,Java File,Old function signature,New function signature\n");
        this.writer.flush();
    }

    public void writeDiff(String hash, String filePath, ParamAdditionDiff diff) throws IOException {
        writer.write(String.format("%s,%s,\"%s\",\"%s\"\n", hash, filePath,
                diff.oldMethod.getSignature().asString(),
                diff.newMethod.getSignature().asString()));
        rowsCount++;

        if (rowsCount % 10 == 0) {
            this.writer.flush();
        }
    }
}

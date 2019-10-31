package paramdiff.git;

import util.Command;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Revision {
    private Repository repository;
    public final String hash;

    public Revision(Repository repository, String hash) {
        this.repository = repository;
        this.hash = hash;
    }

    public List<String> getChangedJavaFiles() throws IOException {
        var commandText = String.format("git diff \"%s^!\" --name-only", hash);
        var filePaths = new Command(commandText)
                .run(repository.localFile)
                .readLines().stream()
                .filter(fp -> fp.endsWith(".java"))
                .collect(Collectors.toList());

        return filePaths;
    }

    public String readFile(String filePath) throws IOException {
        String commandText = String.format("git show \"%s:%s\"", hash, filePath);
        var lines = new Command(commandText)
                .run(repository.localFile)
                .readLines();

        var content = String.join("\n", lines);
        return content;
    }

    public boolean isMerge() throws IOException {
        // this command shows parents revisions of a revision, separated by space
        var commandText = String.format("git log --pretty=%%P -n 1 \"%s\"", hash);
        var output = new Command(commandText).run(repository.localFile).readLines().get(0);
        return output.contains(" ");
    }

    public Revision getParent(){
        return new Revision(repository,hash + "^");
    }
}

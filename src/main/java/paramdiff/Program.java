package paramdiff;

import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        try {
            findDiffsForLocalRepo("src/test/resources/git_repo/mockito");
        } finally {
            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;

            System.out.println("DONE");
            System.out.println(totalTime);
        }
    }

    private static void findDiffsForLocalRepo(String repositoryPath) throws IOException {
        var gitReader = new GitReader(repositoryPath);
        var hashes = gitReader.getAllHashes();

        int count = 0;
        for (var hash : hashes) {
            var changedFilePaths = gitReader.getChangedJavaFiles(hash);
            if (changedFilePaths.size() > 0)
                System.out.println("\n\n" + count + ":" + hash);

            for (var changedFilePath : changedFilePaths) {
                var newFileContent = gitReader.readFile(hash, changedFilePath);
                var oldFileContent = gitReader.readFile(hash + "^", changedFilePath);

                var paramDiffFinder = new ParamDiffFinder();

                var changes = paramDiffFinder.findParamAddition(oldFileContent, newFileContent);
                if (changes.size() > 0)
                    System.out.println("\n" + changedFilePath);

                for (var change : changes) {
                    System.out.println(change);
                }
            }

            count++;
        }
    }
}

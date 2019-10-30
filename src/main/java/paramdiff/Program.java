package paramdiff;

import paramdiff.logger.NanoLogger;
import paramdiff.logger.TimeLogger;

import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        try {
            findDiffsForLocalRepo("src/main/resources/git_repo/spring-analysis", new NanoLogger());
        } finally {
        }
    }

    private static void findDiffsForLocalRepo(String repositoryPath, TimeLogger logger) throws IOException {
        logger.start();
        var gitReader = new GitReader(repositoryPath);
        var hashes = gitReader.getAllHashes();

        for (var hash : hashes) {
            logger.startLap();
            var changedFilePaths = gitReader.getChangedJavaFiles(hash);
            int changeCount = 0;

            for (var changedFilePath : changedFilePaths) {
                var newFileContent = gitReader.readFile(hash, changedFilePath);
                var oldFileContent = gitReader.readFile(hash + "^", changedFilePath);

                var paramDiffFinder = new ParamDiffFinder();

                var changes = paramDiffFinder.findParamAddition(oldFileContent, newFileContent);
                changeCount += changes.size();

                for (var change : changes) {
                    System.out.println(change);
                }
            }

            if (changeCount > 0)
                logger.logLap(hash + ":" + changeCount);
        }
    }
}

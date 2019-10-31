package paramdiff;

import paramdiff.git.Repository;
import paramdiff.git.GitReader;
import paramdiff.logger.NanoLogger;
import paramdiff.logger.TimeLogger;

import java.io.FileWriter;
import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        var repoName = args[0];
        var dataDirectory = args[1];
        var repoPath = dataDirectory + "/" + repoName;

        var csvPath = dataDirectory + "/" + repoName + ".csv";
        var csvWriter = new FileWriter(csvPath);
        var diffWriter = new DiffCsvWriter(csvWriter);
        try {
            if (args.length > 2) {
                var gitUrl = args[2];
                new Repository(gitUrl, repoPath).update();
            }
            findDiffsForLocalRepo(repoPath, diffWriter, new NanoLogger());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
    }

    private static void findDiffsForLocalRepo(String repositoryPath,
                                              DiffCsvWriter diffWriter, TimeLogger logger) throws IOException {
        logger.start();
        diffWriter.writeHeader();
        var gitReader = new GitReader(repositoryPath);
        var hashes = gitReader.getAllHashes();

        for (int i = 0; i < hashes.size(); i++) {
            var hash = hashes.get(i);
            logger.startLap();
            var changedFilePaths = gitReader.getChangedJavaFiles(hash);
            int changeCount = 0;

            for (var changedFilePath : changedFilePaths) {
                var newFileContent = gitReader.readFile(hash, changedFilePath);
                var oldFileContent = gitReader.readFile(hash + "^", changedFilePath);

                var paramDiffFinder = new ParamDiffFinder();

                var diffs = paramDiffFinder.findParamAddition(oldFileContent, newFileContent);
                changeCount += diffs.size();

                for (var diff : diffs) {
                    diffWriter.writeDiff(hash, changedFilePath, diff);
                }
            }

            logger.logLap(String.format("%4d:%s:%3d", i, hash, changeCount));
        }

        logger.logTotal("Complete");
    }
}

package paramdiff;

import paramdiff.git.Repository;
import paramdiff.git.Revision;
import paramdiff.logger.NanoLogger;
import paramdiff.logger.TimeLogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Program {
    public static void main(String[] args) throws IOException {
        var repoName = args[0];
        var dataDirectory = args[1];
        var repoPath = new File(dataDirectory + "/" + repoName);

        var csvPath = String.format("%s/%s_%d.csv", dataDirectory, repoName, System.currentTimeMillis());
        var csvWriter = new FileWriter(csvPath);
        var diffWriter = new DiffCsvWriter(csvWriter);
        try {
            var gitUrl = "";
            if (args.length > 2) {
                gitUrl = args[2];
            }
            var repository = new Repository(gitUrl, repoPath).update();
            findDiffsForLocalRepo(repository, diffWriter, new NanoLogger());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
    }

    private static void findDiffsForLocalRepo(Repository repository,
                                              DiffCsvWriter diffWriter, TimeLogger logger) throws IOException {
        logger.start();
        diffWriter.writeHeader();
        var revisions = repository.getAllRevisions().collect(Collectors.toList());

        var totalCompleted = 0;
        var skippedMerge = 0;
        var filesProcessed = 0;
        var totalDiffsFound = 0;

        logger.startLap();
        for (var revision : revisions) {
            totalCompleted++;
            if (revision.isMerge()) {
                skippedMerge++;
                continue;
            }

            var parentRevision = revision.getParent();
            var changedFilePaths = revision.getChangedJavaFiles();
            filesProcessed += changedFilePaths.size();

            for (var changedFilePath : changedFilePaths) {
                var diffs = findDiffsInFile(revision, parentRevision, changedFilePath);
                totalDiffsFound += diffs.size();

                for (var diff : diffs) {
                    diffWriter.writeDiff(revision.hash, changedFilePath, diff);
                }
            }

            if (totalCompleted % 100 == 0) {
                var message = String.format("%5d/%d revisions, %4d merges skipped, %6d files processed, %5d target changes found",
                        totalCompleted, revisions.size(), skippedMerge, filesProcessed, totalDiffsFound);
                logger.logLap(message);
            }
        }

        var message = String.format("%5d/%d revisions, %4d merges skipped, %6d files processed, %5d target changes found",
                totalCompleted, revisions.size(), skippedMerge, filesProcessed, totalDiffsFound);

        logger.logTotal("Complete:" + message);
    }

    private static List<ParamAdditionDiff> findDiffsInFile(Revision revision, Revision parentRevision, String changedFilePath)
            throws IOException {
        var newFileContent = revision.readFile(changedFilePath);
        var parentFileContent = parentRevision.readFile(changedFilePath);

        var paramDiffFinder = new ParamDiffFinder();

        var diffs = paramDiffFinder.findParamAddition(parentFileContent, newFileContent);

        return diffs;

    }
}

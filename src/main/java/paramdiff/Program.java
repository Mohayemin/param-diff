package paramdiff;

import paramdiff.git.Repository;
import paramdiff.git.Revision;
import util.Stopwatch;

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
            findDiffsForLocalRepo(repository, diffWriter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
    }

    private static void findDiffsForLocalRepo(Repository repository,
                                              DiffCsvWriter diffWriter) throws IOException {
        System.out.printf("Started processing repository at %s\n", repository.localFile.getAbsolutePath());

        var stopwatch = new Stopwatch().start();;
        diffWriter.writeHeader();
        var revisions = repository.getAllRevisions().collect(Collectors.toList());

        var totalRevisions = revisions.size();
        var totalCompleted = 0;
        var skippedMerge = 0;
        var filesProcessed = 0;
        var totalDiffsFound = 0;

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
                logUpdate(stopwatch, totalRevisions, totalCompleted, skippedMerge, filesProcessed, totalDiffsFound);
            }
        }

        logUpdate(stopwatch, totalRevisions, totalCompleted, skippedMerge, filesProcessed, totalDiffsFound);
    }

    private static void logUpdate(Stopwatch stopwatch, int totalRevisions, int totalCompleted, int skippedMerge, int filesProcessed, int totalDiffsFound) {
        var message = String.format("%5d/%d revisions, %4d merges skipped, %6d files processed, %5d target changes found, %.2f seconds",
                totalCompleted, totalRevisions, skippedMerge, filesProcessed, totalDiffsFound, stopwatch.elapsedNanos() / 1e9);
        System.out.println(message);
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

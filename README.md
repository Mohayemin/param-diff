# About
ParamDiff is a tool that mines a given Java GitHub code repository. The program analyses all the commits in that repository and finds commits that have added a parameter to an existing method.

The tool generates a CSV file with columns  `Commit SHA`, `Java File`, `Old function signature`, `New function signature`. 

# How to run
ParamDiff is an executable jar file that requires Java 12. [Click here to download it](https://github.com/Mohayemin/param-diff/releases/download/v1.0/ParamDiff.jar). This can be run with `java -jar` command. Here is an example:  
```
java -jar ParamDiff.jar -url https://github.com/seaswalker/spring-analysis
```
This will clone the repository from the given URL, traverse all the revisions and generate the output CSV. "Sprint analysis" repository used in the example is a small one (89 commits on October 31, 2019) therefore is good for the first test run.

Here are the details of all supported command parameters:
- `-url`: URL of the git repository.
- `-outdir`: Directory for program output. Defaults to `out`. If the directory does not exist, the program will automatically create it.
- `-rn`: Repository name. A short name of the repository.

The `outdir` parameter is optional. However, at least one of the `url` and `rn` parameters must be specified. If `rn` is not specified, this will be the last part of the `url` parameter.


# Output

1. **Repository**: If there is no repository in `rn` subdirectory of the `outdir` directory, the tool will clone the repository from `url` into `rn`. If the repository already exists at `rn`, the tool will pull it instead of cloning. Also, if repository exists locally and the `url` argument is not passed, the current state of the local repository will be used.
2. **CSV**: Output is written to a CSV file inside the `out-dir`. Name of the file is `<rn>_<timestamp>.csv`. A new file will be generated every time the tool is run for a repository.
3. **Console output:** Progress is reported in the console for every 100 revisions processed. 


# Results
The tool has been run on the following Java git repositories:

|   | Project  | Revisions  | Processed Files | Param Addition Found |
| --:| :-------| :------:| :-:| :-: |
| 1 | [Mockito](https://github.com/mockito/mockito)             |  5.2K | 14.5K | 101 |
| 2 | [RxJava](https://github.com/ReactiveX/RxJava)              |  5.6K | 24.9K | 104 |
| 3 | [Elastic Search](https://github.com/elastic/elasticsearch)| 49.0K | - |
| 4 | [Book Keeper](https://github.com/apache/bookkeeper)       |  2.2K | 10.5K | 328 |
| 5 | [Camel Quarkus](https://github.com/apache/camel-quarkus)  |  504 | 616 | 21 |

The results of the projects can be found in the `results` directory of the repository.

# How it works
To be considered as an addition of parameter, the new version must have all the parameters of the previous version in *same sequence*, and there must be at least one additional parameter.  
The tool will consider the followings changes as addition of parameter:  
1. `func(int) -> func(int, int)`  
2. `func(int) -> func(int, int)`
3. `func(int) -> func(int, int)`   
4. `func(int, String, double) -> func(String, int, String, float, double)`

The followings are not considered as an addition of parameter
1. `func(int) -> func(float)`
2. `func(int, float) -> func(float, int, String)`. It may seem like an addition of parameter, but I have ignored this case.


I have excluded any merge revisions from processing. This is because generally any change that is found in a merge commit is also found earlier in a non-merge commit, where the developer actually made the change. Therefore considering merge would yield duplicate results.  

# Scopes of improvement
The tool parses the full modified file in a revision and compares it with its parent. Performance can be improved by only parsing the lines which were changed. 
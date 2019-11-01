# How to run
Java 12 is needed to run the tool. Download the executable jar from here and run the following command  
`java -jar ParamDiff.jar "<repo-name>" "<data-dir>" "<repo-url>"`  
* `data-dir` is the folder for repositories and output CSVs. 
* `repo-name` is a short name of the repository.
* `repo-url` is the URL of the git repository. This argument is optional.


**Example:**  
`java -jar ParamDiff.jar "spring-analysis" "C:\ParamDiff" "https://github.com/seaswalker/spring-analysis"`  
Sprint analysis is a small repository (89 commits on October 31, 2019) and good for first test run.

# Output
1. **Repository**: The repository is cloned from `repo-url` into `data-dir/repo-name` folder. If the repository already exists in the destination, it will pull it instead of cloning. Also, if repository exists localy and the `repo-url` argument is ignored, the current state of the local repository will be used.
2. **CSV**: Output is writen to a CSV file inside the `data-dir`. Name of the file is `repo-name_<timestamp>.csv`. A new file will be generated everytime the tool is run for the repository.
3. **Console output:** Progress is reported in the console for every 100 revisions processed. 


# Results
The tool has been run on the following Java git repositories

|   | Project  | Revisions  | Processed Files | Param Addition Found |
| --:| :-------| :------:| :-:| :-: |
| 1 | [Mockito](https://github.com/mockito/mockito)             |  5.2K | 14.5K | 101 |
| 2 | [RxJava](https://github.com/ReactiveX/RxJava)              |  5.6K | 24.9K | 104 |
| 3 | [Elastic Search](https://github.com/elastic/elasticsearch)| 49.0K | - |
| 4 | [Book Keeper](https://github.com/apache/bookkeeper)       |  2.2K | 10.5K | 328 |
| 5 | [Camel Quarkus](https://github.com/apache/camel-quarkus)  |  504 | 616 | 21 |

The results of the projects can be found in results directory of the repository.

# How it works
To be considered as a addition of parameter, the new version must have all the parameters of previous version in *same sequence*, and there must be at least one additional parameter.  
The tool will consider the followings changes as addition of parameter:  
1. `func(int) -> func(int, int)`  
2. `func(int) -> func(int, int)`
3. `func(int) -> func(int, int)`   
4. `func(int, String, double) -> func(String, int, String, float, double)`

The followings are not considered addition of parameter
1. `func(int) -> func(float)`
2. `func(int, float) -> func(float, int, String)`. It may seem like addition of parameter, but I have ignored this case.


I have excluded amy merge revisions from processing. This is because generally any change that is found in a merge commit is also found earlier in a non merge commit, where developer actually made the change. Therefore considering merge would yield duplicate results.  

# Scope improvement
I parse the full modified file in a revision and compare it with it's parent. Performance can be improved by only parsing the lines which were changed. 
# ngram-generator
N-gram generation by tree or list

## Description

The program allows for a set of files with trees or lists to generate n-grams (unigrams, bigrams and 3-grams).

Also you can specify max distance between neighboring nodes (by default is 0). Max distance is max number of nodes, which can stand between n-grams nodes.

Max distance is specified [here](https://github.com/PetukhovVictor/ngram-generator/blob/master/src/main/kotlin/org/jetbrains/ngramgenerator/Runner.kt#L22) (for generating by tree) and [here](https://github.com/PetukhovVictor/ngram-generator/blob/master/src/main/kotlin/org/jetbrains/ngramgenerator/Runner.kt#L41) (for generating by list).

### N-grams generation by tree

Bulding of n-grams on the tree occurs via depth-first search and storing the path of the walk. On the basis of the stored path, a set of n-grams is built at each node in which it contained.

Example of n-grams generation by tree (without intermediate nodes - max distance = 0):

![Tree n-grams generation](https://github.com/PetukhovVictor/ngram-generator/raw/master/images/tree_ngrams.png)

Green color is 3-gram, orange is bigram and purple is unigrams.

It is Kotlin PSI tree (or CST - concrete syntax tree).

**Tree format**

Tree must be an array, which contains elements with two fields: `type` and `children` (not required).
Field `type` is used by algorithm to generating n-grams.

Example:
```json
[
   {
      "type":"FILE",
      "children":[
         {
            "type":"PACKAGE_DIRECTIVE",
            "children":[
               {
                  "type":"package"
               },
               {
                  "type":"WHITE_SPACE"
               }
            ]
         },
         {
            "type":"WHITE_SPACE"
         },
         {
            "type":"IMPORT_LIST",
            "children":[
               {
                  "type":"IMPORT_DIRECTIVE",
                  "children":[
                     {
                        "type":"import"
                     },
                     {
                        "type":"WHITE_SPACE"
                     },
                     {
                        "type":"DOT_QUALIFIED_EXPRESSION",
                        "children":[
                           {
                              "type":"DOT_QUALIFIED_EXPRESSION",
                              "children":[
                                 {
                                    "type":"REFERENCE_EXPRESSION",
                                    "children":[
                                       {
                                          "type":"IDENTIFIER"
                                       }
                                    ]
                                 },
                                 {
                                    "type":"DOT"
                                 },
                                 {
                                    "type":"REFERENCE_EXPRESSION"
                                    "children":[
                                       {
                                          "type":"IDENTIFIER"
                                       }
                                    ]
                                 }
                              ]
                           },
                           {
                              "type":"DOT"
                           },
                           {
                              "type":"REFERENCE_EXPRESSION"
                              "children":[
                                 {
                                    "type":"IDENTIFIER"
                                 }
                              ]
                           }
                        ]
                     }
                  ]
               },
               {
                  "type":"WHITE_SPACE"
               },
               {
                  "type":"WHITE_SPACE"
               }
            ]
         },
         {
            "type":"WHITE_SPACE"
         },
         {
            "type":"CLASS",
            "children":[
               {
                  "type":"KDoc"
               }
            ]
         }
      ]
   }
]
```

### N-grams generation by list

N-gramms are generated from the list by simple walk (forEach) and storing of history walk (one and two previous elements).

Example of n-grams generation by list (without intermediate nodes - max distance = 0):

![Tree n-grams generation](https://github.com/PetukhovVictor/ngram-generator/raw/master/images/list_ngrams.png)

Green color is 3-gram, orange is bigram and purple is unigrams.

It is JVM inctruction list (parsed bytecode).

**List format**

List must be grouped list (array of arrays) of strings. Groups do not participate in the generation of n-grams and merge into one array.

Such a format was chosen for the initial task — to generate n-grams by a set of JVM-instructions grouped by methods.

Example:
```json
{
	"\u003cclinit\u003e": ["iconst_1","anewarray","dup","iconst_0","new","dup","ldc","invokestatic","ldc","ldc","invokespecial","invokestatic","checkcast","aastore","putstatic","new","dup","aconst_null","invokespecial","putstatic","return"],
	"\u003cinit\u003e": ["aload_0","invokespecial","aload_0","new","dup","aload_0","invokespecial","checkcast","invokestatic","putfield","return"],
	"setFirstRun": ["aload_0","iload_1","putfield","return"],
	"getDefaultDPreference": ["aload_0","getfield","astore_1","getstatic","iconst_0","aaload","astore_2","aload_1","invokeinterface","checkcast","areturn"],
	"getFirstRun": ["aload_0","getfield","ireturn"],
	"onCreate": ["iconst_0","istore_1","aload_0","invokespecial","aload_0","invokestatic","astore_2","aload_2","ldc","invokestatic","aload_2","ldc","iconst_0","invokeinterface","bipush","if_icmpeq","iconst_1","istore_1","aload_0","iload_1","putfield","aload_0","getfield","ifeq","aload_0","invokestatic","astore_2","aload_2","ldc","invokestatic","aload_2","invokeinterface","ldc","bipush","invokeinterface","invokeinterface","getstatic","aload_0","invokevirtual","return"]
}
```

### Program arguments

* `-i` or `--input`: path to folder with structure JSON files (trees or lists);
* `-o` or `--output`: path to folder, in which will be written files with generated n-grams;
* `--tree`: whether to generate by tree;
* `--list`: whether to generate by list.

### How to run

To run program you must run `main` function in `main.kt`, not forgetting to set the program arguments.

Also you can run jar file (you can download from the [release assets](https://github.com/PetukhovVictor/ngram-generator/releases)):
```
java -jar ./ngram-generator-0.1.1.jar -i ./trees -o ./trees_factorized --tree
```

# ngram-selector

N-grams selection via Ends and Derivative bounds selectors using n-gram frequncy information.

Unlike the [old version](https://github.com/PetukhovVictor/ngram-list-selector), this allows also to select from all files of the specified folder (not only in the specified n-gram list).

## Program arguments

- `-i`, `--input`: path to folder with files for n-gram selection;
- `-o`, `--output`: path to folder, in which will be written files with selected n-grams;
- `--all_ngrams_file`: path to all n-grams map file.

## Supported n-gram selectors

### Ends selector

This selector can select n-grams from head or tail of n-grams with sorting by frequncy.

#### Types
- `head` (set [here](https://github.com/PetukhovVictor/ngram-selector/blob/master/src/main/kotlin/org/jetbrains/ngramselector/Runner.kt#L27)): cut features from head n-grams list with sorting by frequncy;
- `tail` (set [here](https://github.com/PetukhovVictor/ngram-selector/blob/master/src/main/kotlin/org/jetbrains/ngramselector/Runner.kt#L28)): cut features from tail n-grams list with sorting by frequncy.

#### Parameters
- `by = 'value'`: n-gram selection with specified value bound after or before which n-grams will be selected;
- `by = 'order'`: same, but order bound;
- `bound`: value or order bound after or before which n-grams will be selected.

### Derivative bounds selector

This selector can clipping n-grams whose values mapped to the values of the derivative, which do not exceed a specified distance from the specified point ( e.g. tan(Pi / 4) ).

You can set it [here](https://github.com/PetukhovVictor/ngram-selector/blob/master/src/main/kotlin/org/jetbrains/ngramselector/Runner.kt#L29).

#### Parameters
- `point`: the point from which the deviation of the derivative will be calculated;
- `deviation` = max derivative deviation.

## Input data

Program required n-grams map: "n-gram name" - "n-gram value (frequency)".

Example:
```json
{
   "RETURN:DOT_QUALIFIED_EXPRESSION:IDENTIFIER":47575,
   "THEN:DOT_QUALIFIED_EXPRESSION:IDENTIFIER":74185,
   "THEN:RETURN:IDENTIFIER":4111,
   "IF:RETURN:IDENTIFIER":4620,
   "RETURN:DOT":19444,
   "RETURN:DOT_QUALIFIED_EXPRESSION:DOT":34104,
   "THEN:DOT_QUALIFIED_EXPRESSION:DOT":46137,
   "RETURN:VALUE_ARGUMENT_LIST:REFERENCE_EXPRESSION":39958,
   "RETURN:VALUE_ARGUMENT_LIST:RPAR":22982,
   "RETURN:CALL_EXPRESSION:RPAR":27579,
   "THEN:RBRACE":33671,
   "IF:RBRACE":41584,
   "THEN:BLOCK:RBRACE":34530,
   "IF:BLOCK:RBRACE":42313,
   "BLOCK:BLOCK:RBRACE":55548,
   "BLOCK:RETURN:if":1468,
   "RETURN:IF:WHITE_SPACE":12403,
   "RETURN:LPAR":14068,
   "RETURN:IF:LPAR":1748,
   "BLOCK:RETURN:LPAR":14361,
   "RETURN:CONDITION":1442,
   "RETURN:IF:CONDITION":1738,
   "BLOCK:RETURN:CONDITION":1468
}
```

## Output data

Output is selected n-gram list and a set of files in the specified directory along the same paths with selected n-grams.

Example selected n-gram file:
```json
[
  "BLOCK:POSTFIX_EXPRESSION:SAFE_ACCESS_EXPRESSION",
  "BLOCK:POSTFIX_EXPRESSION:SAFE_ACCESS",
  "FUN:VALUE_ARGUMENT:IF",
  "PROPERTY:TYPE_PROJECTION:FUNCTION_TYPE",
  "PROPERTY:TYPE_PROJECTION:VALUE_PARAMETER_LIST",
  "PROPERTY:TYPE_PROJECTION:ARROW",
  "BINARY_EXPRESSION:VALUE_ARGUMENT_LIST:if",
  "BINARY_EXPRESSION:VALUE_ARGUMENT_LIST:CONDITION", 
  "BINARY_EXPRESSION:VALUE_ARGUMENT_LIST:THEN",
  "BINARY_EXPRESSION:VALUE_ARGUMENT_LIST:else",
]
```

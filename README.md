# ScalpelAndroid

Inspired from [Scalpel](https://github.com/sleuthkit/scalpel/).

## Supported File Formats
JPG, PNG, DOC, PDF, GIF, HTML, TIF, DOCX, PPTX, XLSX, DOC, ZIP, JAVA, MPG, and RTF.

## Optimizations Performed
* Usage of [RxJava](https://github.com/ReactiveX/RxJava)'s
[ParallelFlowable](https://github.com/ReactiveX/RxJava/wiki/Parallel-flows)
to parallelize on-disk reading.
* Usage of sliding window technique that takes into consideration header bytes amount and footer bytes amount to select the least-possible window to iterate over.
* Reading and writing of files on a parallel background thread to prevent locking of UI.
* Usage of Android's [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) architecture to prevent interruptions of the background carving process.

## Explanation of Code
* Each rule is stored in a local SQLite database table, via the Android ORM library called as
[Room](https://developer.android.com/topic/libraries/architecture/room).
* Rules have been statically defined [in the source code here](app/src/main/java/com/daksh/scalpelandroid/storage/room/dao/RuleDao.kt#L32), since it was not feasible to design a UI for input / editing / disabling rules given the time frame.
* The code for carving bytes from a source file [is here](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L65).
  * It first [reads the source file's bytes](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L245) once in the background.
  * Next, all rules are [read from RuleDao](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L84)
  * Processing for each rule [happens in parallel](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L89) on the disk background thread.
  * [This code converts header and footer bytes](app/src/main/java/com/daksh/scalpelandroid/extensions/StringExtensions.kt#L3) of each rule from their String encodings into a List of Bytes.
  * To match the header and footer, each possible window is checked.
  * Steps for matching the header:
    * Each window of the same size as `headerBytes` is [checked for matching](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L106) with the rule's header bytes, which may include wildcards.
    * A [list of possible carvings is generated](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L115), based on the footer's presence and footer bytes.
  * Steps for matching the footer:
    * For [each appropriate window](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L142) for matching the footer, it is matched with the rule's [footer format](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L145) (which may include wildcards).
    * The list of the filtered footer indices is [reversed if necessary](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L151), and mapped to the [target file's carve size](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L157) from the header's beginning offset.
    * A [minimum carve size is ensured](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L168), and then mapped to a [Byte Array from 0 to that carve size](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L173).
  * After matching of the header and footer, each filtered-out [piece of bytes is saved to disk](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L178).

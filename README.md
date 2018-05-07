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
* Unit Tests have been written for checking code for [converting a Scalpel-formatted String to a List of Bytes](app/src/test/java/com/daksh/scalpelandroid/extensions/StringToScalpelBytesTest.kt#L8), and for [matching wildcards against a target Byte array](app/src/test/java/com/daksh/scalpelandroid/extensions/MatchWildCardTest.kt#L8).
* [`CarveActivity`](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveActivity.kt) is the main screen of the app.
* The ViewModel to control `CarveActivity` is [located in `CarveViewModel`](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt).
* The UI for `CarveActivity` is [defined here](app/src/main/res/layout/carve_activity.xml).
* All files for [Dependency Injection](https://en.wikipedia.org/wiki/Dependency_injection) via [Google's Dagger 2](https://github.com/google/dagger) are located [in this package](app/src/main/java/com/daksh/scalpelandroid/inject).
* [`CarvedFilesDiffUtilCallback`](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarvedFilesDiffUtilCallback.kt) is a RecylerView callback class that compares and old and a new list, and updates the list UI accordingly based on additions / changes / deletions.
* [`CarvedFilesListAdapter`](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarvedFilesListAdapter.kt) controls the list on `CarveActivity`.
* Each click on a row item of the list on `CarveActivity` [opens any file in an external app installed on the phone using this code](app/src/main/java/com/daksh/scalpelandroid/storage/FileOpener.kt#L13). The MIME Type of the file is [determined here](app/src/main/java/com/daksh/scalpelandroid/storage/FileOpener.kt#L26).
* The challenge for opening our own app's files in an external browser on Android SDK >= 24 (i.e., Nougat (7.0) or more recent) was to implement a [FileProvider](https://developer.android.com/reference/android/support/v4/content/FileProvider), since now on, apps cannot give other apps access to arbitrary files.
* To read / write to the app's storage, Android >= 5.0 requires [requesting runtime permissions to the user](https://developer.android.com/training/permissions/requesting). This was done by utilizing the [Dexter Android library](https://github.com/Karumi/Dexter) which makes it easier.
* To pick a source file to carve, the [MaterialFilePicker Android library](https://github.com/nbsp-team/MaterialFilePicker) was used.
* 

## Carving Process
  * The app first [reads the source file's bytes](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L245) once in the background.
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
  * If there were [no footer matches](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L185), but at least one header match, then each of those header-to-max-carve-size byte arrays are saved to disk, depending on [if `forceSave` for that rule is `true`](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L186).
  * Each created file is [appended to a List](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L211), and that List is used to [update the UI](app/src/main/java/com/daksh/scalpelandroid/screens/carve/CarveViewModel.kt#L212).

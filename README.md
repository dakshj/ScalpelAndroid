# ScalpelAndroid

An Android app written natively in Kotlin, inspired by [Scalpel](https://github.com/sleuthkit/scalpel/) and [Foremost](http://foremost.sourceforge.net/). This app picks a source file to carve from, and carves files of the formats mentioned below within it, in parallel using `RxJava`. This is a project for the [UMass CICS CS590F Spring 2018 course](http://people.cs.umass.edu/~liberato/courses/2018-spring-compsci365+590f/).

## Supported File Formats
JPG, PNG, DOC, PDF, GIF, HTML, TIF, DOCX, PPTX, XLSX, DOC, PPT, XLS, ZIP, JAVA, MPG, and RTF.

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
* The selected file is stored [within the app's SharedPreferences](https://developer.android.com/reference/android/content/SharedPreferences) using the code in [`AppSettingsImpl`](app/src/main/java/com/daksh/scalpelandroid/storage/prefs/AppSettingsImpl.kt).
* All database and ORM related files are located [in this package](app/src/main/java/com/daksh/scalpelandroid/storage/room).
* The directories used by the app to store the carved files are created using [the code in `DirectoryManager`](app/src/main/java/com/daksh/scalpelandroid/storage/DirectoryManager.kt).
* The complete list of libraries and dependencies used in this project, is located in [the `app` module's build.gradle file here](app/build.gradle#L55).

## The Carving Process
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
  
## Building and Running the Project
Install [Android Studio 3.1 or above](https://developer.android.com/studio/) and clone this repository. After the initial Gradle sync completes, use the run options from the `Run` menu to launch the app on either an emulator or a connected Android device.

## Testing the Project
* I have written Unit Tests for the core byte-related functions for [converting a Scalpel-formatted String to a List of Bytes](app/src/test/java/com/daksh/scalpelandroid/extensions/StringToScalpelBytesTest.kt#L8), and for [matching wildcards against a target Byte array](app/src/test/java/com/daksh/scalpelandroid/extensions/MatchWildCardTest.kt#L8)
* Further testing is only possible using [Instrumentation Testing on an Android device](https://developer.android.com/training/testing/unit-testing/instrumented-unit-tests); but that requires a significant amount of time, and I was not able to reach to it.
* However, I have done manual tests using various documents and image files. The files I used are here:
  * [`L0_Graphic.dd`](https://www.cfreds.nist.gov/FileCarving/Images/L0_Graphic.dd.bz2), [`L0_Documents.dd`](https://www.cfreds.nist.gov/FileCarving/Images/L0_Documents.dd.bz2), and [`L1_Documents.dd`](https://www.cfreds.nist.gov/FileCarving/Images/L1_Documents.dd.bz2) [located here](https://www.cfreds.nist.gov/FileCarving/index.html).
  * [`Designs.doc`](http://people.cs.umass.edu/~liberato/courses/2018-spring-compsci365+590f/files/Designs.doc) picked from [UMass CICS Spring 2018 CS590F's Assignment 5](http://people.cs.umass.edu/~liberato/courses/2018-spring-compsci365+590f/assignments/05-jpeg-and-exif/).
  * `image.ntfs` and `simple.ntfs` picked from [UMass CICS Spring 2018 CS590F's Assignment 10](http://people.cs.umass.edu/~liberato/courses/2018-spring-compsci365+590f/assignments/10-istat-ntfs/).
* Two test videos showing the app are below:
  * [Carve Designs.doc.mp4](https://drive.google.com/file/d/1nXAzAJSJCJAPQw5OQtc6rMII3awScH-6/view?usp=sharing)
  * [Carve L0_Documents.dd.mp4](https://drive.google.com/file/d/1OZzhoRzMlJJslc7eQ6W4D2A0LiRRWCIw/view?usp=sharing)
* You can test out the app yourself by [installing the APK file from here](https://github.com/dakshj/ScalpelAndroid/releases/tag/1.0).

## Location of Carved Files on Android Device's Storage
All carve "runs" are stored in separate folders (named on the current timestamp of when the run was started) within the `ScalpelAndroid` folder within the root directory of the storage media. Within each run folder, carved files are stored into separate folders based on their file extension.

Example layout:
```
sdcard
  |
  |_____ ScalpelAndroid
                |
                |_____ 2018-05-06T18:28:14.925
                                  |
                                  |_____ doc
                                  |
                                  |_____ gif
                                  |
                                  |_____ html
                                  .
                                  .
                                  .
```

## References for Building the App's Rules Batabase
* [Scalpel's configuration file](https://github.com/sleuthkit/scalpel/blob/master/scalpel.conf).
* [Foremost's configuration file](http://foremost.sourceforge.net/pkg/foremost-1.5.7.tar.gz) (Scalpel's configuration almost same as this).
* [Data Doctor: Headers and footers of some important file types](http://www.datadoctor.biz/data_recovery_programming_book_chapter14-page2.html)
* [Data Carving Concepts](https://www.sans.org/reading-room/whitepapers/forensics/data-carving-concepts-32969)
* [File Signatures Table](https://www.garykessler.net/library/file_sigs.html)

## Future Work
* Build a screen for adding / editing / deleting rules, as well as enabling / disabling them.
* Add instrumentation tests along with adding test assets to this repo.
* Implement individual carving functions based on a file type, similar to how it is done in [Foremost](http://foremost.sourceforge.net/).

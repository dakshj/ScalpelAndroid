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
* Usage of Android's (ViewModel)[https://developer.android.com/topic/libraries/architecture/viewmodel] architecture to prevent interruptions of the background carving process.

## Explanation of Code
* Each rule is stored in a local SQLite database table, via the Android ORM library called as
(Room)[https://developer.android.com/topic/libraries/architecture/room].
* Rules have been statically defined in the source code here, since it was not feasible to design a UI for input / editing / disabling rules given the time frame.

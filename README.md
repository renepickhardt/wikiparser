# WikiParser
WikiParser is mainly used for extracting information about blocked users on Wikipedia by SAX-parsing Wikipedia dumps.
There is also a rudimentary implementation of page history extraction.
However, users interested in page history extraction should rather refer to [0nse/WikiWho DiscussionParser](https://github.com/0nse/WikiWho/tree/DiscussionsParser).
0nse/WikiWho DiscussionParser is much more efficient (especially in terms of speed), elaborate and versatile.

The current [`LoggingBlockHandler`](src/main/java/de/renepickhardt/imessages/xmlParser/LoggingBlockHandler.java) processes logging dumps from the English Wikipedia and writes information about user blocks into a CSV file.
Thus, one can easily generate statistics with simple UNIX tools.
One example would be extracting the number of affected unique users by employing `awk -F "\t" '{print $6}' logItems.csv | sort | uniq | wc -l`.
[`BlockLogPostProcessing`](src/main/python/BlockLogPostProcessing.py]) is used while extracting blocks to filter some, which are not of interest.
Among others, filtered blocks were issued on bots or as tests.

## How to execute
* `git clone git@github.com:renepickhardt/wikiparser.git`
* `mvn compile`
* `mvn exec:java -Dexec.args="log"`

Currently, the accepted parameters are `log` and `history` for processing log files and page histories respectively.

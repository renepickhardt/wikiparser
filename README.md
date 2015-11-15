# Wikiparser
The wikiparser will exist for the “I”-messages project. It is a SAX-parser to process Wikipedia dumps. The current `LoggingHandler` processes logging dumps from the English Wikipedia and writes them into a CSV file. Thus, one can easily generate statistics on it with simple UNIX tools. One example would be extracting the number of unique affected users by employing `awk -F "\t" '{print $6}' logItems.csv | sort | uniq | wc -l`.

## How to execute
* `git clone git@github.com:renepickhardt/wikiparser.git`
* `mvn compile`
* `mvn exec:java -Dexec.args="log"`

Currently, the accepted parameters are `log` and `history` for processing log files and page histories respectively. Although a rudimentary implementation of page history extraction exists, interested users should rather refer to [0nse/WikiWho DiscussionParser](https://github.com/0nse/WikiWho/tree/DiscussionsParser). WikiWho DiscussionParser is much more efficient (especially in terms of speed), elaborate and versatile.

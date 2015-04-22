# Notes
This file serves as a hub for general notes e.g. about the syntax of the Wiki dumps.

## [dewiki-20150301-pages-logging.xml](https://dumps.wikimedia.org/dewiki/20150301/dewiki-20150301-pages-logging.xml.gz)
* All positive odd namespaces are discussion pages:
| Key | Namespace |
|-----|-----------|
| -2 | Medium |
| -1 | Spezial |
| 0 | [empty] |
| 1 | Diskussion |
| 2 | Benutzer |
| 3 | Benutzer Diskussion |
| 4 | Wikipedia |
| 5 | Wikipedia Diskussion |
| 6 | Datei |
| 7 | Datei Diskussion |
| 8 | MediaWiki |
| 9 | MediaWiki Diskussion |
| 10 | Vorlage |
| 11 | Vorlage Diskussion |
| 12 | Hilfe |
| 13 | Hilfe Diskussion |
| 14 | Kategorie |
| 15 | Kategorie Diskussion |
| 100 | Portal |
| 101 | Portal Diskussion |
| 828 | Modul |
| 829 | Modul Diskussion |

* Existing `action`s are:
  * `upload`
  * `delete`
  * `protect`
  * `block`
  * `unprotect`
  * `restore`
  * `unblock`
  * `rights`
  * `move`
  * `move_redir`
  * `<action />`
  * `renameuser`
  * `newusers`
  * `create`
  * `create2`
  * `interwiki`
  * `modify`
  * `overwrite`
  * `patrol`
  * `approve`
  * `unapprove`
  * `config`
  * `reset`
  * `autocreate`
  * `approve-a`
  * `approve-ia`
  * `approve-i`
  * `move_prot`
  * `reblock`
  * `revision`
  * `event`
  * `autopromote`
  * `whitelist`
  * `hide`
  * `unhelpful`
  * `helpful`
  * `flag`
  * `resolve`
  * `unhide`
  * `unflag`
  * `feature`
  * `undo-helpful`
  * `autohide`
  * `unresolve`
  * `clear-flags`
  * `undo-unhelpful`
  * `unfeature`
  * `byemail`
  * `inappropriate`
  * `uninappropriate`
  * `noaction`
  * `unnoaction`
  * `send`
  * `thank`
  * `skipbadns`
  * `skipnouser`
  * `failure`
  * `merge`

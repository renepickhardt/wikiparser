#!/usr/bin/python
# -*- coding: utf-8 -*-
#author: Michael Ruster

def process(inputFile, outputFile):
    """ Reads a block log CSV, cleans the comment, reorders the output and
    writes it to disk according to outputFile. Please be aware that, if
    writing permissions are given for outputFile, it will blindly overwrite
    everything you love.
    """
    import csv
    from sys import path
    path.append("./WikiCodeCleaner")
    try:
        from WikiCodeCleaner.clean import clean as cleanWikiCode
    except ImportError:
        # Ubuntu 12.04's Python 3.2.3 behaves differently:
        from clean import clean as cleanWikiCode
    ignoredBlocksCount = 0

    with inputFile:
        logReader = csv.reader(inputFile, delimiter='\t', quotechar='"')
        logWriter = csv.writer(outputFile, delimiter='\t',
                                   quotechar='|', quoting=csv.QUOTE_MINIMAL)

        for [comment, userId, userName, timestamp, blockedUserName] in logReader:
            comment = comment.lower()
            cleanedComment = cleanWikiCode(comment).strip()
            if isCommentOfInterest(cleanedComment):
                logWriter.writerow([timestamp,
                                    blockedUserName,
                                    cleanedComment,
                                    userId,
                                    userName])
            else:
                ignoredBlocksCount += 1
    print('[I] Ignored %i comments' % ignoredBlocksCount)

def isCommentOfInterest(comment):
    """ Drop blocks that seem to be issued due to behaviour that is of no
    interest to us. This includes automated and testing behaviour. Also,
    here are some word combinations that could be of interest:

        badWords = ['copyright violation', 'copyright infringement', # 'promotional account', 'spam advertising', 'advertising purposes', 'matches the name of a business', 'advertising', 'matches the name of a website', 'advertising name of', 'name of company', 'commercial username', 'band name', 'company name'
                    # 'abusing multiple accounts', 'promoting socking', 'sock puppet', 'sockpuppetry', 'sockpuppet', 'sock'
                    'represent organization', # a Wikipedia account is not meant to represent an organization
                    'violation of the username policy', 'inappropriate username', 'username vandal', 'vandalism user name', 'username violation', 'user renamed', 'username policy', 'username block', 'get a new username', 'name', 'username closely matches', 'bad username', 'confusing username', 'choose another username', 'obscene username', 'attack username'
                    'spambot', 'spam bot', 'vandalbot', 'vandal bot', 'bot malfunction', 'unapproved bot', 'referral spammer', 'spamonly', 'botlike' # 'spamming', 'spamonly account' 'spam only account'
                    # 'troll', 'idiot', 'go away'
                    'unsourced content', 'cited source', 'misinformation'
                    'fake articles', 'inappropriate pages', 'nonsense pages',
                    'test', # 'wordbomb',
                    # 'wikistalking'
                    # 'compromised account', 'gibberish', 'impersonator'
                    'user request',
                    'mass attack'
        ]
        goodWords = ['vandal', 'attack', 'harassment', 'threat', 'hating','death threat', 'legal threat']

    We are testing for 'in words' if testing as a substring of comments
    would most likely return false positives (e.g. 'bot' in 'both').
    """
    words = comment.split(' ')
    if ( 'personal attack' in comment or                   # personal attacks
         'wpnpa' in words or
         'npa' in words or
         'harass' in comment or                            # harassment
         'wpharassment' in words or
         'threat' in comment or                            # legal/death threats
         'wpnlt' in words or
         'nlt' in words or
         'hating' in words                                 # hating
        ):
        if ( 'bot' in words or                         # bot content
             'vandalbot' in comment or
             'spambot' in comment or
             'user request' in comment or              # voluntary block
             'mass attack' in comment or               # automated attack
             'username' in words or                    # user names because they might act in a collaborative manner but have been blocked solely due to their promo puposes (or because they had provoking names)
             'user name' in comment or
             'unsourced content' in comment or         # unsourced content -- might collaborate in a well-intended manner but fails on a level that we cannot detect wih our approach
             'cited source' in comment or
             # These may add too much noise to our data but also are typical
             # user behaviour that could be nice to detect:
             'referral spam' in comment or             # spam (there are way more elaborte anti-spam measures already)
             'spamonly' in words or
             'spam only' in comment or
             'copyright' in comment or                 # copyright infringement (hardly detectable on just world-level)
             'advertis' in comment or                  # promotional content
             'test' in comment                         # test
            ):
            print('[I] Ignoring block "%s"' % comment)
            return False
        else:
            return True

    return False

if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser(description='Clean BlockLog comments so that symbols are removed and only space separated words are left.',
                                     epilog="""
                                     WikiParser, Copyright (C) 2015 René Pickhardt, Michael Ruster.
                                     WikiCodeCleaner comes with ABSOLUTELY NO WARRANTY. This is free software, and you are welcome to redistribute it under certain conditions. Launch this program with `licence' for details.
                                     """)
    parser.add_argument('inputFile',  type=argparse.FileType('r'),
                        help='The path to the file to process (CSV).')
    parser.add_argument('outputFile',  type=argparse.FileType('w'),
                        help='The path to the file to which the processed output should be written to. Please be aware that the file will be overwritten if it already exists.')

    args = parser.parse_args()
    if args.inputFile == args.outputFile:
        raise ValueError("Input and output file may not identify the same file!")
    process(args.inputFile, args.outputFile)

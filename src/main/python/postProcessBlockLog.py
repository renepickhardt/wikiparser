#!/usr/bin/python
# -*- coding: utf-8 -*-
#author: Michael Ruster

def process(inputFile, outputFile):
    """ Reads a block log CSV, cleans the comment, reorders the output,
    drops the action (which is always 'block') and writes it to disk
    according to outputFile. Please be aware that, if writing permissions
    are given for outputFile, it will blindly overwrite everything you love.
    """
    import csv
    from sys import path
    path.append("./WikiCodeCleaner")
    from WikiCodeCleaner.clean import clean as cleanWikiCode

    with inputFile:
        logReader = csv.reader(inputFile, delimiter='\t', quotechar='"')
        logWriter = csv.writer(outputFile, delimiter='\t',
                                   quotechar='|', quoting=csv.QUOTE_MINIMAL)

        i = 0
        import time
        x = time.time()
        for [action, comment, userId, userName, logItemId, timestamp, blockedUserName] in logReader:
            i += 1
            if i % 100 == 0:
                y = time.time()
                print ( y - x )
                x = time.time()
            comment = comment.lower()
            cleanedComment = cleanWikiCode(comment).strip()
            # TODO filter by comment content
            logWriter.writerow([timestamp,
                                blockedUserName,
                                cleanedComment,
                                userId,
                                userName])

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

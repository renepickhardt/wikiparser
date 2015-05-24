#!/usr/bin/python
# -*- coding: utf-8 -*-
import re

# skip level 1, it is page name level
section = re.compile(r'(==+)\s*(.*?)\s*\1')

listOpen = { '*': '<ul>', '#': '<ol>', ';': '<dl>', ':': '<dl>' }
listClose = { '*': '</ul>', '#': '</ol>', ';': '</dl>', ':': '</dl>' }
listItem = { '*': '<li>%s</li>', '#': '<li>%s</<li>', ';': '<dt>%s</dt>',
             ':': '<dd>%s</dd>' }

# Match preformatted lines
preformatted = re.compile(r'^ .*?$')


def compact(text):
    """Deal with headers, lists, empty sections, residuals of tables.
    :param toHTML: convert to HTML
    """

    page = []                   # list of paragraph
    headers = {}                # Headers for unfilled sections
    emptySection = False        # empty sections are discarded
    listLevel = ''              # nesting of lists

    for line in text.split('\n'):

        if not line:
            continue
        # Handle section titles
        m = section.match(line)
        if m:
            title = m.group(2)
            lev = len(m.group(1))
            if title and title[-1] not in '!?':
                title += '.'
            headers[lev] = title
            # drop previous headers
            for i in headers.keys():
                if i > lev:
                    del headers[i]
            emptySection = True
            continue
        # Handle page title
        if line.startswith('++'):
            title = line[2:-2]
            if title:
                if title[-1] not in '!?':
                    title += '.'
                page.append(title)
        # handle indents
        elif line[0] == ':':
            #page.append(line.lstrip(':*#;'))
            continue
        # handle lists
        elif line[0] in '*#;:':
                continue
        elif len(listLevel):
            for c in reversed(listLevel):
                page.append(listClose[c])
            listLevel = []

        # Drop residuals of lists
        elif line[0] in '{|' or line[-1] == '}':
            continue
        # Drop irrelevant lines
        elif (line[0] == '(' and line[-1] == ')') or line.strip('.-') == '':
            continue
        elif len(headers):
            headers.clear()
            page.append(line)   # first line
            emptySection = False
        elif not emptySection:
            page.append(line)
        # dangerous
        # # Drop preformatted
        # elif line[0] == ' ':
        #     continue

    return page


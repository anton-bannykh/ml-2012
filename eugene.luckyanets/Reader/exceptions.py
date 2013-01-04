# -*- coding: utf-8 -*-

__author__ = 'eugene'


class ReaderException(Exception):
    def __init__(self, comment):
        self.comment = comment

    def __str__(self):
        return repr(self.comment)


class CountMismatchReaderException(ReaderException):
    def __init__(self, label_count, image_count):
        super(CountMismatchReaderException, self).__init__(
            'Count mismatch: %d labels, %d images' % (label_count, image_count))
        self.label_count = label_count
        self.image_count = image_count
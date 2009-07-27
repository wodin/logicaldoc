#!/usr/bin/env python
# -*- coding: utf-8 -*-

from translate.lang import common

def test_words():
    """Tests basic functionality of word segmentation."""
    language = common.Common
    words = language.words(u"Test sentence.")
    assert words == [u"Test", u"sentence"]

    # Let's test Khmer with zero width space (\u200b)
    words = language.words(u"ផ្ដល់​យោបល់")
    assert words == [u"ផ្ដល់", u"យោបល់"]

    words = language.words(u"This is a weird test .")
    assert words == [u"This", u"is", u"a", u"weird", u"test"]

    words = language.words(u"Don't send e-mail!")
    assert words == [u"Don't", u"send", u"e-mail"]

    words = language.words(u"Don’t send e-mail!")
    assert words == [u"Don’t", u"send", u"e-mail"]

def test_sentences():
    """Tests basic functionality of sentence segmentation."""
    language = common.Common
    sentences = language.sentences(u"This is a sentence.")
    assert sentences == [u"This is a sentence."]
    sentences = language.sentences(u"This is a sentence")
    assert sentences == [u"This is a sentence"]
    sentences = language.sentences(u"This is a sentence. Another one.")
    assert sentences == [u"This is a sentence.", u"Another one."]
    sentences = language.sentences(u"This is a sentence. Another one. Bla.")
    assert sentences == [u"This is a sentence.", u"Another one.", u"Bla."]
    sentences = language.sentences(u"This is a sentence.Not another one.")
    assert sentences == [u"This is a sentence.Not another one."]
    sentences = language.sentences(u"Exclamation! Really? No...")
    assert sentences == [u"Exclamation!", u"Really?", u"No..."]
    sentences = language.sentences(u"Four i.e. 1+3. See?")
    assert sentences == [u"Four i.e. 1+3.", u"See?"]
    sentences = language.sentences(u"Apples, bananas, etc. are nice.")
    assert sentences == [u"Apples, bananas, etc. are nice."]
    sentences = language.sentences(u"Apples, bananas, etc.\nNext part")
    assert sentences == [u"Apples, bananas, etc.", u"Next part"]
    sentences = language.sentences(u"No font for displaying text in encoding '%s' found,\nbut an alternative encoding '%s' is available.\nDo you want to use this encoding (otherwise you will have to choose another one)?")
    assert sentences == [u"No font for displaying text in encoding '%s' found,\nbut an alternative encoding '%s' is available.", u"Do you want to use this encoding (otherwise you will have to choose another one)?"]

def test_capsstart():
    """Tests that the indefinite article ('n) doesn't confuse startcaps()."""
    language = common.Common
    assert language.capsstart("Open cow file")
    assert language.capsstart("'Open' cow file")
    assert not language.capsstart("open cow file")
    assert not language.capsstart(":")


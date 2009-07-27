#!/usr/bin/env python
# -*- coding: utf-8 -*-
# 
# Copyright 2007-2009 Zuza Software Foundation
# 
# This file is part of translate.
#
# translate is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# translate is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with translate; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

"""This module stores information and functionality that relates to plurals."""

import unicodedata

languages = {
'af': ('Afrikaans', 2, '(n != 1)'),
'ak': ('Akan', 2, 'n > 1'),
'am': ('Amharic', 2, 'n > 1'),
'ar': ('Arabic', 6, 'n==0 ? 0 : n==1 ? 1 : n==2 ? 2 : n>=3 && n<=10 ? 3 : n>=11 && n<=99 ? 4 : 5'),
'arn': ('Mapudungun; Mapuche', 2, 'n > 1'),
'az': ('Azerbaijani', 2, '(n != 1)'),
'be': ('Belarusian', 3, 'n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2'),
'bg': ('Bulgarian', 2, '(n != 1)'),
'bn': ('Bengali', 2, '(n != 1)'),
'bo': ('Tibetan', 1, '0'),
'bs': ('Bosnian', 3, 'n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2'),
'ca': ('Catalan; Valencian', 2, '(n != 1)'),
'cs': ('Czech', 3, '(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2'),
'csb': ('Kashubian', 3, 'n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2'),
'cy': ('Welsh', 2, '(n==2) ? 1 : 0'),
'da': ('Danish', 2, '(n != 1)'),
'de': ('German', 2, '(n != 1)'),
'dz': ('Dzongkha', 1, '0'),
'el': ('Greek', 2, '(n != 1)'),
'en': ('English', 2, '(n != 1)'),
'en_GB': ('English (United Kingdom)', 2, '(n != 1)'),
'en_ZA': ('English (South Africa)', 2, '(n != 1)'),
'eo': ('Esperanto', 2, '(n != 1)'),
'es': ('Spanish; Castilian', 2, '(n != 1)'),
'et': ('Estonian', 2, '(n != 1)'),
'eu': ('Basque', 2, '(n != 1)'),
'fa': ('Persian', 1, '0'),
'fi': ('Finnish', 2, '(n != 1)'),
'fil': ('Filipino; Pilipino', 2, '(n > 1)'),
'fo': ('Faroese', 2, '(n != 1)'),
'fr': ('French', 2, '(n > 1)'),
'fur': ('Friulian', 2, '(n != 1)'),
'fy': ('Frisian', 2, '(n != 1)'),
'ga': ('Irish', 3, 'n==1 ? 0 : n==2 ? 1 : 2'),
'gl': ('Galician', 2, '(n != 1)'),
'gu': ('Gujarati', 2, '(n != 1)'),
'gun': ('Gun', 2, '(n > 1)'),
'ha': ('Hausa', 2, '(n != 1)'),
'he': ('Hebrew', 2, '(n != 1)'),
'hi': ('Hindi', 2, '(n != 1)'),
'hy': ('Armenian', 1, '0'),
'hr': ('Croatian', 3, '(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)'),
'hu': ('Hungarian', 2, '(n != 1)'),
'id': ('Indonesian', 1, '0'),
'is': ('Icelandic', 2, '(n != 1)'),
'it': ('Italian', 2, '(n != 1)'),
'ja': ('Japanese', 1, '0'),
'jv': ('Javanese', 2, '(n != 1)'),
'ka': ('Georgian', 1, '0'),
'km': ('Khmer', 1, '0'),
'kn': ('Kannada', 2, '(n != 1)'),
'ko': ('Korean', 1, '0'),
'ku': ('Kurdish', 2, '(n != 1)'),
'kw': ('Cornish', 4, '(n==1) ? 0 : (n==2) ? 1 : (n == 3) ? 2 : 3'),
'ky': ('Kirghiz; Kyrgyz', 1, '0'),
'lb': ('Luxembourgish; Letzeburgesch', 2, '(n != 1)'),
'ln': ('Lingala', 2, '(n > 1)'),
'lt': ('Lithuanian', 3, '(n%10==1 && n%100!=11 ? 0 : n%10>=2 && (n%100<10 || n%100>=20) ? 1 : 2)'),
'lv': ('Latvian', 3, '(n%10==1 && n%100!=11 ? 0 : n != 0 ? 1 : 2)'),
'mg': ('Malagasy', 2, '(n > 1)'),
'mi': ('Maori', 2, '(n > 1)'),
'mk': ('Macedonian', 2, 'n==1 || n%10==1 ? 0 : 1'),
'ml': ('Malayalam', 2, '(n != 1)'),
'mn': ('Mongolian', 2, '(n != 1)'),
'mr': ('Marathi', 2, '(n != 1)'),
'ms': ('Malay', 1, '0'),
'mt': ('Maltese', 4, '(n==1 ? 0 : n==0 || ( n%100>1 && n%100<11) ? 1 : (n%100>10 && n%100<20 ) ? 2 : 3)'),
'nah': ('Nahuatl languages', 2, '(n != 1)'),
'nb': ('Norwegian Bokmal', 2, '(n != 1)'),
'ne': ('Nepali', 2, '(n != 1)'),
'nl': ('Dutch; Flemish', 2, '(n != 1)'),
'nn': ('Norwegian Nynorsk', 2, '(n != 1)'),
'nso': ('Northern Sotho', 2, '(n > 1)'),
'or': ('Oriya', 2, '(n != 1)'),
'pa': ('Panjabi; Punjabi', 2, '(n != 1)'),
'pap': ('Papiamento', 2, '(n != 1)'),
'pl': ('Polish', 3, '(n==1 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)'),
'pt': ('Portuguese', 2, '(n != 1)'),
'pt_BR': ('Portuguese (Brazil)', 2, '(n > 1)'),
'ro': ('Romanian', 3, '(n==1 ? 0 : (n==0 || (n%100 > 0 && n%100 < 20)) ? 1 : 2);'),
'ru': ('Russian', 3, '(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)'),
'sco': ('Scots', 2, '(n != 1)'),
'sk': ('Slovak', 3, '(n==1) ? 0 : (n>=2 && n<=4) ? 1 : 2'),
'sl': ('Slovenian', 4, '(n%100==1 ? 0 : n%100==2 ? 1 : n%100==3 || n%100==4 ? 2 : 3)'),
'so': ('Somali', 2, '(n != 1)'),
'sq': ('Albanian', 2, '(n != 1)'),
'sr': ('Serbian', 3, '(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)'),
'su': ('Sundanese', 1, '0'),
'sv': ('Swedish', 2, '(n != 1)'),
'ta': ('Tamil', 2, '(n != 1)'),
'te': ('Telugu', 2, '(n != 1)'),
'tg': ('Tajik', 2, '(n != 1)'),
'ti': ('Tigrinya', 2, '(n > 1)'),
'th': ('Thai', 1, '0'),
'tk': ('Turkmen', 2, '(n != 1)'),
'tr': ('Turkish', 1, '0'),
'uk': ('Ukrainian', 3, '(n%10==1 && n%100!=11 ? 0 : n%10>=2 && n%10<=4 && (n%100<10 || n%100>=20) ? 1 : 2)'),
'vi': ('Vietnamese', 1, '0'),
'wa': ('Walloon', 2, '(n > 1)'),
# Chinese is difficult because the main divide is on script, not really 
# country. Simplified Chinese is used mostly in China, Singapore and Malaysia.
# Traditional Chinese is used mostly in Hong Kong, Taiwan and Macau.
'zh_CN': ('Chinese (China)', 1, '0'),
'zh_HK': ('Chinese (Hong Kong)', 1, '0'),
'zh_TW': ('Chinese (Taiwan)', 1, '0'),
}
"""Dictionary of language data.
The language code is the dictionary key (which may contain country codes and modifiers).
The value is a tuple: (Full name in English, nplurals, plural equation)"""

def simplercode(code):
    """This attempts to simplify the given language code by ignoring country 
    codes, for example.

    @see:
      - U{http://www.rfc-editor.org/rfc/bcp/bcp47.txt}
      - U{http://www.rfc-editor.org/rfc/rfc4646.txt}
      - U{http://www.rfc-editor.org/rfc/rfc4647.txt}
      - U{http://www.w3.org/International/articles/language-tags/}
    """
    if not code:
        return code

    normalized = normalize_code(code)
    separator = normalized.rfind('-')
    if separator >= 0:
        return code[:separator]
    else:
        return ""
    

expansion_factors = {
        'af': 0.1,
        'ar': -0.09,
        'es': 0.21,
        'fr': 0.28,
        'it': 0.2,
}
"""Source to target string length expansion factors."""

import gettext
import re

iso639 = {}
"""ISO 639 language codes"""
iso3166 = {}
"""ISO 3166 country codes"""

langcode_re = re.compile("^[a-z]{2,3}([_-][A-Z]{2,3}|)(@[a-zA-Z0-9]+|)$")
variant_re = re.compile("^[_-][A-Z]{2,3}(@[a-zA-Z0-9]+|)$")

def languagematch(languagecode, otherlanguagecode):
    """matches a languagecode to another, ignoring regions in the second"""
    if languagecode is None:
      return langcode_re.match(otherlanguagecode)
    return languagecode == otherlanguagecode or \
      (otherlanguagecode.startswith(languagecode) and variant_re.match(otherlanguagecode[len(languagecode):]))

dialect_name_re = re.compile(r"([^(\s]+)\s*\(([^)]+)\)")

def tr_lang(langcode=None):
    """Gives a function that can translate a language name, even in the form::
           "language (country)"
       into the language with iso code langcode, or the system language if no
       language is specified."""
    langfunc = gettext_lang(langcode)
    countryfunc = gettext_country(langcode)

    def handlelanguage(name):
        match = dialect_name_re.match(name)
        if match:
            language, country = match.groups()
            return u"%s (%s)" % (langfunc(language), countryfunc(country))
        else:
            return langfunc(name)

    return handlelanguage

def gettext_lang(langcode=None):
    """Returns a gettext function to translate language names into the given
    language, or the system language if no language is specified."""
    if not langcode in iso639:
        if not langcode:
            langcode = ""
            t = gettext.translation('iso_639', fallback=True)
        else:
            t = gettext.translation('iso_639', languages=[langcode], fallback=True)
        iso639[langcode] = t.ugettext
    return iso639[langcode]

def gettext_country(langcode=None):
    """Returns a gettext function to translate country names into the given
    language, or the system language if no language is specified."""
    if not langcode in iso3166:
        if not langcode:
            langcode = ""
            t = gettext.translation('iso_3166', fallback=True)
        else:
            t = gettext.translation('iso_3166', languages=[langcode], fallback=True)
        iso3166[langcode] = t.ugettext
    return iso3166[langcode]

def normalize(string, normal_form="NFC"):
    """Return a unicode string in its normalized form

       @param string: The string to be normalized
       @param normal_form: NFC (default), NFD, NFCK, NFDK
       @return: Normalized string
    """
    if string is None:
        return None
    else:
        return unicodedata.normalize(normal_form, string)

def forceunicode(string):
    """Ensures that the string is in unicode.

       @param string: A text string
       @type string: Unicode, String
       @return: String converted to Unicode and normalized as needed.
       @rtype: Unicode
    """
    if string is None:
        return None
    if isinstance(string, str):
        encoding = getattr(string, "encoding", "utf-8")
        string = string.decode(encoding)
    return string

def normalized_unicode(string):
    """Forces the string to unicode and does normalization."""
    return normalize(forceunicode(string))

def normalize_code(code):
    return code.replace("_", "-").replace("@", "-").lower()


def simplify_to_common(language_code, languages=languages):
    """Simplify language code to the most commonly used form for the
    language, stripping country information for languages that tend
    not to be localized differently for different countries"""
    simpler = simplercode(language_code)
    if normalize_code(language_code) in [normalize_code(key) for key in languages.keys()] or simpler =="":
        return language_code
    else:
        return simplify_to_common(simpler)
    

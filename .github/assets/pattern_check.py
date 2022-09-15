# modules
import re
import sys

# pattern
pattern = re.compile(r"/(?!supportedVersion.*|supported.*)((S)|(s)upport.*|(R)|(r)elease.*|(B)|(b)ump version.*).*/g")
print(bool(re.search(pattern, sys.argv[1])))
# modules
import re
import sys

# pattern
pattern = re.compile(r"^\s*\(\s*(R|r)elease\s*\)")
print(bool(re.search(pattern, sys.argv[1])))

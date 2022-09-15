# modules
import re
import sys

# pattern
pattern = re.compile(r"(?!supportedVersion.*|(S|s)upported.*)((S|s)upport.*|(R|r)elease.*|(B|b)ump version.*).*")
print(bool(re.search(pattern, sys.argv[1])))
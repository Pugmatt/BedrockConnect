import re
import sys
from pathlib import Path

# Read 'release' variable from BedrockConnect.java to use for new version

file_path = Path("serverlist-server/src/main/com/pyratron/pugmatt/bedrockconnect/BedrockConnect.java")

try:
    content = file_path.read_text()
except FileNotFoundError:
    sys.stderr.write(f"File not found: {file_path}\n")
    sys.exit(1)

ver_string = re.search(r'String\s+release\s*=\s*"([^"]+)"', content)
if ver_string:
    print(ver_string.group(1))
else:
    sys.stderr.write("No release version found in file.\n")
    sys.exit(1)

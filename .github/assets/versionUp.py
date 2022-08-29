# modules
import urllib.request as request
import json

# fetch newest version
req = request.Request("https://api.github.com/repos/Pugmatt/BedrockConnect/releases/latest")
req.add_header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:104.0) Gecko/20100101 Firefox/104.0") # avoid 403 erros
req = json.loads(request.urlopen(req).read().decode("utf-8"))

# bump version +1
ver_string = req["tag_name"].split(".")
if ver_string[1] == "99":
    string_zero = int(ver_string[0])+1
    ver_string = str(string_zero)+"."+"0"
else:
    string_one = int(ver_string[1])+1
    ver_string = ver_string[0]+"."+str(string_one)

# print new ver
print(ver_string)

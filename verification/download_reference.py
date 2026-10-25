"""Download the exact inspected Amplified Nether artifact; verify its published SHA-512."""
import hashlib
from pathlib import Path
from urllib.request import urlopen

URL = 'https://cdn.modrinth.com/data/wXiGiyGX/versions/IYaFP7y8/Amplified_Nether_26.3_v1.3.0%2B26.3.jar'
SHA512 = '0b5a61fb524495ad63a64aaa7144ef74917506ec9ff2881d9d3bedf4bab15335f259536e4fcd44845860a6840f93abb0e699243619e7acf13bf8528e49d18914'
data = urlopen(URL).read()
if hashlib.sha512(data).hexdigest() != SHA512:
    raise SystemExit('Reference artifact hash mismatch')
Path(__file__).with_name('amplified-nether.jar').write_bytes(data)
print('Verified Amplified Nether 1.3.0+26.3')

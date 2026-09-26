from typing import List
from urllib.parse import quote
from ..models.station import Station
from ..config import settings


class M3UBuilder:
    @staticmethod
    def build_m3u(stations: List[Station], raw: bool = False) -> str:
        lines = ["#EXTM3U"]
        base_url = settings.BASE_URL.rstrip('/')

        for s in stations:
            logo = s.favicon or ""
            group = s.country or "Global"
            name = s.name.replace(",", " ")

            if raw:
                stream_url = s.url_resolved or s.url
            else:
                encoded_url = quote(s.url_resolved or s.url, safe="")
                stream_url = f"{base_url}/stream?url={encoded_url}"

            extinf = f'#EXTINF:-1 tvg-logo="{logo}" group-title="{group}",{name}'
            lines.append(extinf)
            lines.append(stream_url)

        return "\n".join(lines)

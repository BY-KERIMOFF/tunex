from typing import Optional, List
from pydantic import BaseModel, Field


class Station(BaseModel):
    stationuuid: str
    name: str
    url: str
    url_resolved: Optional[str] = ""
    homepage: Optional[str] = ""
    favicon: Optional[str] = ""
    tags: Optional[str] = ""
    country: Optional[str] = ""
    countrycode: Optional[str] = ""
    state: Optional[str] = ""
    language: Optional[str] = ""
    votes: Optional[int] = 0
    codec: Optional[str] = ""
    bitrate: Optional[int] = 0
    hls: Optional[int] = 0
    lastcheckok: Optional[int] = 1
    clickcount: Optional[int] = 0

    @property
    def tag_list(self) -> List[str]:
        if not self.tags:
            return []
        return [t.strip().lower() for t in self.tags.split(",") if t.strip()]

    @property
    def language_list(self) -> List[str]:
        if not self.language:
            return []
        return [l.strip().lower() for l in self.language.split(",") if l.strip()]

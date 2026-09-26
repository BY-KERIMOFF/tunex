import asyncio
import logging
from typing import List
import httpx
from ..models.station import Station
from ..config import settings
from .cache import cache_service

logger = logging.getLogger(__name__)


class RadioBrowserService:
    def __init__(self):
        self.mirrors = settings.mirror_list
        self.timeout = settings.RADIO_BROWSER_TIMEOUT
        self.user_agent = settings.RADIO_BROWSER_USER_AGENT
        self.page_size = settings.RADIO_BROWSER_PAGE_SIZE
        self.max_pages = settings.RADIO_BROWSER_MAX_PAGES

    async def _fetch_from_mirror(self, client: httpx.AsyncClient, mirror: str, offset: int, limit: int) -> List[dict]:
        url = f"{mirror}/json/stations/search"
        params = {
            "offset": offset,
            "limit": limit,
            "hidebroken": "true",
            "order": "clickcount",
            "reverse": "true"
        }
        headers = {"User-Agent": self.user_agent}
        response = await client.get(url, params=params, headers=headers, timeout=self.timeout)
        response.raise_for_status()
        return response.json()

    async def fetch_page_with_fallback(self, client: httpx.AsyncClient, offset: int, limit: int) -> List[dict]:
        for mirror in self.mirrors:
            try:
                data = await self._fetch_from_mirror(client, mirror, offset, limit)
                return data
            except Exception as e:
                logger.warning(f"Mirror {mirror} failed for offset {offset}: {e}. Trying next mirror...")
        logger.error(f"All mirrors failed for offset {offset}")
        return []

    async def fetch_all_stations(self, force_refresh: bool = False) -> List[Station]:
        if not force_refresh and cache_service.is_cache_valid():
            stations = cache_service.get_stations()
            if stations:
                logger.info(f"Loaded {len(stations)} stations from cache.")
                return stations

        logger.info("Fetching stations from Radio-Browser API across mirrors...")
        all_stations: List[Station] = []
        seen_uuids = set()

        limits_per_batch = 5
        async with httpx.AsyncClient(timeout=self.timeout) as client:
            page = 0
            while page < self.max_pages:
                tasks = []
                for p in range(page, min(page + limits_per_batch, self.max_pages)):
                    offset = p * self.page_size
                    tasks.append(self.fetch_page_with_fallback(client, offset, self.page_size))

                results = await asyncio.gather(*tasks, return_exceptions=True)
                empty_page_encountered = False

                for res in results:
                    if isinstance(res, list) and res:
                        for raw in res:
                            uuid = raw.get("stationuuid")
                            if uuid and uuid not in seen_uuids:
                                seen_uuids.add(uuid)
                                try:
                                    station = Station(
                                        stationuuid=uuid,
                                        name=raw.get("name", "").strip() or "Unnamed Station",
                                        url=raw.get("url", "").strip(),
                                        url_resolved=raw.get("url_resolved", raw.get("url", "")).strip(),
                                        homepage=raw.get("homepage", "").strip(),
                                        favicon=raw.get("favicon", "").strip(),
                                        tags=raw.get("tags", "").strip(),
                                        country=raw.get("country", "").strip(),
                                        countrycode=raw.get("countrycode", "").strip(),
                                        state=raw.get("state", "").strip(),
                                        language=raw.get("language", "").strip(),
                                        votes=raw.get("votes", 0),
                                        codec=raw.get("codec", "").strip(),
                                        bitrate=raw.get("bitrate", 0),
                                        hls=raw.get("hls", 0),
                                        lastcheckok=raw.get("lastcheckok", 1),
                                        clickcount=raw.get("clickcount", 0)
                                    )
                                    if station.url:
                                        all_stations.append(station)
                                except Exception as err:
                                    logger.debug(f"Error parsing station {uuid}: {err}")
                    else:
                        empty_page_encountered = True

                if empty_page_encountered or len(all_stations) >= 50000:
                    break

                page += limits_per_batch

        logger.info(f"Total fetched stations: {len(all_stations)}")
        if all_stations:
            cache_service.set_stations(all_stations)

        return all_stations


radio_browser_service = RadioBrowserService()

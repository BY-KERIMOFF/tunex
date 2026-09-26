import json
import os
import time
import logging
from typing import List, Optional
from ..models.station import Station
from ..config import settings

logger = logging.getLogger(__name__)

try:
    import redis
except ImportError:
    redis = None


class CacheService:
    def __init__(self):
        self.cache_type = settings.CACHE_TYPE
        self.file_path = settings.CACHE_FILE_PATH
        self.ttl = settings.CACHE_TTL_SECONDS
        self._memory_cache: Optional[List[Station]] = None
        self._last_updated: float = 0.0

        self.redis_client = None
        if self.cache_type == "redis" and redis:
            try:
                self.redis_client = redis.Redis.from_url(settings.REDIS_URL, decode_responses=True)
            except Exception as e:
                logger.error(f"Failed to connect to Redis: {e}")

    def is_cache_valid(self) -> bool:
        if self._memory_cache and (time.time() - self._last_updated < self.ttl):
            return True

        if self.cache_type == "file":
            if os.path.exists(self.file_path):
                mtime = os.path.getmtime(self.file_path)
                if time.time() - mtime < self.ttl:
                    return True

        elif self.cache_type == "redis" and self.redis_client:
            try:
                return self.redis_client.exists("neo_radio_stations") > 0
            except Exception as e:
                logger.error(f"Redis existence check failed: {e}")

        return False

    def get_stations(self) -> List[Station]:
        if self._memory_cache and (time.time() - self._last_updated < self.ttl):
            return self._memory_cache

        if self.cache_type == "file":
            if os.path.exists(self.file_path):
                try:
                    with open(self.file_path, "r", encoding="utf-8") as f:
                        data = json.load(f)
                        stations = [Station(**item) for item in data]
                        self._memory_cache = stations
                        self._last_updated = os.path.getmtime(self.file_path)
                        return stations
                except Exception as e:
                    logger.error(f"Error reading cache file: {e}")

        elif self.cache_type == "redis" and self.redis_client:
            try:
                raw_data = self.redis_client.get("neo_radio_stations")
                if raw_data:
                    data = json.loads(raw_data)
                    stations = [Station(**item) for item in data]
                    self._memory_cache = stations
                    self._last_updated = time.time()
                    return stations
            except Exception as e:
                logger.error(f"Error reading from Redis: {e}")

        return []

    def set_stations(self, stations: List[Station]) -> None:
        self._memory_cache = stations
        self._last_updated = time.time()

        raw_list = [s.model_dump() for s in stations]

        if self.cache_type == "file":
            os.makedirs(os.path.dirname(self.file_path), exist_ok=True)
            try:
                with open(self.file_path, "w", encoding="utf-8") as f:
                    json.dump(raw_list, f, ensure_ascii=False)
                logger.info(f"Saved {len(stations)} stations to file cache.")
            except Exception as e:
                logger.error(f"Failed to write file cache: {e}")

        elif self.cache_type == "redis" and self.redis_client:
            try:
                payload = json.dumps(raw_list, ensure_ascii=False)
                self.redis_client.setex("neo_radio_stations", self.ttl, payload)
                logger.info(f"Saved {len(stations)} stations to Redis cache.")
            except Exception as e:
                logger.error(f"Failed to write Redis cache: {e}")


cache_service = CacheService()

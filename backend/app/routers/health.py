import time
from fastapi import APIRouter
from ..config import settings
from ..services.cache import cache_service

router = APIRouter()
start_time = time.time()


@router.get("/")
async def root_info():
    return {
        "app_name": settings.APP_NAME,
        "version": settings.APP_VERSION,
        "status": "running",
        "docs": "/docs"
    }


@router.get("/health")
async def health_check():
    cache_valid = cache_service.is_cache_valid()
    stations_count = len(cache_service.get_stations()) if cache_valid else 0
    return {
        "status": "ok",
        "uptime_seconds": int(time.time() - start_time),
        "cache_valid": cache_valid,
        "cached_stations_count": stations_count
    }


@router.get("/stats")
async def get_stats():
    stations = cache_service.get_stations()
    countries = set(s.country for s in stations if s.country)
    languages = set()
    genres = set()

    for s in stations:
        for lang in s.language_list:
            languages.add(lang)
        for tag in s.tag_list:
            genres.add(tag)

    return {
        "total_stations": len(stations),
        "total_countries": len(countries),
        "total_languages": len(languages),
        "total_genres": len(genres)
    }

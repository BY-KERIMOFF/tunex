from typing import Optional, List
from fastapi import APIRouter, HTTPException, Header, Query
from ..models.station import Station
from ..services.radio_browser import radio_browser_service
from ..config import settings

router = APIRouter()


@router.get("/stations", response_model=List[Station])
async def get_stations(
    country: Optional[str] = None,
    genre: Optional[str] = None,
    language: Optional[str] = None,
    limit: Optional[int] = Query(100, ge=1, le=5000),
    offset: Optional[int] = Query(0, ge=0)
):
    stations = await radio_browser_service.fetch_all_stations()

    if country:
        c_list = [c.strip().lower() for c in country.split(",") if c.strip()]
        stations = [s for s in stations if s.country and s.country.lower() in c_list]

    if genre:
        g_list = [g.strip().lower() for g in genre.split(",") if g.strip()]
        stations = [s for s in stations if any(tag in g_list for tag in s.tag_list)]

    if language:
        l_list = [l.strip().lower() for l in language.split(",") if l.strip()]
        stations = [s for s in stations if any(lang in l_list for lang in s.language_list)]

    return stations[offset: offset + limit]


@router.get("/stations/{uuid}", response_model=Station)
async def get_station_by_uuid(uuid: str):
    stations = await radio_browser_service.fetch_all_stations()
    for s in stations:
        if s.stationuuid == uuid:
            return s
    raise HTTPException(status_code=404, detail="Station not found")


@router.get("/countries")
async def get_countries():
    stations = await radio_browser_service.fetch_all_stations()
    country_counts = {}
    for s in stations:
        if s.country:
            country_counts[s.country] = country_counts.get(s.country, 0) + 1
    sorted_countries = sorted(
        [{"name": k, "station_count": v} for k, v in country_counts.items()],
        key=lambda x: x["station_count"],
        reverse=True
    )
    return sorted_countries


@router.get("/genres")
async def get_genres():
    stations = await radio_browser_service.fetch_all_stations()
    genre_counts = {}
    for s in stations:
        for tag in s.tag_list:
            if tag:
                genre_counts[tag] = genre_counts.get(tag, 0) + 1
    sorted_genres = sorted(
        [{"name": k, "station_count": v} for k, v in genre_counts.items()],
        key=lambda x: x["station_count"],
        reverse=True
    )
    return sorted_genres[:200]


@router.get("/languages")
async def get_languages():
    stations = await radio_browser_service.fetch_all_stations()
    lang_counts = {}
    for s in stations:
        for lang in s.language_list:
            if lang:
                lang_counts[lang] = lang_counts.get(lang, 0) + 1
    sorted_langs = sorted(
        [{"name": k, "station_count": v} for k, v in lang_counts.items()],
        key=lambda x: x["station_count"],
        reverse=True
    )
    return sorted_langs[:100]


@router.post("/admin/refresh")
async def refresh_cache(x_admin_token: Optional[str] = Header(None)):
    if x_admin_token != settings.ADMIN_TOKEN:
        raise HTTPException(status_code=403, detail="Invalid admin token")
    stations = await radio_browser_service.fetch_all_stations(force_refresh=True)
    return {"message": "Cache refreshed successfully", "total_stations": len(stations)}

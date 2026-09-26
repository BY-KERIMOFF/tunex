from typing import Optional
from fastapi import APIRouter, Query, Response
from ..services.radio_browser import radio_browser_service
from ..services.m3u_builder import M3UBuilder

router = APIRouter()


@router.get("/m3u")
async def get_m3u(
    country: Optional[str] = Query(None, description="Country filter (comma-separated, e.g., Turkey,Azerbaijan)"),
    genre: Optional[str] = Query(None, description="Genre/tag filter (comma-separated, e.g., pop,rock)"),
    language: Optional[str] = Query(None, description="Language filter (comma-separated, e.g., english,turkish)"),
    minbitrate: Optional[int] = Query(0, description="Minimum bitrate"),
    limit: Optional[int] = Query(5000, description="Max stations count"),
    raw: Optional[int] = Query(0, description="Direct URL if 1, otherwise proxied")
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

    if minbitrate and minbitrate > 0:
        stations = [s for s in stations if (s.bitrate or 0) >= minbitrate]

    if limit and limit > 0:
        stations = stations[:limit]

    content = M3UBuilder.build_m3u(stations, raw=(raw == 1))
    return Response(content=content, media_type="audio/x-mpegurl", headers={
        "Content-Disposition": "attachment; filename=stations.m3u"
    })

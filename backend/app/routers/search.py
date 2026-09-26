from typing import List
from fastapi import APIRouter, Query
from ..models.station import Station
from ..services.radio_browser import radio_browser_service

router = APIRouter()


@router.get("/search", response_model=List[Station])
async def search_stations(
    q: str = Query(..., min_length=1, description="Search query string"),
    limit: int = Query(50, ge=1, le=500)
):
    query = q.lower().strip()
    stations = await radio_browser_service.fetch_all_stations()

    matched = []
    for s in stations:
        if (query in s.name.lower() or
            query in s.country.lower() or
            query in s.language.lower() or
            query in s.tags.lower()):
            matched.append(s)

        if len(matched) >= limit:
            break

    return matched
